package org.example.Controller;
import jakarta.transaction.SystemException;
import org.example.Config.MyUserDetails;
import org.example.DTO.UserDTO;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/users")
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private UserDetailsServiceImpl userDetailsService;
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

    @GetMapping("/admin-users-list")
    public String listUsers(@RequestParam(required = false) String name,
                            @RequestParam(required = false) String lastname, Model model) {
        List<UserDTO> users;
        if ((name == null || name.isBlank()) && (lastname == null || lastname.isBlank()))
            users = userService.getAllUsers();
        else
            users = userService.findByNameAndLastname(name, lastname);
        model.addAttribute("users", users);
        model.addAttribute("searchTitle", name);
        model.addAttribute("searchLastname", lastname);
        return "admin-users-list";
    }

    @PostMapping("/update-role")
    public String updateUserRole(@RequestParam("userId") Long userId,
                                 @RequestParam(name = "userTypes") String[] userTypes) {
        userService.updateUserRole(userId, userTypes);
        return "redirect:/users/admin-users-list";
    }

    @GetMapping("delete/{id}")
    public String deleteUser(@PathVariable Long id, Principal principal, RedirectAttributes redirectAttributes) throws SystemException {
        Long currentUserId = getUserIdFromPrincipal(principal);

        if (currentUserId.equals(id)) { //защита от дурака. эта хрень работает но не обрабатывается
            redirectAttributes.addFlashAttribute("error", "Вы не можете удалить свой собственный аккаунт.");
            logger.error("Вы не можете удалить свой собственный аккаунт.");
            return "redirect:/users/admin-users-list";
        }
        userService.deleteUserById(id);
        return "redirect:/users/admin-users-list";
    }

    private Long getUserIdFromPrincipal(Principal principal) {
        MyUserDetails userDetails = (MyUserDetails) ((Authentication) principal).getPrincipal();
        return userDetails.getId();
    }
}