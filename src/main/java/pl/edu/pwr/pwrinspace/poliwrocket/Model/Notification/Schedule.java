package pl.edu.pwr.pwrinspace.poliwrocket.Model.Notification;

import com.google.gson.annotations.Expose;

public class Schedule {

    @Expose
    private String messageKey;

    @Expose
    private int everySecond;

    public Schedule(String messageKey, int everySecond) {
        this.messageKey = messageKey;
        this.everySecond = everySecond;
    }

    public String getMessageKey() {
        return messageKey;
    }

    public int getEverySecond() {
        return everySecond;
    }
}
