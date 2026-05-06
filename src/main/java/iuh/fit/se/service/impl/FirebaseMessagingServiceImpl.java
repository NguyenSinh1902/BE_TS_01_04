package iuh.fit.se.service.impl;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import iuh.fit.se.service.FirebaseMessagingService;
import org.springframework.stereotype.Service;

@Service
public class FirebaseMessagingServiceImpl implements FirebaseMessagingService {

    @Override
    public void sendNotification(String token, String title, String body) {
        if (token == null || token.isEmpty()) return;

        Message message = Message.builder()
                .setToken(token)
                .setNotification(Notification.builder()
                        .setTitle(title)
                        .setBody(body)
                        .build())
                .build();

        try {
            FirebaseMessaging.getInstance().send(message);
        } catch (FirebaseMessagingException e) {
            System.err.println("Lỗi gửi thông báo FCM: " + e.getMessage());
        }
    }

    // Gửi cho một nhóm người (ví dụ: tất cả Quản lý)
    @Override
    public void sendNotificationToTopic(String topic, String title, String body) {
        Message message = Message.builder()
                .setTopic(topic)
                .setNotification(Notification.builder()
                        .setTitle(title)
                        .setBody(body)
                        .build())
                .build();
        try {
            FirebaseMessaging.getInstance().send(message);
        } catch (FirebaseMessagingException e) {
            System.err.println("Lỗi gửi thông báo Topic: " + e.getMessage());
        }
    }
}
