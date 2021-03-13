package pl.edu.pwr.pwrinspace.poliwrocket.Model.Sensor;

import java.util.Map;

public interface IGPSSensor {
     String LATITUDE_KEY = "latitude";
     String LONGITUDE_KEY = "longitude";

     void setPosition(double latitude, double longitude);
     Map<String,Double> getPosition();
}
