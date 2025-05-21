import org.example.Config.MyUserDetails;
import org.example.Controller.NotificationPageController;
import org.example.Model.Notification;
import org.example.Service.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.ui.Model;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class NotificationPageControllerTest {
    @InjectMocks
    private NotificationPageController notificationPageController;

    @Mock
    private NotificationService notificationService;

    @Mock
    private Model model;

    @Mock
    private MyUserDetails userDetails;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testNotificationsPage() {
        Long userId = 1L;
        List<Notification> notifications = new ArrayList<>();
        notifications.add(new Notification());
        notifications.add(new Notification());

        when(userDetails.getId()).thenReturn(userId);
        when(notificationService.getUserNotifications(userId)).thenReturn(notifications);

        String viewName = notificationPageController.notificationsPage(userDetails, model);

        verify(model).addAttribute("notifications", notifications);
        assertEquals("notifications", viewName);
    }
}
