package com.solarflow.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.solarflow.model.Notification;
import com.solarflow.model.User;
import com.solarflow.repo.NotificationRepository;

@Service
public class NotificationService {
    private final NotificationRepository notifications;
    private final EmailProvider emailProvider;

    public NotificationService(NotificationRepository notifications, EmailProvider emailProvider) {
        this.notifications = notifications;
        this.emailProvider = emailProvider;
    }

    @Transactional
    public Notification createAndSend(User user, String title, String message) {
        Notification notification = new Notification();
        notification.setUser(user);
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setReadFlag(false);
        notifications.save(notification);

        try {
            if (user != null && user.getEmail() != null && !user.getEmail().isBlank()) {
                emailProvider.send(user.getEmail(), title, message);
            }
        } catch (Exception ignored) {
            // Email provider failures must not break the core notification persistence flow.
        }

        return notification;
    }
}
