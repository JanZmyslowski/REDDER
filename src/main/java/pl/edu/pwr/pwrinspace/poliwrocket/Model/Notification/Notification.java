package pl.edu.pwr.pwrinspace.poliwrocket.Model.Notification;

import javafx.beans.InvalidationListener;
import pl.edu.pwr.pwrinspace.poliwrocket.Event.NotificationEvent;

import java.util.ArrayList;
import java.util.List;

public abstract class Notification implements INotification {

    private List<InvalidationListener> observers = new ArrayList<>();

    protected NotificationEvent notificationEvent;

    public Notification(NotificationEvent notificationEvent){
        this.notificationEvent = notificationEvent;
    }

    @Override
    public synchronized void setup() {
        notifyObserver();
    }

    @Override
    public void addListener(InvalidationListener invalidationListener) {
        observers.add(invalidationListener);
    }

    @Override
    public void removeListener(InvalidationListener invalidationListener) {
        observers.remove(invalidationListener);
    }

    private void notifyObserver() {
        for (InvalidationListener obs : observers) {
            obs.invalidated(this);
        }
    }}
