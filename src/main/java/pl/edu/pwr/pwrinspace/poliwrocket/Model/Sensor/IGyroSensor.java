package pl.edu.pwr.pwrinspace.poliwrocket.Model.Sensor;

import java.util.Map;

public interface IGyroSensor {
     String AXIS_X_KEY = "axis_x";
     String AXIS_Y_KEY = "axis_y";
     String AXIS_Z_KEY = "axis_z";

     Map<String,Double> getValueGyro();
     void assignSensors(Sensor axis_x, Sensor axis_y, Sensor axis_z);
}
