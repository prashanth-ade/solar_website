package com.solarflow.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.solarflow.model.Order;
import com.solarflow.model.User;
import com.solarflow.repo.OrderRepository;
import com.solarflow.repo.PaymentRepository;
import com.solarflow.repo.QuoteRepository;
import com.solarflow.repo.UserRepository;

class AdminControllerTest {
    @Test
    void adminDashboardProvidesSummaryMetrics() {
        UserRepository userRepository = mock(UserRepository.class);
        OrderRepository orderRepository = mock(OrderRepository.class);
        PaymentRepository paymentRepository = mock(PaymentRepository.class);
        QuoteRepository quoteRepository = mock(QuoteRepository.class);

        User customer = new User();
        customer.setId(3L);
        customer.setName("Asha");
        customer.setEmail("asha@example.com");
        customer.setRole("CUSTOMER");

        Order order = new Order();
        order.setId(9L);
        order.setUser(customer);
        order.setStatus("PAID");
        order.setTotal(2450.0);

        when(userRepository.count()).thenReturn(1L);
        when(orderRepository.count()).thenReturn(1L);
        when(orderRepository.findAll()).thenReturn(List.of(order));
        when(paymentRepository.count()).thenReturn(1L);
        when(paymentRepository.countByStatus("PAID")).thenReturn(1L);
        when(quoteRepository.countByStatus("DRAFT")).thenReturn(2L);
        when(quoteRepository.count()).thenReturn(3L);

        AdminController controller = new AdminController(userRepository, orderRepository, paymentRepository, quoteRepository);
        Map<String, Object> dashboard = controller.dashboard();

        assertEquals(1L, dashboard.get("totalCustomers"));
        assertEquals(1L, dashboard.get("totalOrders"));
        assertEquals(1L, dashboard.get("paidPayments"));
    }
}
