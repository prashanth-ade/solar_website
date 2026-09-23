package com.solarflow.repo;

import com.solarflow.model.Review;
import com.solarflow.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByProduct(Product product);
}
