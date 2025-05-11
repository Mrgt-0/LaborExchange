package org.example.Controller;

import org.example.Enum.ApplicationStatus;
import org.example.Model.Application;
import org.example.Model.Vacancy;
import org.example.Repository.VacancyRepository;
import org.example.Service.ApplicationService;
import org.example.Service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Collections;
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
                               Principal principal) {
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
        return "redirect:/employer/applications";
    }

    private Long getEmployerIdFromPrincipal(Principal principal) {
        String email = principal.getName();
        return userService.findIdByEmail(email);
    }
}