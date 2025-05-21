import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import org.example.Controller.ApplicationRestController;
import org.example.DTO.ApplicationViewDTO;
import org.example.DTO.UserDTO;
import org.example.Service.ApplicationService;
import org.example.Service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import java.security.Principal;
import java.util.List;

class ApplicationRestControllerTest {

    @InjectMocks
    private ApplicationRestController applicationRestController;

    @Mock
    private ApplicationService applicationService;

    @Mock
    private UserService userService;

    @Mock
    private Principal principal;

    private UserDTO userDTO;
    private ApplicationViewDTO applicationViewDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        userDTO = new UserDTO();
        userDTO.setId(1L);
        userDTO.setEmail("test@example.com");

        applicationViewDTO = new ApplicationViewDTO();
        applicationViewDTO.setId(1L);
        applicationViewDTO.setStatus("ACTIVE");
    }

    @Test
    void testGetMyApplicationsSuccess() {
        when(principal.getName()).thenReturn(userDTO.getEmail());
        when(userService.findByEmail(userDTO.getEmail())).thenReturn(userDTO);
        when(applicationService.getApplicationsByUser(userDTO.getId())).thenReturn(List.of(applicationViewDTO));
        ResponseEntity<List<ApplicationViewDTO>> response = applicationRestController.getMyApplications(principal);

        verify(userService).findByEmail(userDTO.getEmail());
        verify(applicationService).getApplicationsByUser(userDTO.getId());
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(1, response.getBody().size());
        assertEquals(applicationViewDTO, response.getBody().get(0));
    }

    @Test
    void testGetMyApplicationsUserNotAuthenticated() {
        when(principal.getName()).thenReturn(null);
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            applicationRestController.getMyApplications(principal);
        });
        assertEquals("Пользователь не аутентифицирован", exception.getMessage());
    }

    @Test
    void testGetMyApplicationsUserNotFound() {
        when(principal.getName()).thenReturn(userDTO.getEmail());
        when(userService.findByEmail(userDTO.getEmail())).thenReturn(null);
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            applicationRestController.getMyApplications(principal);
        });
        assertEquals("Пользователь по email " + userDTO.getEmail() + " не найден", exception.getMessage());
    }
}