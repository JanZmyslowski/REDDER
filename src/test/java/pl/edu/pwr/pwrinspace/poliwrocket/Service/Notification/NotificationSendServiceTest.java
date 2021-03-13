package pl.edu.pwr.pwrinspace.poliwrocket.Service.Notification;

import net.dv8tion.jda.api.EmbedBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.Notification.INotification;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

class NotificationSendServiceTest {

    @Mock
    private INotification mockNotification;
    @Mock
    private NotificationFormatService mockNotificationFormatService;

    private NotificationSendService notificationSendServiceUnderTest;

    @BeforeEach
    void setUp() {
        initMocks(this);
        notificationSendServiceUnderTest = new NotificationSendService(mockNotification, mockNotificationFormatService);
    }

    @Test
    void testSendNotificationString() {
        // Setup
        final String message = "message";
        final var result = "result";
        when(mockNotificationFormatService.getFormattedMessage(message)).thenReturn(result);

        // Run the test
        notificationSendServiceUnderTest.sendNotification(message);

        // Verify the results
        verify(mockNotification).sendNotification(result);
    }

    @Test
    void testSendNotificationBuilder() {
        // Setup
        final String message = "message";
        final var result = new EmbedBuilder();
        when(mockNotificationFormatService.getFormattedMessage(message)).thenReturn(result);

        // Run the test
        notificationSendServiceUnderTest.sendNotification(message);

        // Verify the results
        verify(mockNotification).sendNotification(result);
    }

    @Test
    void testIsConnected() {
        // Setup
        when(mockNotification.isConnected()).thenReturn(true);

        // Run the test
        final boolean result = notificationSendServiceUnderTest.isConnected();

        // Verify the results
        assertTrue(result);
    }

    @Test
    void testIsConnected2() {
        // Setup
        when(mockNotification.isConnected()).thenReturn(false);

        // Run the test
        final boolean result = notificationSendServiceUnderTest.isConnected();

        // Verify the results
        assertFalse(result);
    }
}
