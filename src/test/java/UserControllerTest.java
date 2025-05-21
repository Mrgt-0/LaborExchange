import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import jakarta.transaction.SystemException;
import org.example.Config.MyUserDetails;
import org.example.Controller.UserController;
import org.example.DTO.UserDTO;
import org.example.Service.UserDetailsServiceImpl;
import org.example.Service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.ui.Model;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;
import java.util.List;

class UserControllerTest {
    @InjectMocks
    private UserController userController;

    @Mock
    private UserService userService;

    @Mock
    private UserDetailsServiceImpl userDetailsService;

    @Mock
    private Model model;

    @Mock
    private Authentication authentication;

    private UserDTO user;
    private List<UserDTO> users;
    UserDetails userDetails;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user = new UserDTO();
        user.setId(1L);
        user.setEmail("user@example.com");
        user.setName("Test");
        user.setLastname("User");
        userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn(user.getEmail());
        when(userDetails.getPassword()).thenReturn("password");
        authentication = mock(Authentication.class);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        when(authentication.getName()).thenReturn(user.getEmail());
        when(userDetailsService.loadUserByUsername(user.getEmail())).thenReturn(userDetails);
    }

    @Test
    void testShowUserProfileUserFound() {
        when(authentication.getName()).thenReturn(user.getEmail());
        when(userService.findByEmail(user.getEmail())).thenReturn(user);

        String viewName = userController.showUserProfile(model);

        verify(model).addAttribute("user", user);
        assertEquals("user-profile", viewName);
    }

    @Test
    void testShowUserProfileUserNotFound() {
        when(authentication.getName()).thenReturn("unknown@example.com");
        when(userService.findByEmail("unknown@example.com")).thenReturn(null);

        String viewName = userController.showUserProfile(model);

        verify(model).addAttribute("errorMessage", "Пользователь не найден.");
        assertEquals("error", viewName);
    }

    @Test
    void testUpdateUserProfileEmailNotChanged() throws SystemException {
        UserDTO updatedUser = new UserDTO();
        updatedUser.setEmail(user.getEmail());
        updatedUser.setName("Updated");
        updatedUser.setLastname("User");
        when(authentication.getName()).thenReturn(user.getEmail());
        userController.updateUserProfile(updatedUser, "ADMIN");
        verify(userService).updateUser(user.getEmail(), updatedUser);
    }

    @Test
    void testListUsersWithoutFilter() {
        when(userService.getAllUsers()).thenReturn(users);
        String viewName = userController.listUsers(null, null, model);
        verify(model).addAttribute("users", users);
        assertEquals("admin-users-list", viewName);
    }

    @Test
    void testListUsersWithFilter() {
        String name = "Test";
        when(userService.findByNameAndLastname(name, null)).thenReturn(users);
        String viewName = userController.listUsers(name, null, model);
        verify(model).addAttribute("users", users);
        verify(model).addAttribute("searchTitle", name);
        assertEquals("admin-users-list", viewName);
    }

    @Test
    void testUpdateUserRole() {
        Long userId = 1L;
        String[] userTypes = {"ROLE_USER"};
        String viewName = userController.updateUserRole(userId, userTypes);
        verify(userService).updateUserRole(userId, userTypes);
        assertEquals("redirect:/users/admin-users-list", viewName);
    }

    @Test
    void testDeleteUserSelfDelete() throws SystemException {
        MyUserDetails userDetails = mock(MyUserDetails.class);
        when(userDetails.getId()).thenReturn(user.getId());
        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn(user.getEmail());
        when(auth.getPrincipal()).thenReturn(userDetails);
        SecurityContextHolder.getContext().setAuthentication(auth);
        String viewName = userController.deleteUser(user.getId(), auth, new RedirectAttributesModelMap());
        assertEquals("redirect:/users/admin-users-list", viewName);
    }

    @Test
    void testDeleteUserSuccess() throws SystemException {
        Long deleteUserId = 2L;
        MyUserDetails userDetails = mock(MyUserDetails.class);
        when(userDetails.getId()).thenReturn(1L);
        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn("other@example.com");
        when(auth.getPrincipal()).thenReturn(userDetails);
        SecurityContextHolder.getContext().setAuthentication(auth);
        String viewName = userController.deleteUser(deleteUserId, auth, new RedirectAttributesModelMap());
        verify(userService).deleteUserById(deleteUserId);
        assertEquals("redirect:/users/admin-users-list", viewName);
    }
}