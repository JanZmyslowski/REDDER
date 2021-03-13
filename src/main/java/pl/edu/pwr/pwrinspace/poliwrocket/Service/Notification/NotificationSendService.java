package pl.edu.pwr.pwrinspace.poliwrocket.Service.Notification;

import net.dv8tion.jda.api.EmbedBuilder;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.Notification.INotification;

public class NotificationSendService {

    private final INotification notification;

    private final NotificationFormatService notificationFormatService;

    public NotificationSendService(INotification notification, NotificationFormatService notificationFormatService) {
        this.notification = notification;
        this.notificationFormatService = notificationFormatService;
    }

    public void sendNotification(String message){
        var formattedMessage = notificationFormatService.getFormattedMessage(message);
        if(formattedMessage instanceof EmbedBuilder) {
            notification.sendNotification((EmbedBuilder) formattedMessage);
        } else if (formattedMessage instanceof String) {
            notification.sendNotification((String) formattedMessage);
        }
    }

    public boolean isConnected() {
        return notification.isConnected();
    }
}
