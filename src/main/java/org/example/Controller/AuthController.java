package org.example.Controller;

import jakarta.transaction.SystemException;
import jakarta.validation.Valid;
import org.example.DTO.UserDTO;
import org.example.Enum.UserType;
import org.example.Model.User;
import org.example.Service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
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
        logger.info("Email: {}", email);
        logger.info("Пароль: {}", password);

        if (email == null || email.isEmpty()) {
            logger.error("Email не указано!");
            model.addAttribute("errorMessage", "Неверное имя пользователя или пароль.");
            return "login";
        }

        if (password == null || password.isEmpty()) {
            logger.error("Пароль не был предоставлен!");
            model.addAttribute("errorMessage", "Пароль не может быть пустым");
            return "login";
        }

        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));
        logger.info("Пользователь с email {} успешно вошел в систему.", email);
        ResponseEntity.ok("Аутентификация успешна!");
        return "redirect:/users/profile";
    }

    @GetMapping("/login")
    public String showLoginForm(Model model) {
        logger.info("Пользователь открывает страницу логина.");
        model.addAttribute("user", new User());
        return "login";
    }
}