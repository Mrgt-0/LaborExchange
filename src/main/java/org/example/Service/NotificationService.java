package org.example.Service;
import org.example.DTO.NotificationDTO;
import org.example.DTO.UserDTO;
import org.example.DTO.VacancyDTO;
import org.example.Mapper.NotificationMapper;
import org.example.Mapper.UserMapper;
import org.example.Model.Notification;
import org.example.Model.User;
import org.example.Model.Vacancy;
import org.example.Repository.NotificationRepository;
import org.example.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificationService {
    @Autowired
    private NotificationRepository notificationRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private NotificationMapper notificationMapper;
    @Autowired
    private UserMapper userMapper;

    public void createAndSendNotification(Long userId, String message, Long vacancyId) {
        NotificationDTO notification = new NotificationDTO();
        notification.setMessage(message);
        notification.setUserId(userId);
        notification.setVacancyId(vacancyId);
        notification.setCreatedDate(LocalDateTime.now());
        notificationRepository.save(notificationMapper.toEntity(notification));
    }

    public void notifyAllUsersAboutNewVacancy(VacancyDTO vacancy, String message) {
        List<UserDTO> allUsers = userRepository.findAll().stream().map(userMapper::toDTO).toList();
        for (UserDTO user : allUsers) {
            createAndSendNotification(user.getId(), message, vacancy.getId());
        }
    }

    public List<Notification> getUserNotifications(Long userId) {
        return notificationRepository.findAllByUserId(userId);
    }
}