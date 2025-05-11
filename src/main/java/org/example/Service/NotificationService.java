package org.example.Service;

import org.example.Enum.ReadStatus;
import org.example.Model.Notification;
import org.example.Model.Vacancy;
import org.example.Repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationService {
    @Autowired
    private NotificationRepository notificationRepository;

    // Создать и "отправить" уведомление
    public Notification createAndSendApplicationNotification(Long userId, String message, Vacancy vacancy) {
        Notification notification = new Notification();
        notification.setMessage(message);
        notification.setUserId(userId);
        notification.setVacancyId(vacancy.getId());
        notification.setVacancyTitle(vacancy.getTitle());

        return notificationRepository.save(notification);
    }

    public Notification createAndSendVacancyNotification(Long userId, String message, Vacancy vacancy) {
        Notification notification = new Notification();
        notification.setMessage(message);
        notification.setUserId(userId);
        notification.setVacancyId(vacancy.getId());
        notification.setVacancyTitle(vacancy.getTitle());

        return notificationRepository.save(notification);
    }

    // Получить все уведомления пользователя
    public List<Notification> getUserNotifications(Long userId) {
        return notificationRepository.findAllByUserId(userId);
    }
}
