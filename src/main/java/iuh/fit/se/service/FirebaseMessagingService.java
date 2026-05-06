package iuh.fit.se.service;

public interface FirebaseMessagingService {
    void sendNotification(String token, String title, String body);
    void sendNotificationToTopic(String topic, String title, String body);
}
