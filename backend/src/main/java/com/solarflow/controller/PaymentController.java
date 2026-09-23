package com.solarflow.controller;

import java.util.List;
import java.util.Map;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.solarflow.model.Order;
import com.solarflow.model.Payment;
import com.solarflow.model.User;
import com.solarflow.repo.OrderRepository;
import com.solarflow.repo.PaymentRepository;
import com.solarflow.repo.UserRepository;
import com.solarflow.service.PaymentService;

@RestController
@RequestMapping("/api")
public class PaymentController {
    private final PaymentService paymentService;
    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;

    public PaymentController(PaymentService paymentService, PaymentRepository paymentRepository,
                            UserRepository userRepository, OrderRepository orderRepository) {
        this.paymentService = paymentService;
        this.paymentRepository = paymentRepository;
        this.userRepository = userRepository;
        this.orderRepository = orderRepository;
    }

    @PostMapping("/orders/{orderId}/payments")
    public Payment createPayment(@PathVariable Long orderId,
                                @RequestBody Map<String, Object> body,
                                Authentication authentication) {
        User user = currentUser(authentication);
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));

        if (!user.getId().equals(order.getUser().getId())) {
            throw new IllegalArgumentException("You are not allowed to manage this order's payment");
        }

        double amount = body.get("amount") == null ? order.getTotal() : ((Number) body.get("amount")).doubleValue();
        String method = body.getOrDefault("method", "ONLINE").toString();
        return paymentService.createPayment(orderId, method, amount);
    }

    @GetMapping("/payments")
    public List<Payment> listCustomerPayments(Authentication authentication) {
        User user = currentUser(authentication);
        return paymentRepository.findByUserOrderByCreatedAtDesc(user);
    }

    @PostMapping("/payments/{id}/verify")
    public Payment verifyPayment(@PathVariable Long id,
                                @RequestBody Map<String, Object> body,
                                Authentication authentication) {
        User user = currentUser(authentication);
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Payment not found"));

        if (!user.getId().equals(payment.getUser().getId())) {
            throw new IllegalArgumentException("Payment does not belong to this user");
        }

        String status = body.get("status") == null ? null : body.get("status").toString();
        String reference = body.get("reference") == null ? null : body.get("reference").toString();
        return paymentService.verifyPayment(id, status, reference);
    }

    private User currentUser(Authentication authentication) {
        return userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new IllegalArgumentException("Authenticated user not found"));
    }
}
