package pl.edu.pwr.pwrinspace.poliwrocket.Model.Sensor;

import pl.edu.pwr.pwrinspace.poliwrocket.Model.Sensor.ISensor;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.Sensor.Sensor;

import java.util.Set;

public interface ISensorRepository {
    Sensor getSensorByName(String name);
    void addSensor(Sensor sensor);
    void removeSensor(ISensor sensor);
    void updateSensor(ISensor sensor);
    Set<String> getSensorsKeys();
}
