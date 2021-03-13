package pl.edu.pwr.pwrinspace.poliwrocket.Model.Sensor;

import com.google.gson.annotations.Expose;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import pl.edu.pwr.pwrinspace.poliwrocket.Controller.ControllerNameEnum;

import java.util.*;

public class GPSSensor implements Observable, IGPSSensor, InvalidationListener {

    List<InvalidationListener> observers = new ArrayList<>();

    @Expose
    private List<ControllerNameEnum> destinationControllerNames = new ArrayList<>();

    @Expose
    private Sensor latitude;

    public Sensor getLatitude() {
        return latitude;
    }

    public Sensor getLongitude() {
        return longitude;
    }

    @Expose
    private Sensor longitude;

    private boolean isLatUpToDate = false;

    private boolean isLongUpToDate = false;

    public GPSSensor() {
    }

    public GPSSensor(Sensor latitude, Sensor longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public void observeFields(){
        this.latitude.addListener(this);
        this.longitude.addListener(this);
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
    }

    @Override
    public void setPosition(double latitude, double longitude) {
        this.latitude.setValue(latitude);
        this.longitude.setValue(longitude);
        notifyObserver();
    }

    @Override
    public Map<String, Double> getPosition() {
        HashMap<String, Double> position = new HashMap<>();
        position.put(IGPSSensor.LATITUDE_KEY,latitude.getValue());
        position.put(IGPSSensor.LONGITUDE_KEY,longitude.getValue());
        return position;
    }

    @Override
    public void invalidated(Observable observable) {
        if (observable == longitude) {
            isLongUpToDate = true;
        }
        if (observable == latitude) {
            isLatUpToDate = true;
        }
        if (isLongUpToDate && isLatUpToDate) {
            notifyObserver();
            isLongUpToDate = false;
            isLatUpToDate = false;
        }
    }

    public List<ControllerNameEnum> getDestinationControllerNames() {
        return destinationControllerNames;
    }

    public void setDestinationControllerNames(List<ControllerNameEnum> destinationControllerNames) {
        this.destinationControllerNames = destinationControllerNames;
    }
}
