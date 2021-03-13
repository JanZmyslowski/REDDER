package pl.edu.pwr.pwrinspace.poliwrocket.Model.Sensor;

import com.google.gson.annotations.Expose;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import pl.edu.pwr.pwrinspace.poliwrocket.Controller.ControllerNameEnum;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GyroSensor implements Observable, InvalidationListener, IGyroSensor {

    @Expose
    private Sensor axis_x;

    @Expose
    private Sensor axis_y;

    @Expose
    private Sensor axis_z;

    @Expose
    private List<ControllerNameEnum> destinationControllerNames = new ArrayList<>();

    List<InvalidationListener> observers = new ArrayList<>();

    public GyroSensor(){

    }

    public Sensor getAxis_x() {
        return axis_x;
    }

    public Sensor getAxis_y() {
        return axis_y;
    }

    public Sensor getAxis_z() {
        return axis_z;
    }

    public GyroSensor(GyroSensor sensor) {
        this.axis_x= sensor.axis_x;
        this.axis_y= sensor.axis_y;
        this.axis_z= sensor.axis_z;
    }
    public GyroSensor(Sensor axis_x, Sensor axis_y, Sensor axis_z){
        this.axis_x=axis_x;
        this.axis_y=axis_y;
        this.axis_z=axis_z;
    }

    public void observeFields(){
        this.axis_x.addListener(this);
        this.axis_y.addListener(this);
        this.axis_z.addListener(this);
    }

    @Override
    public Map<String, Double> getValueGyro() {
        HashMap<String, Double> gyro = new HashMap<String,Double>();
        gyro.put(IGyroSensor.AXIS_X_KEY,axis_x.getValue());
        gyro.put(IGyroSensor.AXIS_Y_KEY,axis_y.getValue());
        gyro.put(IGyroSensor.AXIS_Z_KEY,axis_z.getValue());
        return gyro;
    }

    @Override
    public void assignSensors(Sensor axis_x, Sensor axis_y, Sensor axis_z) {
        this.axis_x = axis_x;
        this.axis_y = axis_y;
        this.axis_z = axis_z;
    }

    private void notifyObserver(){
        for (InvalidationListener obs: observers) {
            obs.invalidated(this);
        }
    }

    @Override
    public void addListener(InvalidationListener invalidationListener) {
        observers.add(invalidationListener);
    }

    @Override
    public void removeListener(InvalidationListener invalidationListener) {
        observers.remove(invalidationListener);
    }

    @Override
    public void invalidated(Observable observable) {
        notifyObserver();
    }

    public List<ControllerNameEnum> getDestinationControllerNames() {
        return destinationControllerNames;
    }

    public void setDestinationControllerNames(List<ControllerNameEnum> destinationControllerNames) {
        this.destinationControllerNames = destinationControllerNames;
    }
}
