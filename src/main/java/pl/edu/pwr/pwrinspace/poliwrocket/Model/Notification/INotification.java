package pl.edu.pwr.pwrinspace.poliwrocket.Model.Notification;

import javafx.beans.Observable;
import net.dv8tion.jda.api.EmbedBuilder;

public interface INotification extends Observable {
    void sendNotification(String message);
    void sendNotification(EmbedBuilder message);
    void setup();
    boolean isConnected();
}
