import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import org.example.Controller.NotificationController;
import org.example.Model.Notification;
import org.example.Service.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.util.ArrayList;
import java.util.List;

class NotificationControllerTest {
    @InjectMocks
    private NotificationController notificationController;

    @Mock
    private NotificationService notificationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetUserNotifications() {
        Long userId = 1L;
        List<Notification> notifications = new ArrayList<>();
        notifications.add(new Notification());
        notifications.add(new Notification());
        when(notificationService.getUserNotifications(userId)).thenReturn(notifications);
        List<Notification> result = notificationController.getUserNotifications(userId);
        assertEquals(notifications, result);
        assertEquals(2, result.size());
        verify(notificationService).getUserNotifications(userId);
    }
}