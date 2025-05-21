package org.example.Controller;
import jakarta.transaction.SystemException;
import jakarta.validation.Valid;
import org.example.DTO.UserDTO;
import org.example.Enum.UserTypeEnum;
import org.example.Model.User;
import org.example.Model.UserType;
import org.example.Service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/auth")
public class AuthController {
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    @Autowired
    private UserService userService;
    @Autowired
    private AuthenticationManager authenticationManager;

    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("user") UserDTO user,
                               BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) throws SystemException {
        if (bindingResult.hasErrors()) {
            logger.error("Ошибки валидации: {}", bindingResult.getAllErrors());
            return "register";
        }
        try {
            userService.registerUser(user);
        } catch (RuntimeException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "register";
        }
        redirectAttributes.addFlashAttribute("successMessage", "Регистрация прошла успешно! Пожалуйста, войдите.");
        return "redirect:/auth/login";
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping(value = "/login", produces = MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8")
    public String login(@RequestParam String email, @RequestParam String password, Model model) {
        if (email == null || email.isEmpty()) {
            logger.error("Email не указан!");
            model.addAttribute("errorMessage", "Неверное имя пользователя или пароль.");
            return "login";
        }
        if (password == null || password.isEmpty()) {
            logger.error("Пароль не был предоставлен!");
            model.addAttribute("errorMessage", "Пароль не может быть пустым");
            return "login";
        }
        try {
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));
            SecurityContextHolder.getContext().setAuthentication(authentication); // Устанавливаем аутентификацию
            logger.info("Пользователь с email {} успешно вошел в систему.", email);
            UserDTO user = userService.findByEmail(email);
            if (user != null && user.getUserTypes() != null) {
                for (UserType ut : user.getUserTypes())
                    logger.info("Роль пользователя: {}", ut.getType());

                boolean isAdmin = user.getUserTypes().stream()
                        .anyMatch(ut -> ut.getType() == UserTypeEnum.ADMIN);
                boolean isEmployer = user.getUserTypes().stream()
                        .anyMatch(ut -> ut.getType() == UserTypeEnum.EMPLOYER);

                logger.info("Пользователь является администратором: {}", isAdmin);
                logger.info("Пользователь является работодателем: {}", isEmployer);

                if (isAdmin)
                    return "redirect:/users/admin-profile";
                else if (isEmployer)
                    return "redirect:/users/employer-profile";
                else
                    return "redirect:/users/user-profile";

            } else {
                logger.error("Пользователь не найден или не имеет типов.");
                model.addAttribute("errorMessage", "Пользователь не найден.");
                return "login";
            }
        } catch (AuthenticationException e) {
            logger.error("Ошибка аутентификации: {}", e.getMessage());
            model.addAttribute("errorMessage", "Неверное имя пользователя или пароль.");
            return "login";
        }
    }

    @GetMapping("/login")
    public String showLoginForm(Model model) {
        logger.info("Пользователь открывает страницу логина.");
        model.addAttribute("user", new User());
        return "login";
    }
}