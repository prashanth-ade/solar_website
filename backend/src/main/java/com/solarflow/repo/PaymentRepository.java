package com.solarflow.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.solarflow.model.Order;
import com.solarflow.model.Payment;
import com.solarflow.model.User;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findAllByOrderByCreatedAtDesc();
    List<Payment> findByUserOrderByCreatedAtDesc(User user);
    List<Payment> findByOrderOrderByCreatedAtDesc(Order order);
    long countByStatus(String status);
}
