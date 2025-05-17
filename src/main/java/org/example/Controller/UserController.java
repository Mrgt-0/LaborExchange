package org.example.Controller;
import jakarta.transaction.SystemException;
import org.example.DTO.UserDTO;
import org.example.DTO.UserTypeDTO;
import org.example.Mapper.UserMapper;
import org.example.Mapper.UserTypeMapper;
import org.example.Model.User;
import org.example.Model.UserType;
import org.example.Service.UserDetailsServiceImpl;
import org.example.Service.UserService;
import org.example.Service.UserTypeService;
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

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/users")
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private UserDetailsServiceImpl userDetailsService;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private UserTypeMapper userTypeMapper;
    @Autowired
    private UserTypeService userTypeService;
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

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
    // Метод для отображения профиля работодателя
    @GetMapping("/employer-profile")
    public String showEmployerProfile(Model model) {
        logger.info("Форма профиля работодателя отображается.");
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        UserDTO user = userService.findByEmail(email);

        if (user != null) {
            model.addAttribute("user", user);
            logger.info("Пользователь с email: {} является работодателем.", email);
            return "employer-profile";
        } else {
            logger.warn("Работодатель не найден: {}", email);
            model.addAttribute("errorMessage", "Работодатель не найден.");
            return "error";
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
    // Метод для отображения профиля администратора
    @GetMapping("/admin-profile")
    public String showAdminProfile(Model model) {
        logger.info("Форма профиля администратора отображается.");
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        UserDTO user = userService.findByEmail(email);
        if (user != null) {
            model.addAttribute("user", user);
            logger.info("Пользователь с email: {} найден.", email);
            return "admin-profile";
        } else {
            logger.warn("Пользователь не найден: {}", email);
            model.addAttribute("errorMessage", "Пользователь не найден.");
            return "error";
        }
    }

    @PostMapping("/update-profile")
    public String updateUserProfile(@ModelAttribute UserDTO updatedUser, @RequestParam String userTypes) throws SystemException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        userService.updateUser(email, updatedUser);
        if (!email.equals(updatedUser.getEmail())) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(updatedUser.getEmail());
            Authentication newAuth = new UsernamePasswordAuthenticationToken(userDetails, userDetails.getPassword(), userDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(newAuth);
        }
        logger.info("Данные о пользователе обновлены для роли: {}", userTypes);
        return switch (userTypes) {
            case "EMPLOYER" -> "redirect:/users/employer-profile";
            case "ADMIN" -> "redirect:/users/admin-profile";
            default -> "redirect:/users/user-profile";
        };
    }

    @GetMapping("admin-users-list")
    public String listUsers(Model model) {
        List<UserDTO> users = userService.getAllUsers();
        model.addAttribute("users", users);
        List<UserTypeDTO> availableUserTypes = userTypeService.findAll();
        model.addAttribute("availableUserTypes", availableUserTypes);
        return "admin-users-list";
    }

    @PostMapping("/update-role")
    public String updateUserRole(@RequestParam("userId") Long userId,
                                 @RequestParam(name = "userTypes") String[] userTypes) {
        userService.updateUserRole(userId, userTypes);
        return "redirect:/users/admin-users-list";
    }

    @GetMapping("delete/{id}")
    public String deleteUser(@PathVariable Long id) {
        userService.deleteUserById(id);
        return "redirect:/users/admin-users-list";
    }
}