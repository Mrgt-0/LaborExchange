import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.example.Controller.ApplicationController;
import org.example.DTO.UserDTO;
import org.example.Model.Application;
import org.example.Service.ApplicationService;
import org.example.Service.ResumeService;
import org.example.Service.UserService;
import org.example.Service.VacancyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;

import java.security.Principal;
import java.util.Collections;

public class ApplicationControllerTest {
    @InjectMocks
    private ApplicationController applicationController;

    @Mock
    private ApplicationService applicationService;

    @Mock
    private UserService userService;

    @Mock
    private VacancyService vacancyService;

    @Mock
    private ResumeService resumeService;

    @Mock
    private Principal principal;

    @Mock
    private Model model;

    private UserDTO userDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userDTO = new UserDTO();
        userDTO.setId(1L);
        userDTO.setEmail("test@example.com");
    }

    @Test
    void testMyApplicationsPage() {
        when(principal.getName()).thenReturn(userDTO.getEmail());
        when(userService.findByEmail(userDTO.getEmail())).thenReturn(userDTO);
        when(applicationService.getApplicationsByUser(userDTO.getId())).thenReturn(Collections.emptyList());
        String viewName = applicationController.myApplicationsPage(principal, model);
        verify(applicationService).getApplicationsByUser(userDTO.getId());
        verify(model).addAttribute("applications", Collections.emptyList());
        assertEquals("my-applications", viewName);
    }

    @Test
    void testSubmitApplicationWithoutResume() {
        when(principal.getName()).thenReturn(userDTO.getEmail());
        when(userService.findByEmail(userDTO.getEmail())).thenReturn(userDTO);
        when(resumeService.userHasResume(userDTO.getId())).thenReturn(false);
        ResponseEntity<?> response = applicationController.submit(1L, principal);
        assertEquals(ResponseEntity.badRequest().body("Вы не можете подать отклик, так как у вас нет резюме."), response);
    }

    @Test
    void testWithdrawApplication() {
        Long applicationId = 1L;
        when(principal.getName()).thenReturn(userDTO.getEmail());
        when(userService.findByEmail(userDTO.getEmail())).thenReturn(userDTO);
        Application application = new Application();
        application.setUserId(userDTO.getId());
        when(applicationService.getApplicationById(applicationId)).thenReturn(application);
        doNothing().when(applicationService).withdrawApplication(applicationId, userDTO.getId());
        String viewName = applicationController.withdraw(applicationId, principal, new RedirectAttributesModelMap());
        verify(applicationService).getApplicationById(applicationId);
        verify(applicationService).withdrawApplication(applicationId, userDTO.getId());
        assertEquals("redirect:/applications/my-applications", viewName);
    }

    @Test
    void testWithdrawApplicationNotOwned() {
        Long applicationId = 1L;
        when(principal.getName()).thenReturn(userDTO.getEmail());
        when(userService.findByEmail(userDTO.getEmail())).thenReturn(userDTO);
        Application application = new Application();
        application.setUserId(2L);
        when(applicationService.getApplicationById(applicationId)).thenReturn(application);

        RedirectAttributes redirectAttributes = new RedirectAttributesModelMap();
        String viewName = applicationController.withdraw(applicationId, principal, redirectAttributes);

        verify(applicationService).getApplicationById(applicationId);
        assertEquals("redirect:/applications/my-applications", viewName);
        assertEquals("Вы не можете отозвать этот отклик.", redirectAttributes.getFlashAttributes().get("errorMessage"));
    }
}