package pl.edu.pwr.pwrinspace.poliwrocket.Event;

import net.dv8tion.jda.api.hooks.ListenerAdapter;
import pl.edu.pwr.pwrinspace.poliwrocket.Service.Notification.NotificationFormatService;

public abstract class NotificationEvent extends ListenerAdapter {

    protected NotificationFormatService notification;

    public NotificationEvent(NotificationFormatService notificationFormatService) {
        this.notification = notificationFormatService;
    }
}
