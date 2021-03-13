package pl.edu.pwr.pwrinspace.poliwrocket.Controller.BasicController;

import javafx.beans.InvalidationListener;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.Sensor.ISensor;

import java.util.Collection;
import java.util.HashSet;

public abstract class BasicSensorController extends BasicController implements InvalidationListener {


    protected HashSet<ISensor> sensors = new HashSet<>();

    public final void injectSensorsModels(Collection<ISensor> sensors) {
        if(this.sensors == null) {
            this.sensors = new HashSet<>();
        }
        this.sensors.addAll(sensors);
        setUIBySensors();
    }

    protected abstract void setUIBySensors();

}
