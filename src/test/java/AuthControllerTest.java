import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import jakarta.transaction.SystemException;
import org.example.Controller.AuthController;
import org.example.DTO.UserDTO;
import org.example.Enum.UserTypeEnum;
import org.example.Model.User;
import org.example.Model.UserType;
import org.example.Service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.Collections;

class AuthControllerTest {

    @InjectMocks
    private AuthController authController;

    @Mock
    private UserService userService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private BindingResult bindingResult;

    @Mock
    private Model model;

    @Mock
    private RedirectAttributes redirectAttributes;

    private UserDTO userDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userDTO = new UserDTO();
        userDTO.setEmail("test@example.com");
        userDTO.setPassword("password");
    }

    @Test
    void testRegisterUserSuccess() throws SystemException {
        when(bindingResult.hasErrors()).thenReturn(false);
        doNothing().when(userService).registerUser(any(UserDTO.class));
        String viewName = authController.registerUser(userDTO, bindingResult, model, redirectAttributes);
        verify(redirectAttributes).addFlashAttribute("successMessage", "Регистрация прошла успешно! Пожалуйста, войдите.");
        assertEquals("redirect:/auth/login", viewName);
    }

    @Test
    void testRegisterUserValidationError() throws SystemException {
        when(bindingResult.hasErrors()).thenReturn(true);
        String viewName = authController.registerUser(userDTO, bindingResult, model, redirectAttributes);
        verify(model, never()).addAttribute(anyString(), anyString());
        assertEquals("register", viewName);
    }

    @Test
    void testRegisterUserRuntimeException() throws SystemException {
        when(bindingResult.hasErrors()).thenReturn(false);
        doThrow(new RuntimeException("Ошибка")).when(userService).registerUser(any(UserDTO.class));
        String viewName = authController.registerUser(userDTO, bindingResult, model, redirectAttributes);
        assertEquals("register", viewName);
        verify(model).addAttribute("errorMessage", "Ошибка");
    }

    @Test
    void testShowRegistrationForm() {
        String viewName = authController.showRegistrationForm(model);
        verify(model).addAttribute(eq("user"), any(User.class));
        assertEquals("register", viewName);
    }

    @Test
    void testLoginSuccess() {
        UserType userType = new UserType();
        userType.setType(UserTypeEnum.CANDIDATE);
        userDTO.setUserTypes(Collections.singleton(userType));
        when(authenticationManager.authenticate(any())).thenReturn(mock(Authentication.class));
        when(userService.findByEmail(userDTO.getEmail())).thenReturn(userDTO);
        String viewName = authController.login(userDTO.getEmail(), userDTO.getPassword(), model);
        assertEquals("redirect:/users/user-profile", viewName);
    }

    @Test
    void testLoginEmptyEmail() {
        String viewName = authController.login("", userDTO.getPassword(), model);
        assertEquals("login", viewName);
        verify(model).addAttribute("errorMessage", "Неверное имя пользователя или пароль.");
    }

    @Test
    void testLoginEmptyPassword() {
        String viewName = authController.login(userDTO.getEmail(), "", model);
        assertEquals("login", viewName);
        verify(model).addAttribute("errorMessage", "Пароль не может быть пустым");
    }

    @Test
    void testLoginAuthenticationException() {
        when(authenticationManager.authenticate(any())).thenThrow(new AuthenticationException("Ошибка") {});
        String viewName = authController.login(userDTO.getEmail(), userDTO.getPassword(), model);
        assertEquals("login", viewName);
        verify(model).addAttribute("errorMessage", "Неверное имя пользователя или пароль.");
    }

    @Test
    void testShowLoginForm() {
        String viewName = authController.showLoginForm(model);
        verify(model).addAttribute(eq("user"), any(User.class));
        assertEquals("login", viewName);
    }
}