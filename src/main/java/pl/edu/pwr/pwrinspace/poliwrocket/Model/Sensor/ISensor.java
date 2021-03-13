package pl.edu.pwr.pwrinspace.poliwrocket.Model.Sensor;

import pl.edu.pwr.pwrinspace.poliwrocket.Controller.ControllerNameEnum;

import java.time.Instant;
import java.util.List;

public interface ISensor {

    double getValue();
    String getDestination();
    String getName();
    List<ControllerNameEnum> getDestinationControllerNames();
    void setValue(double newValue);
    void setDestination(String destination);
    String getUnit();
    double getMaxRange();
    double getMinRange();
    boolean isBoolean();
    Instant getTimeStamp();
}
