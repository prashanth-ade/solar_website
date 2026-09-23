package com.solarflow.service;

import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.solarflow.model.Order;
import com.solarflow.model.Payment;
import com.solarflow.repo.OrderRepository;
import com.solarflow.repo.PaymentRepository;

@Service
public class PaymentService {

    private static final Set<String> PAYMENT_STATUSES = Set.of(
            "INITIATED",
            "PENDING",
            "PAID",
            "FAILED",
            "CANCELLED"
    );

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final PaymentProvider paymentProvider;

    @Value("${app.payment.currency:INR}")
    private String defaultCurrency;

    public PaymentService(
            PaymentRepository paymentRepository,
            OrderRepository orderRepository,
            PaymentProvider paymentProvider) {

        this.paymentRepository = paymentRepository;
        this.orderRepository = orderRepository;
        this.paymentProvider = paymentProvider;
    }

    @Transactional
    public Payment createPayment(Long orderId, String method, double amount) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));

        String reference = "pay_" + orderId + "_" + System.currentTimeMillis();

        Payment payment = new Payment();

        payment.setOrder(order);
        payment.setUser(order.getUser());

        payment.setProvider(
                paymentProvider.providerName()
        );

        payment.setMethod(
                method == null || method.isBlank()
                        ? "ONLINE"
                        : method.trim().toUpperCase(Locale.ROOT)
        );

        payment.setReference(reference);
        payment.setAmount(amount);

        payment.setCurrency(
                defaultCurrency == null || defaultCurrency.isBlank()
                        ? "INR"
                        : defaultCurrency.trim().toUpperCase(Locale.ROOT)
        );

        payment.setStatus("INITIATED");

        paymentRepository.save(payment);

        PaymentResult result = paymentProvider.initiate(
                reference,
                amount,
                payment.getCurrency(),
                order.getUser().getEmail()
        );

        payment.setStatus(
                normalizeStatus(result.status())
        );

        payment.setReference(
                result.reference()
        );

        payment.setProvider(
                result.provider()
        );

        paymentRepository.save(payment);

        return payment;
    }

    @Transactional
    public Payment verifyPayment(
            Long paymentId,
            String verificationStatus,
            String providerReference) {

        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new IllegalArgumentException("Payment not found"));

        String reference =
                providerReference == null || providerReference.isBlank()
                        ? payment.getReference()
                        : providerReference;

        PaymentResult result = paymentProvider.verify(reference);

        String normalizedStatus = normalizeStatus(
                verificationStatus != null
                        ? verificationStatus
                        : result.status()
        );

        payment.setStatus(normalizedStatus);

        payment.setReference(
                result.reference()
        );

        payment.setProvider(
                result.provider()
        );

        if ("PAID".equalsIgnoreCase(normalizedStatus)) {

            Order order = payment.getOrder();

            order.setStatus("PAID");

            orderRepository.save(order);

        } else if (
                "FAILED".equalsIgnoreCase(normalizedStatus)
                        || "CANCELLED".equalsIgnoreCase(normalizedStatus)) {

            Order order = payment.getOrder();

            order.setStatus("FAILED");

            orderRepository.save(order);
        }

        return paymentRepository.save(payment);
    }

    public List<Payment> listAll() {
        return paymentRepository.findAllByOrderByCreatedAtDesc();
    }

    private String normalizeStatus(String value) {

        String normalized =
                value == null
                        ? "PENDING"
                        : value.trim().toUpperCase(Locale.ROOT);

        return PAYMENT_STATUSES.contains(normalized)
                ? normalized
                : "PENDING";
    }
}