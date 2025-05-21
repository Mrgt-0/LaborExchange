import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import org.example.Config.MyUserDetails;
import org.example.Controller.MessageController;
import org.example.DTO.MessageDTO;
import org.example.DTO.UserDTO;
import org.example.Service.MessageService;
import org.example.Service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import java.util.ArrayList;
import java.util.List;

class MessageControllerTest {

    @InjectMocks
    private MessageController messageController;

    @Mock
    private MessageService messageService;

    @Mock
    private UserService userService;

    @Mock
    private Model model;

    @Mock
    private Authentication authentication;
    private UserDTO currentUser;
    private UserDTO recipientUser;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        currentUser = new UserDTO();
        currentUser.setEmail("currentUser@example.com");
        recipientUser = new UserDTO();
        recipientUser.setEmail("recipient@example.com");
    }

    @Test
    void testShowMessagesUserFound() {
        when(authentication.getPrincipal()).thenReturn(new MyUserDetails(currentUser));
        when(userService.findByEmail("recipient@example.com")).thenReturn(recipientUser);
        when(messageService.getMessages(currentUser, recipientUser)).thenReturn(new ArrayList<>());
        String viewName = messageController.showMessages("recipient@example.com", model, authentication);
        verify(model).addAttribute("messages", new ArrayList<>());
        verify(model).addAttribute("recipient", recipientUser);
        assertEquals("chat", viewName);
    }

    @Test
    void testShowMessagesUserNotFound() {
        when(authentication.getPrincipal()).thenReturn(new MyUserDetails(currentUser));
        when(userService.findByEmail("nonexistent@example.com")).thenReturn(null);
        String viewName = messageController.showMessages("nonexistent@example.com", model, authentication);
        verify(model).addAttribute("error", "Пользователь не найден.");
        assertEquals("error", viewName);
    }

    @Test
    void testSendMessageSuccess() {
        when(authentication.getPrincipal()).thenReturn(new MyUserDetails(currentUser));
        when(userService.findByEmail(recipientUser.getEmail())).thenReturn(recipientUser);
        String viewName = messageController.sendMessage("recipient@example.com", "Hello!", authentication);
        verify(messageService).sendMessage(currentUser, recipientUser, "Hello!");
        assertEquals("redirect:/chat/recipient@example.com", viewName);
    }

    @Test
    void testSendMessageRecipientNotFound() {
        when(authentication.getPrincipal()).thenReturn(new MyUserDetails(currentUser));
        when(userService.findByEmail("nonexistent@example.com")).thenReturn(null);

        String viewName = messageController.sendMessage("nonexistent@example.com", "Hello!", authentication);

        verify(messageService, never()).sendMessage(any(), any(), anyString());
        assertEquals("error", viewName);
    }

    @Test
    void testGetMessagesUserFound() {
        when(authentication.getPrincipal()).thenReturn(new MyUserDetails(currentUser));
        when(userService.findByEmail("recipient@example.com")).thenReturn(recipientUser);
        List<MessageDTO> messages = new ArrayList<>();
        messages.add(new MessageDTO());
        when(messageService.getMessages(currentUser, recipientUser)).thenReturn(messages);
        ResponseEntity<List<MessageDTO>> response = messageController.getMessages("recipient@example.com", authentication);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(messages, response.getBody());
    }

    @Test
    void testGetMessagesRecipientNotFound() {
        when(authentication.getPrincipal()).thenReturn(new MyUserDetails(currentUser));
        when(userService.findByEmail("nonexistent@example.com")).thenReturn(null);
        ResponseEntity<List<MessageDTO>> response = messageController.getMessages("nonexistent@example.com", authentication);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void testGetMessagesUnauthorized() {
        when(authentication.getPrincipal()).thenReturn(new Object());
        ResponseEntity<List<MessageDTO>> response = messageController.getMessages("recipient@example.com", authentication);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }
}