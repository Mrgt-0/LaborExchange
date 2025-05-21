package org.example.Controller;
import jakarta.persistence.EntityNotFoundException;
import org.example.DTO.ApplicationViewDTO;
import org.example.DTO.UserDTO;
import org.example.Model.Application;
import org.example.Model.Vacancy;
import org.example.Service.ApplicationService;
import org.example.Service.ResumeService;
import org.example.Service.UserService;
import org.example.Service.VacancyService;
import org.hibernate.StaleObjectStateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/applications")
public class ApplicationController {
    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private UserService userService;
    @Autowired
    private VacancyService vacancyService;
    @Autowired
    private ResumeService resumeService;

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
            Vacancy vacancy = vacancyService.getVacancyById(vacancyId);

            if (!resumeService.userHasResume(userId))
                return ResponseEntity.badRequest().body("Вы не можете подать отклик, так как у вас нет резюме.");

            if (applicationService.hasExistingApplication(userId, vacancyId))
                return ResponseEntity.badRequest().body("Вы уже подали отклик на эту вакансию.");

            applicationService.submitApplication(userId, vacancy);
            return ResponseEntity.ok().body("Отклик отправлен");
        } catch (EntityNotFoundException | IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (StaleObjectStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    @PostMapping("/{applicationId}/withdraw")
    public String withdraw(@PathVariable Long applicationId, Principal principal, RedirectAttributes redirectAttributes) {
        Long userId = getUserIdFromPrincipal(principal);
        try {
            Application application = applicationService.getApplicationById(applicationId);
            if (application.getUserId().equals(userId)) {
                applicationService.withdrawApplication(applicationId, userId);
                redirectAttributes.addFlashAttribute("successMessage", "Отклик успешно отозван.");
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Вы не можете отозвать этот отклик.");
            }
        } catch (EntityNotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/applications/my-applications";
    }
}