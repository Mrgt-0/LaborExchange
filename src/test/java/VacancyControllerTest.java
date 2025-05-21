import org.example.Controller.VacancyController;
import org.example.DTO.ApplicationDTO;
import org.example.DTO.UserDTO;
import org.example.DTO.VacancyDTO;
import org.example.Mapper.ApplicationMapper;
import org.example.Mapper.VacancyMapper;
import org.example.Model.Application;
import org.example.Model.Vacancy;
import org.example.Repository.UserRepository;
import org.example.Service.ApplicationService;
import org.example.Service.UserService;
import org.example.Service.VacancyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;
import java.security.Principal;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class VacancyControllerTest {
    @InjectMocks
    private VacancyController vacancyController;

    @Mock
    private ApplicationService applicationService;

    @Mock
    private VacancyService vacancyService;

    @Mock
    private UserService userService;

    @Mock
    private ApplicationMapper applicationMapper;

    @Mock
    private VacancyMapper vacancyMapper;

    @Mock
    private Model model;

    @Mock
    private Principal principal;

    @Mock
    private UserRepository userRepository;

    private Vacancy vacancy;
    private ApplicationDTO applicationDTO;
    private UserDTO userDTO;

    @BeforeEach
    void setUp() {
        vacancy = new Vacancy();
        applicationDTO = new ApplicationDTO();
        userDTO = new UserDTO();
        userDTO.setId(1L);
        userDTO.setEmail("employer@example.com");
        userDTO.setName("Employer Name");
    }
    @Test
    void testShowApplications() {
        Long vacancyId = 1L;
        Application application = new Application();
        application.setId(1L);
        List<Application> applicationsModel = Collections.singletonList(application);
        when(applicationService.getApplicationsForVacancy(vacancyId)).thenReturn(applicationsModel);
        when(vacancyService.getVacancyById(vacancyId)).thenReturn(vacancy);
        String viewName = vacancyController.showApplications(vacancyId, model, null);
        List<ApplicationDTO> applicationsDTO = applicationsModel.stream()
                .map(applicationMapper::toDTO)
                .collect(Collectors.toList());
        verify(model).addAttribute("applications", applicationsDTO);
        verify(model).addAttribute("vacancy", vacancy);
        assertThat(viewName).isEqualTo("employer-application-list");
    }
    @Test
    void testShowAddVacancyForm() {
        when(principal.getName()).thenReturn(userDTO.getEmail());
        when(userService.findByEmail(userDTO.getEmail())).thenReturn(userDTO);
        Vacancy vacancy = new Vacancy();
        String viewName = vacancyController.showAddVacancyForm(model, principal);
        verify(model).addAttribute(eq("vacancy"), any(Vacancy.class));
        verify(model).addAttribute("employerId", userDTO.getId());
        assertThat(viewName).isEqualTo("vacancy-add");
    }

    @Test
    void testShowAddVacancyForm_UserNotFound() {
        when(principal.getName()).thenReturn(userDTO.getEmail());
        when(userService.findByEmail(userDTO.getEmail())).thenReturn(null);
        String viewName = vacancyController.showAddVacancyForm(model, principal);

        assertThat(viewName).isEqualTo("error");
    }

    @Test
    void testShowEmployerVacancies() {
        when(principal.getName()).thenReturn(userDTO.getEmail());
        when(userService.findByEmail(userDTO.getEmail())).thenReturn(userDTO);
        when(vacancyService.findVacancyByEmployerId(userDTO.getId())).thenReturn(Collections.singletonList(vacancy));
        String viewName = vacancyController.showEmployerVacancies(model, principal);
        verify(model).addAttribute("myVacancies", Collections.singletonList(vacancy));
        assertThat(viewName).isEqualTo("employer-vacancy-list");
    }

    @Test
    void testShowEmployerVacancies_UserNotFound() {
        when(principal.getName()).thenReturn(userDTO.getEmail());
        when(userService.findByEmail(userDTO.getEmail())).thenReturn(null);
        String viewName = vacancyController.showEmployerVacancies(model, principal);
        assertThat(viewName).isEqualTo("error");
    }

    @Test
    void testShowEmployerVacancies_NoPrincipal() {
        String viewName = vacancyController.showEmployerVacancies(model, null);
        assertThat(viewName).isEqualTo("error");
    }

    @Test
    void testShowAllVacancies() {
        List<VacancyDTO> vacancyDTOs = Collections.singletonList(new VacancyDTO());
        when(vacancyService.getAllVacancies()).thenReturn(Collections.singletonList(new Vacancy()));
        when(vacancyMapper.toDTO(any())).thenReturn(vacancyDTOs.get(0));
        String viewName = vacancyController.showAllVacancies(null, model);

        verify(model).addAttribute("vacancies", vacancyDTOs);
        verify(model).addAttribute("searchTitle", null);
        assertThat(viewName).isEqualTo("all-vacancy-list");
    }

    @Test
    void testShowEditVacancy_UserNotFound() {
        Long vacancyId = 1L;
        when(principal.getName()).thenReturn(userDTO.getEmail());
        when(userService.findByEmail(userDTO.getEmail())).thenReturn(null);
        String viewName = vacancyController.showEditVacancy(vacancyId, model, principal);
        assertThat(viewName).isEqualTo("error");
    }
}
