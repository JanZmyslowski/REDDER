package pl.edu.pwr.pwrinspace.poliwrocket.Service.Notification;

import pl.edu.pwr.pwrinspace.poliwrocket.Model.Sensor.SensorRepository;

public abstract class NotificationFormatService {

    protected SensorRepository sensorRepository;

    protected String googleMap = "https://www.google.pl/maps/dir//";

    public NotificationFormatService(SensorRepository sensorRepository) {
        this.sensorRepository = sensorRepository;
    }

    public abstract Object getFormattedMessage(String messageKey);


}
