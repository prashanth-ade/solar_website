package com.solarflow.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;

import com.solarflow.model.Order;
import com.solarflow.model.Payment;
import com.solarflow.model.User;
import com.solarflow.repo.OrderRepository;
import com.solarflow.repo.PaymentRepository;

class PaymentServiceTest {
    @Test
    void createPaymentCreatesRecordAndNormalizesStatus() {
        PaymentRepository paymentRepository = mock(PaymentRepository.class);
        OrderRepository orderRepository = mock(OrderRepository.class);
        PaymentProvider paymentProvider = mock(PaymentProvider.class);

        User user = new User();
        user.setId(7L);
        user.setEmail("sam@example.com");

        Order order = new Order();
        order.setId(12L);
        order.setUser(user);
        order.setStatus("PENDING");
        order.setTotal(1850.0);

        when(orderRepository.findById(12L)).thenReturn(Optional.of(order));
        when(paymentProvider.providerName()).thenReturn("stub");
        when(paymentProvider.initiate(anyString(), anyDouble(), anyString(), anyString()))
                .thenReturn(new PaymentResult("pay_12_123", "INITIATED", "stub", "Payment initiated"));
        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        PaymentService service = new PaymentService(paymentRepository, orderRepository, paymentProvider);
        Payment payment = service.createPayment(12L, "ONLINE", 1850.0);

        assertEquals("INITIATED", payment.getStatus());
        assertEquals("pay_12_123", payment.getReference());
        assertEquals("stub", payment.getProvider());
    }
}
