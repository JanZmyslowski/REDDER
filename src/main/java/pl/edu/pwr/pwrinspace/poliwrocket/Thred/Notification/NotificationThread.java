package pl.edu.pwr.pwrinspace.poliwrocket.Thred.Notification;

import pl.edu.pwr.pwrinspace.poliwrocket.Model.Notification.Schedule;
import pl.edu.pwr.pwrinspace.poliwrocket.Service.Notification.NotificationSendService;

import java.util.ArrayList;
import java.util.List;

public class NotificationThread implements INotificationThread {

    private List<Schedule> schedule;

    private final NotificationSendService notificationSendService;

    private int seconds = 1;

    public NotificationThread(NotificationSendService notificationSendService) {
        this.schedule = new ArrayList<>();
        this.notificationSendService = notificationSendService;
    }

    @Override
    public void sendNotifications(String message) {
        notificationSendService.sendNotification(message);
    }

    @Override
    public void setupSchedule(List<Schedule> schedule) {
        this.schedule = schedule;
    }

    @Override
    public String getFormattedMessage(String messageKey) {
        return messageKey;
    }

    @Override
    public void run() {
        if (!schedule.isEmpty()) {
            try {
                Thread.sleep(2000);
                while (true) {
                    schedule.forEach(scheduleTask -> {
                        if (seconds % scheduleTask.getEverySecond() == 0) {
                            sendNotifications(getFormattedMessage(scheduleTask.getMessageKey()));
                        }
                    });

                    Thread.sleep(1000);

                    seconds++;
                    if (seconds == Integer.MAX_VALUE) {
                        break;
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
                Thread.currentThread().interrupt();
            }
        }
    }

}
