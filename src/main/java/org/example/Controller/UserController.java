package org.example.Controller;

import jakarta.transaction.SystemException;
import org.example.DTO.UserDTO;
import org.example.Enum.UserType;
import org.example.Mapper.UserMapper;
import org.example.Model.User;
import org.example.Service.UserDetailsServiceImpl;
import org.example.Service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

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

    @GetMapping("/profile")
    public String showProfileForm(Model model) {
        logger.info("Форма профиля отображается.");
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        UserDTO user = userService.findByEmail(email);

        if (user != null) {
            model.addAttribute("user", user);
        } else {
            logger.warn("Пользователь не найден: {}", email);
            model.addAttribute("errorMessage", "Пользователь не найден.");
        }
        return "profile";
    }

    @PostMapping("/profile")
    public String updateProfile(@ModelAttribute UserDTO updatedUser) throws SystemException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        userService.updateUser(email, userMapper.toEntity(updatedUser));

        if (!email.equals(updatedUser.getEmail())) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(updatedUser.getEmail());
            Authentication newAuth = new UsernamePasswordAuthenticationToken(userDetails, userDetails.getPassword(), userDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(newAuth);
        }
        logger.info("Данные о пользователе обновлены.");
        return "redirect:/users/profile";
    }
}