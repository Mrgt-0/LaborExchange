package org.example.Controller;

import org.example.Config.MyUserDetails;
import org.example.Model.Notification;
import org.example.Service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class NotificationPageController {

    @Autowired
    private NotificationService notificationService;

    @GetMapping("/notifications")
    public String notificationsPage(@AuthenticationPrincipal MyUserDetails userDetails, Model model) {
        Long userId = userDetails.getId(); // Получаем идентификатор пользователя
        List<Notification> notifications = notificationService.getUserNotifications(userId);

        // Добавляем уведомления в модель для передачи в шаблон
        model.addAttribute("notifications", notifications);
        return "notifications"; // Возвращаем имя шаблона (например, notifications.html)
    }
}