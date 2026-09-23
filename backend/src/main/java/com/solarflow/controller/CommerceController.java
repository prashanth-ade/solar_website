package com.solarflow.controller;

import com.solarflow.dto.AddressRequest;
import com.solarflow.model.Address;
import com.solarflow.model.CartItem;
import com.solarflow.model.Order;
import com.solarflow.model.Product;
import com.solarflow.model.Review;
import com.solarflow.model.User;
import com.solarflow.repo.AddressRepository;
import com.solarflow.repo.CartItemRepository;
import com.solarflow.repo.NotificationRepository;
import com.solarflow.repo.OrderRepository;
import com.solarflow.repo.ProductRepository;
import com.solarflow.repo.ReviewRepository;
import com.solarflow.repo.UserRepository;
import com.solarflow.service.NotificationService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class CommerceController {
    private final UserRepository users;
    private final ProductRepository products;
    private final CartItemRepository carts;
    private final OrderRepository orders;
    private final AddressRepository addresses;
    private final ReviewRepository reviews;
    private final NotificationRepository notifications;
    private final NotificationService notificationService;

    CommerceController(UserRepository users,
                       ProductRepository products,
                       CartItemRepository carts,
                       OrderRepository orders,
                       AddressRepository addresses,
                       ReviewRepository reviews,
                       NotificationRepository notifications,
                       NotificationService notificationService) {
        this.users = users;
        this.products = products;
        this.carts = carts;
        this.orders = orders;
        this.addresses = addresses;
        this.reviews = reviews;
        this.notifications = notifications;
        this.notificationService = notificationService;
    }

    private User user(Authentication authentication) {
        return users.findByEmail(authentication.getName())
                .orElseThrow(() -> new IllegalArgumentException("Authenticated user not found"));
    }

    @GetMapping("/cart")
    Object cart(Authentication authentication) {
        return carts.findByUser(user(authentication));
    }

    @PostMapping("/cart/items")
    @Transactional
    Object addCart(@RequestBody Map<String, Object> body, Authentication authentication) {
        var item = new CartItem();
        item.user = user(authentication);
        item.product = products.findById(((Number) body.get("productId")).longValue())
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));
        item.quantity = ((Number) body.getOrDefault("quantity", 1)).intValue();
        return carts.save(item);
    }

    @GetMapping("/orders")
    Object orders(Authentication authentication) {
        return orders.findByUserOrderByCreatedAtDesc(user(authentication));
    }

    @PostMapping("/orders")
    @Transactional
    Object order(@RequestBody Map<String, Object> body, Authentication authentication) {
        var order = new Order();
        order.user = user(authentication);
        order.total = ((Number) body.getOrDefault("total", 0)).doubleValue();
        order.status = "PENDING";
        Order saved = orders.save(order);
        notificationService.createAndSend(order.user, "Order created", "Your solar order has been received and is being processed.");
        return saved;
    }

    @GetMapping("/addresses")
    Object addresses(Authentication authentication) {
        return addresses.findByUser(user(authentication));
    }

    @PostMapping("/addresses")
    Object address(@Valid @RequestBody AddressRequest body, Authentication authentication) {
        var address = new Address();
        address.user = user(authentication);
        address.label = body.label();
        address.line1 = body.line1();
        address.city = body.city();
        address.state = body.state();
        address.postalCode = body.postalCode();
        address.country = body.country();
        return addresses.save(address);
    }

    @GetMapping("/notifications")
    Object notifications(Authentication authentication) {
        return notifications.findByUserOrderByCreatedAtDesc(user(authentication));
    }

    @GetMapping("/products/{id}/reviews")
    Object productReviews(@PathVariable Long id) {
        return reviews.findByProduct(products.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Product not found")));
    }

    @PostMapping("/products/{id}/reviews")
    Object review(@PathVariable Long id, @RequestBody Review body, Authentication authentication) {
        body.user = user(authentication);
        body.product = products.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));
        return reviews.save(body);
    }
}

