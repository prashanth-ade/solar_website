package com.solarflow.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;

import com.solarflow.model.Order;
import com.solarflow.model.Payment;
import com.solarflow.model.User;
import com.solarflow.repo.OrderRepository;
import com.solarflow.repo.PaymentRepository;
import com.solarflow.repo.QuoteRepository;
import com.solarflow.repo.CalculatorRequestRecordRepository;
import com.solarflow.repo.SolarServiceRepository;
import com.solarflow.repo.UserRepository;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
    private static final Set<String> ORDER_STATUSES = Set.of("PENDING", "PAID", "PROCESSING", "SHIPPED", "DELIVERED", "FAILED", "CANCELLED");
    private static final Set<String> PAYMENT_STATUSES = Set.of("INITIATED", "PENDING", "PAID", "FAILED", "CANCELLED");

    private final UserRepository users;
    private final OrderRepository orders;
    private final PaymentRepository payments;
    private final QuoteRepository quotes;
    private final SolarServiceRepository services;
    private final CalculatorRequestRecordRepository calculatorRequests;

    @Autowired
    public AdminController(UserRepository users, OrderRepository orders, PaymentRepository payments, QuoteRepository quotes,
                           SolarServiceRepository services, CalculatorRequestRecordRepository calculatorRequests) {
        this.users = users;
        this.orders = orders;
        this.payments = payments;
        this.quotes = quotes;
        this.services = services;
        this.calculatorRequests = calculatorRequests;
    }

    public AdminController(UserRepository users, OrderRepository orders, PaymentRepository payments, QuoteRepository quotes) {
        this(users, orders, payments, quotes, null, null);
    }

    @GetMapping("/dashboard")
    public Map<String, Object> dashboard() {
        Map<String, Object> summary = new HashMap<>();
        summary.put("totalCustomers", users.count());
        summary.put("totalOrders", orders.count());
        summary.put("totalRevenue", orders.findAll().stream().mapToDouble(Order::getTotal).sum());
        summary.put("totalPayments", payments.count());
        summary.put("paidPayments", payments.countByStatus("PAID"));
        summary.put("pendingQuotes", quotes.countByStatus("DRAFT"));
        summary.put("totalQuotes", quotes.count());
        summary.put("totalServices", services == null ? 0 : services.count());
        summary.put("calculatorRequests", calculatorRequests == null ? 0 : calculatorRequests.count());
        return summary;
    }

    @GetMapping("/customers")
    public List<Map<String, Object>> customers() {
        return users.findAll().stream().map(this::customerSummary).toList();
    }

    @GetMapping("/customers/{id}")
    public Map<String, Object> customer(@PathVariable Long id) {
        User user = users.findById(id).orElseThrow(() -> new IllegalArgumentException("Customer not found"));
        return customerSummary(user);
    }

    @GetMapping("/orders")
    public List<Map<String, Object>> orders() {
        return orders.findAllByOrderByCreatedAtDesc().stream().map(this::orderSummary).toList();
    }

    @GetMapping("/orders/{id}")
    public Map<String, Object> order(@PathVariable Long id) {
        Order order = orders.findById(id).orElseThrow(() -> new IllegalArgumentException("Order not found"));
        return orderSummary(order);
    }

    @PatchMapping("/orders/{id}/status")
    public Order updateOrderStatus(@PathVariable Long id, @RequestParam String status) {
        String normalized = normalizeStatus(status, ORDER_STATUSES);
        Order order = orders.findById(id).orElseThrow(() -> new IllegalArgumentException("Order not found"));
        order.setStatus(normalized);
        return orders.save(order);
    }

    @GetMapping("/payments")
    public List<Map<String, Object>> payments() {
        return payments.findAllByOrderByCreatedAtDesc().stream().map(this::paymentSummary).toList();
    }

    @GetMapping("/payments/{id}")
    public Map<String, Object> payment(@PathVariable Long id) {
        Payment payment = payments.findById(id).orElseThrow(() -> new IllegalArgumentException("Payment not found"));
        return paymentSummary(payment);
    }

    @PatchMapping("/payments/{id}/status")
    public Payment updatePaymentStatus(@PathVariable Long id, @RequestParam String status) {
        String normalized = normalizeStatus(status, PAYMENT_STATUSES);
        Payment payment = payments.findById(id).orElseThrow(() -> new IllegalArgumentException("Payment not found"));
        payment.setStatus(normalized);
        return payments.save(payment);
    }

    @GetMapping("/reports")
    public Map<String, Object> reports() {
        List<Order> allOrders = orders.findAll();
        List<Payment> allPayments = payments.findAll();
        double revenue = allOrders.stream().mapToDouble(Order::getTotal).sum();

        Map<String, Long> ordersByStatus = new HashMap<>();
        for (String status : ORDER_STATUSES) {
            ordersByStatus.put(status, allOrders.stream().filter(order -> status.equalsIgnoreCase(order.getStatus())).count());
        }

        Map<String, Long> paymentsByStatus = new HashMap<>();
        for (String status : PAYMENT_STATUSES) {
            paymentsByStatus.put(status, allPayments.stream().filter(payment -> status.equalsIgnoreCase(payment.getStatus())).count());
        }

        Map<String, Object> report = new HashMap<>();
        report.put("revenue", revenue);
        report.put("ordersByStatus", ordersByStatus);
        report.put("paymentsByStatus", paymentsByStatus);
        report.put("customersByRole", users.findAll().stream().collect(java.util.stream.Collectors.groupingBy(User::getRole, java.util.stream.Collectors.counting())));
        report.put("monthlyRevenue", buildMonthlyRevenue(allOrders));
        report.put("totalServices", services == null ? 0 : services.count());
        report.put("calculatorRequests", calculatorRequests == null ? 0 : calculatorRequests.count());
        return report;
    }

    private Map<String, Object> customerSummary(User user) {
        Map<String, Object> summary = new HashMap<>();
        summary.put("id", user.getId());
        summary.put("name", user.getName());
        summary.put("email", user.getEmail());
        summary.put("role", user.getRole());
        List<Order> customerOrders = orders.findAll().stream().filter(order -> order.getUser() != null && user.getId().equals(order.getUser().getId())).toList();
        double totalSpent = customerOrders.stream().mapToDouble(Order::getTotal).sum();
        Order latestOrder = customerOrders.stream().max((a, b) -> a.getCreatedAt().compareTo(b.getCreatedAt())).orElse(null);
        summary.put("orderCount", customerOrders.size());
        summary.put("totalSpent", totalSpent);
        summary.put("lastOrderAt", latestOrder != null ? latestOrder.getCreatedAt() : null);
        summary.put("lastOrderStatus", latestOrder != null ? latestOrder.getStatus() : null);
        return summary;
    }

    private Map<String, Object> orderSummary(Order order) {
        Map<String, Object> summary = new HashMap<>();
        summary.put("id", order.getId());
        summary.put("status", order.getStatus());
        summary.put("total", order.getTotal());
        summary.put("customerName", order.getUser() != null ? order.getUser().getName() : null);
        summary.put("customerEmail", order.getUser() != null ? order.getUser().getEmail() : null);
        summary.put("customerId", order.getUser() != null ? order.getUser().getId() : null);
        summary.put("createdAt", order.getCreatedAt());
        return summary;
    }

    private Map<String, Object> paymentSummary(Payment payment) {
        Map<String, Object> summary = new HashMap<>();
        summary.put("id", payment.getId());
        summary.put("orderId", payment.getOrder() != null ? payment.getOrder().getId() : null);
        summary.put("status", payment.getStatus());
        summary.put("amount", payment.getAmount());
        summary.put("currency", payment.getCurrency());
        summary.put("method", payment.getMethod());
        summary.put("provider", payment.getProvider());
        summary.put("reference", payment.getReference());
        summary.put("customerName", payment.getUser() != null ? payment.getUser().getName() : null);
        summary.put("customerEmail", payment.getUser() != null ? payment.getUser().getEmail() : null);
        summary.put("createdAt", payment.getCreatedAt());
        return summary;
    }

    private List<Map<String, Object>> buildMonthlyRevenue(List<Order> orders) {
        Map<String, Double> totals = new HashMap<>();
        for (Order order : orders) {
            if (order.getCreatedAt() == null) {
                continue;
            }
            String month = order.getCreatedAt().toLocalDate().withDayOfMonth(1).toString();
            totals.merge(month, order.getTotal(), Double::sum);
        }

        List<Map<String, Object>> monthlyRevenue = new ArrayList<>();
        totals.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(entry -> {
                    Map<String, Object> row = new HashMap<>();
                    row.put("month", entry.getKey());
                    row.put("revenue", entry.getValue());
                    monthlyRevenue.add(row);
                });
        return monthlyRevenue;
    }

    private String normalizeStatus(String status, Set<String> validStatuses) {
        String normalized = status == null ? "PENDING" : status.trim().toUpperCase(Locale.ROOT);
        if (!validStatuses.contains(normalized)) {
            throw new IllegalArgumentException("Invalid status: " + status);
        }
        return normalized;
    }
}
