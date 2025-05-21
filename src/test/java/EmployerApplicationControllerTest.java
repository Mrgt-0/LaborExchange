import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

import org.example.Controller.EmployerApplicationController;
import org.example.Enum.ApplicationStatus;
import org.example.Model.Application;
import org.example.Model.Vacancy;
import org.example.Service.ApplicationService;
import org.example.Service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

class EmployerApplicationControllerTest {

    @InjectMocks
    private EmployerApplicationController employerApplicationController;

    @Mock
    private ApplicationService applicationService;

    @Mock
    private UserService userService;

    @Mock
    private Model model;

    @Mock
    private RedirectAttributes redirectAttributes;

    @Mock
    private Principal principal;

    private List<Application> applications;
    private Application application;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        application = new Application();
        application.setId(1L);
        application.setStatus(ApplicationStatus.PENDING);
        application.setUserId(1L);
        Vacancy vacancy = new Vacancy();
        vacancy.setId(1L);
        application.setVacancy(vacancy);
        applications = new ArrayList<>();
        applications.add(application);
    }

    @Test
    void testListApplications() {
        when(principal.getName()).thenReturn("test@example.com");
        when(userService.findIdByEmail(anyString())).thenReturn(1L);
        when(applicationService.getApplicationsForEmployer(anyLong())).thenReturn(applications);
        String viewName = employerApplicationController.listApplications(model, principal);

        verify(model).addAttribute("applications", applications);
        assertEquals("employer-application-list", viewName);
    }

    @Test
    void testUpdateStatusSuccess() {
        when(principal.getName()).thenReturn("test@example.com");
        when(userService.findIdByEmail(anyString())).thenReturn(1L);
        when(applicationService.getApplicationsForEmployer(anyLong())).thenReturn(applications);
        String viewName = employerApplicationController.updateStatus(1L, "PENDING", principal, redirectAttributes, model);

        verify(redirectAttributes).addFlashAttribute("statusUpdate", "Статус отклика успешно обновлен.");
        verify(model).addAttribute("application", application);
        verify(model).addAttribute("applications", applications);
        assertEquals("redirect:/vacancies/1/applications", viewName);
    }

    @Test
    void testUpdateStatusApplicationNotFound() {
        when(principal.getName()).thenReturn("test@example.com");
        when(userService.findIdByEmail(anyString())).thenReturn(1L);
        when(applicationService.getApplicationsForEmployer(anyLong())).thenReturn(applications);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            employerApplicationController.updateStatus(2L, "PENDING", principal, redirectAttributes, model);
        });

        assertEquals("Отклик с ID 2 не найден для работодателя с ID 1", exception.getMessage());
    }

    @Test
    void testUpdateStatusInvalidStatus() {
        when(principal.getName()).thenReturn("test@example.com");
        when(userService.findIdByEmail(anyString())).thenReturn(1L);
        when(applicationService.getApplicationsForEmployer(anyLong())).thenReturn(applications);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            employerApplicationController.updateStatus(1L, "INVALID_STATUS", principal, redirectAttributes, model);
        });

        assertEquals("Недопустимый статус: INVALID_STATUS", exception.getMessage());
    }
}