package org.example.Controller;

import org.example.DTO.ApplicationViewDTO;
import org.example.DTO.UserDTO;
import org.example.Service.ApplicationService;
import org.example.Service.UserService;
import org.hibernate.StaleObjectStateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/users/applications")
public class ApplicationController {
    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private UserService userService;

    @GetMapping("/my-applications")
    public String myApplicationsPage(Principal principal, Model model) {
        Long userId = getUserIdFromPrincipal(principal);
        List<ApplicationViewDTO> applications = applicationService.getApplicationsByUser(userId);
        applications.forEach(app -> app.setStatus(applicationService.getStatusInRussian(app.getStatus())));
        model.addAttribute("applications", applications);
        return "my-applications";
    }

    private Long getUserIdFromPrincipal(Principal principal) {
        if (principal == null || principal.getName() == null)
            throw new IllegalStateException("Пользователь не аутентифицирован");

        String email = principal.getName();
        UserDTO user = userService.findByEmail(email);
        if (user == null)
            throw new IllegalStateException("Пользователь по email " + email + " не найден");

        return user.getId();
    }

    @PostMapping("/submit")
    public ResponseEntity<?> submit(@RequestParam Long vacancyId, Principal principal) {
        Long userId = getUserIdFromPrincipal(principal);
        try {
            applicationService.submitApplication(userId, applicationService.getVacancyById(vacancyId));
            return ResponseEntity.ok().body("Отклик отправлен");
        } catch (StaleObjectStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    @PostMapping("/{applicationId}/withdraw")
    public ResponseEntity<?> withdraw(@PathVariable Long applicationId, Principal principal) {
        Long userId = getUserIdFromPrincipal(principal);
        applicationService.withdrawApplication(applicationId, userId);
        return ResponseEntity.ok().body("Отклик отозван");
    }
}