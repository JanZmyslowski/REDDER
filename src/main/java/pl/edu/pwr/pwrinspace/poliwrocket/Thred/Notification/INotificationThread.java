package pl.edu.pwr.pwrinspace.poliwrocket.Thred.Notification;

import pl.edu.pwr.pwrinspace.poliwrocket.Model.Notification.Schedule;

import java.util.List;

public interface INotificationThread extends Runnable {
    void sendNotifications(String message);
    void setupSchedule(List<Schedule> schedule);
    String getFormattedMessage(String messageKey);
}
