import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import jakarta.transaction.SystemException;
import org.example.Controller.ResumeController;
import org.example.DTO.ResumeDTO;
import org.example.DTO.UserDTO;
import org.example.Service.ResumeService;
import org.example.Service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

class ResumeControllerTest {
    @InjectMocks
    private ResumeController resumeController;

    @Mock
    private UserService userService;

    @Mock
    private ResumeService resumeService;

    @Mock
    private Model model;

    @Mock
    private Authentication authentication;

    @Mock
    private Principal principal;
    private UserDTO user;
    private ResumeDTO resume;
    private List<ResumeDTO> resumes;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user = new UserDTO();
        user.setId(1L);
        user.setEmail("test@example.com");
        resumes = new ArrayList<>();
        resumes.add(new ResumeDTO());
    }

    @Test
    void testShowResumeListUserFound() throws SystemException {
        when(principal.getName()).thenReturn(user.getEmail());
        when(userService.findByEmail(user.getEmail())).thenReturn(user);
        when(resumeService.findResumesByUserId(user.getId())).thenReturn(resumes);
        String viewName = resumeController.showResumeList(model, principal);
        verify(model).addAttribute("user", user);
        verify(model).addAttribute("resumes", resumes);
        assertEquals("resume-list", viewName);
    }

    @Test
    void testShowResumeListUserNotFound() throws SystemException {
        when(principal.getName()).thenReturn("unknown@example.com");
        when(userService.findByEmail("unknown@example.com")).thenReturn(null);
        String viewName = resumeController.showResumeList(model, principal);
        verify(model, never()).addAttribute(any(), any());
        assertEquals("error", viewName);
    }

    @Test
    void testShowAddResumeFormUserFound() {
        when(principal.getName()).thenReturn(user.getEmail());
        when(userService.findByEmail(user.getEmail())).thenReturn(user);
        String viewName = resumeController.showAddResumeForm(model, principal);
        verify(model).addAttribute("user", user);
        assertEquals("resume-add", viewName);
    }

    @Test
    void testShowAddResumeFormUserNotFound() {
        when(principal.getName()).thenReturn("unknown@example.com");
        when(userService.findByEmail("unknown@example.com")).thenReturn(null);
        String viewName = resumeController.showAddResumeForm(model, principal);
        verify(model, never()).addAttribute(any(), any());
        assertEquals("error", viewName);
    }

    @Test
    void testAddResume() {
        Long userId = user.getId();
        String title = "Java Developer";
        String skills = "Java, Spring";
        String experience = "2 years";
        String education = "Bachelor's";
        String viewName = resumeController.addResume(userId, title, skills, experience, education);
        verify(resumeService).upload(userId, title, skills, experience, education);
        assertEquals("redirect:/resumes/resume-list", viewName);
    }

    @Test
    void testShowEditResumeFormUserFound() throws SystemException {
        Long resumeId = 1L;
        when(principal.getName()).thenReturn(user.getEmail());
        when(userService.findByEmail(user.getEmail())).thenReturn(user);
        ResumeDTO resume = new ResumeDTO();
        resume.setId(resumeId);
        resume.setUserId(user.getId());
        when(resumeService.findResumeById(resumeId)).thenReturn(resume);
        String viewName = resumeController.showEditResumeForm(resumeId, model, principal);
        verify(model).addAttribute("user", user);
        verify(model).addAttribute("resume", resume);
        assertEquals("resume-edit", viewName);
    }

    @Test
    void testShowEditResumeFormUserNotFound() throws SystemException {
        Long resumeId = 1L;
        when(principal.getName()).thenReturn("unknown@example.com");
        when(userService.findByEmail("unknown@example.com")).thenReturn(null);
        String viewName = resumeController.showEditResumeForm(resumeId, model, principal);
        verify(model, never()).addAttribute(any(), any());
        assertEquals("error", viewName);
    }

    @Test
    void testShowEditResumeFormResumeNotFound() throws SystemException {
        Long resumeId = 1L;
        when(principal.getName()).thenReturn(user.getEmail());
        when(userService.findByEmail(user.getEmail())).thenReturn(user);
        when(resumeService.findResumeById(resumeId)).thenReturn(null);
        String viewName = resumeController.showEditResumeForm(resumeId, model, principal);
        verify(model, never()).addAttribute(any(), any());
        assertEquals("error", viewName);
    }

    @Test
    void testShowAllResumesWithoutTitle() throws SystemException {
        when(resumeService.getAllResumes()).thenReturn(resumes);

        String viewName = resumeController.showAllResumes(null, model);

        verify(model).addAttribute("resumes", resumes);
        assertEquals("all-resume-list", viewName);
    }

    @Test
    void testShowAllResumesWithTitle() throws SystemException {
        String title = "Java";
        when(resumeService.findByTitleContainingIgnoreCase(title)).thenReturn(resumes);

        String viewName = resumeController.showAllResumes(title, model);

        verify(model).addAttribute("resumes", resumes);
        verify(model).addAttribute("searchTitle", title);
        assertEquals("all-resume-list", viewName);
    }

    @Test
    void testShowAllResumesError() throws SystemException {
        String title = "Java";
        when(resumeService.findByTitleContainingIgnoreCase(title)).thenThrow(new RuntimeException("Database error"));

        String viewName = resumeController.showAllResumes(title, model);

        verify(model).addAttribute("errorMessage", "Ошибка при загрузке резюме: Database error");
        assertEquals("error", viewName);
    }
}