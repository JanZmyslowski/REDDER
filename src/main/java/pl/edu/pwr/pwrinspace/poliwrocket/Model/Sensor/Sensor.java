package pl.edu.pwr.pwrinspace.poliwrocket.Model.Sensor;

import com.google.gson.annotations.Expose;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import org.jetbrains.annotations.NotNull;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.Configuration.Configuration;
import pl.edu.pwr.pwrinspace.poliwrocket.Controller.ControllerNameEnum;
import pl.edu.pwr.pwrinspace.poliwrocket.Controller.NewMapController;

import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Sensor implements Observable, ISensor {

    public List<InvalidationListener> observers = new ArrayList<>();

    @Expose
    private String destination;

    @Expose
    private String name = "Altitude";

    @Expose
    private String unit = "m";

    private Instant timeStamp;

    private Instant previousTimeStamp;

    @Expose
    private List<ControllerNameEnum> destinationControllerNames = new ArrayList<>();

    @Expose
    private double maxRange = 360;

    @Expose
    private double minRange = -360;

    @Expose
    private boolean isBoolean = false;

    private double value = 0;

    private double maxValue = Double.MIN_VALUE;

    private List<Double> values = new LinkedList<>();

    public Sensor() {
        this.timeStamp = Instant.now();
    }

    public Sensor(@NotNull Sensor sensor) {
        this.destination = sensor.destination;
        this.unit = sensor.unit;
        this.name = sensor.name;
        this.minRange = sensor.minRange;
        this.maxRange = sensor.maxRange;
        this.value = sensor.value;
        this.timeStamp = sensor.timeStamp;
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
    public void setDestination(String destination) {
        this.destination = destination;
    }

    @Override
    public String getDestination() {
        return destination;
    }

    public Instant getTimeStamp() {
        return timeStamp;
    }

    private void notifyObserver() {
        for (InvalidationListener obs : observers) {
            if (isBoolean
                    || this.timeStamp.toEpochMilli() - this.previousTimeStamp.toEpochMilli() >  1000
                    || this.values.size() % Configuration.getInstance().FPS == 0
                    || obs instanceof Observable
                    || obs instanceof NewMapController) {

                obs.invalidated(this);
            }
        }
    }

    @Override
    public void setValue(double newValue) {
        this.values.add(newValue);

        if (isBoolean || Instant.now().toEpochMilli() - this.timeStamp.toEpochMilli() >  1000) {
            this.value = newValue;
        } else if (this.values.size() % Configuration.getInstance().FPS == 0) {
            this.value = this.getAverageFromSecond();
        } else {
            this.value = newValue;
        }
        if(this.value > this.maxValue) {
            this.maxValue = this.value;
        }
        this.previousTimeStamp = this.timeStamp;
        this.timeStamp = Instant.now();
        notifyObserver();
    }

    private double getAverageFromSecond() {
        double sum = 0;
        int lastIndex = this.values.size();
        int startIndex = lastIndex <= Configuration.getInstance().FPS ? 0 : lastIndex - Configuration.getInstance().FPS;
        this.values = this.values.subList(startIndex, lastIndex);
        for (Double oldValue : this.values) {
            sum += oldValue;
        }
        return sum / (double) (Configuration.getInstance().FPS);
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getUnit() {
        return unit;
    }

    @Override
    public double getMaxRange() {
        return maxRange;
    }

    @Override
    public double getMinRange() {
        return minRange;
    }

    @Override
    public double getValue() {
        return value;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    public List<ControllerNameEnum> getDestinationControllerNames() {
        return destinationControllerNames;
    }

    public void setDestinationControllerNames(List<ControllerNameEnum> destinationControllerNames) {
        this.destinationControllerNames = destinationControllerNames;
    }

    public void setMaxRange(double maxRange) {
        this.maxRange = maxRange;
    }

    public void setMinRange(double minRange) {
        this.minRange = minRange;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public double getMaxValue() {
        return maxValue;
    }

    public boolean isBoolean() {
        return isBoolean;
    }

    public void setBoolean(boolean aBoolean) {
        isBoolean = aBoolean;
    }

    public double getPreviousValue() {
        var size = this.values.size();
        return size >= 2 ? this.values.get(size-2) : value;
    }
}
