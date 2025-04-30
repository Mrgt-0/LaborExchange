package org.example.Controller;

import jakarta.transaction.SystemException;
import org.example.DTO.UserDTO;
import org.example.Enum.UserTypeEnum;
import org.example.Mapper.UserMapper;
import org.example.Service.UserDetailsServiceImpl;
import org.example.Service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/users")
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    @Autowired
    private UserMapper userMapper;

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
        UserDTO user = userService.findUserById(id);
        if (user != null) {
            logger.info("Пользователь с id: {} найден: {}", id, user.getEmail());
            return ResponseEntity.ok(user);
        } else {
            logger.warn("Пользователь с id: {} не найден.", id);
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/employer-profile")
    public String showEmployerProfile(Model model) {
        logger.info("Форма профиля работодателя отображается.");

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        UserDTO user = userService.findByEmail(email);

        if (user != null) {
            model.addAttribute("user", user);
            logger.info("Пользователь с email: {} является работодателем.", email);
            return "employer-profile"; // имя вашего view для работодателя
        } else {
            logger.warn("Работодатель не найден: {}", email);
            model.addAttribute("errorMessage", "Работодатель не найден.");
            return "error"; // имя вашего view для ошибок
        }
    }

    // Метод для отображения профиля обычного пользователя
    @GetMapping("/user-profile")
    public String showUserProfile(Model model) {
        logger.info("Форма профиля пользователя отображается.");

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        UserDTO user = userService.findByEmail(email);

        if (user != null) {
            model.addAttribute("user", user);
            logger.info("Пользователь с email: {} найден.", email);
            return "user-profile";
        } else {
            logger.warn("Пользователь не найден: {}", email);
            model.addAttribute("errorMessage", "Пользователь не найден.");
            return "error";
        }
    }

    @PostMapping("/user-profile")
    public String updateProfile(@ModelAttribute UserDTO updatedUser) throws SystemException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        userService.updateUser(email, updatedUser);
        if (!email.equals(updatedUser.getEmail())) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(updatedUser.getEmail());
            Authentication newAuth = new UsernamePasswordAuthenticationToken(userDetails, userDetails.getPassword(), userDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(newAuth);
        }
        logger.info("Данные о пользователе обновлены.");
        return "redirect:/users/user-profile"; // Перенаправляем на страницу профиля после обновления
    }
}