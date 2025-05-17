package org.example.Controller;
import org.example.DTO.ApplicationDTO;
import org.example.Enum.ApplicationStatus;
import org.example.Mapper.ApplicationMapper;
import org.example.Model.Application;
import org.example.Model.Vacancy;
import org.example.Service.ApplicationService;
import org.example.Service.UserService;
import org.example.Service.VacancyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/employer/applications")
public class EmployerApplicationController {
    private static final Logger logger = LoggerFactory.getLogger(EmployerApplicationController.class);
    @Autowired
    private ApplicationService applicationService;
    @Autowired
    private UserService userService;

    @GetMapping
    public String listApplications(Model model, Principal principal) {
        Long employerId = getEmployerIdFromPrincipal(principal);
        List<Application> applications = applicationService.getApplicationsForEmployer(employerId).stream()
                .filter(app -> app.getStatus() != ApplicationStatus.WITHDRAWN)
                .collect(Collectors.toList());

        model.addAttribute("applications", applications);
        return "employer-application-list";
    }

    @PostMapping("/{applicationId}/status")
    public String updateStatus(@PathVariable Long applicationId,
                               @RequestParam("status") String status,
                               Principal principal,
                               RedirectAttributes redirectAttributes,
                               Model model) {
        Long employerId = getEmployerIdFromPrincipal(principal);
        List<Application> applications = applicationService.getApplicationsForEmployer(employerId);
        logger.info("Отклики для работодателя с ID {}: {}", employerId, applications.stream()
                .map(a -> "ID: " + a.getId() + ", Employer ID: " + a.getUserId())
                .collect(Collectors.joining(", ")));

        Application app = applications.stream()
                .filter(a -> a.getId() == applicationId)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Отклик с ID " + applicationId + " не найден для работодателя с ID " + employerId));
        ApplicationStatus applicationStatus;
        try {
            applicationStatus = ApplicationStatus.valueOf(status);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Недопустимый статус: " + status);
        }
        applicationService.updateApplicationStatus(applicationId, applicationStatus);
        Long vacancyId = app.getVacancy().getId();
        redirectAttributes.addFlashAttribute("statusUpdate", "Статус отклика успешно обновлен.");
        model.addAttribute("application", app);
        model.addAttribute("applications", applications);
        return "redirect:/vacancies/" + vacancyId + "/applications";
    }

    private Long getEmployerIdFromPrincipal(Principal principal) {
        String email = principal.getName();
        return userService.findIdByEmail(email);
    }
}