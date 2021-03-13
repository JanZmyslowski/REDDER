package pl.edu.pwr.pwrinspace.poliwrocket.Service.Notification;

import net.dv8tion.jda.api.EmbedBuilder;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.Configuration.Configuration;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.Sensor.IGPSSensor;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.Sensor.SensorRepository;
import pl.edu.pwr.pwrinspace.poliwrocket.Thred.ThreadName;

import java.awt.*;

public class NotificationFormatDiscordService extends NotificationFormatService {

    protected String channel = Configuration.getInstance().DISCORD_CHANNEL_NAME;

    public NotificationFormatDiscordService(SensorRepository sensorRepository) {
        super(sensorRepository);
    }

    @Override
    public Object getFormattedMessage(String messageKey) {
        EmbedBuilder embedBuilder = new EmbedBuilder();

        if (messageKey.equalsIgnoreCase("Data")) {
            embedBuilder.setTitle("Current data").setColor(Color.GRAY);
            sensorRepository.getAllBasicSensors().forEach((s, sensor) ->
                embedBuilder.addField(sensor.getName(), sensor.getValue() + " " + sensor.getUnit(), true)
            );
            return embedBuilder;

        } else if (messageKey.equalsIgnoreCase("Max")) {
            embedBuilder.setTitle("Max values").setColor(Color.RED);
            sensorRepository.getAllBasicSensors().forEach((s, sensor) ->
                embedBuilder.addField(sensor.getName(), sensor.getMaxValue() + " " + sensor.getUnit(), true)
            );
            return embedBuilder;

        } else if (messageKey.equalsIgnoreCase("Position")) {
            var latitude = sensorRepository.getGpsSensor().getPosition().get(IGPSSensor.LATITUDE_KEY);
            var longitude = sensorRepository.getGpsSensor().getPosition().get(IGPSSensor.LONGITUDE_KEY);

            embedBuilder.setTitle("Current position").setColor(Color.BLUE)
                    .addField("Latitude", latitude.toString(), true)
                    .addField("Longitude", longitude.toString(), true)
                    .addField("Map", googleMap + latitude.toString() +
                            "," + longitude.toString(), false);

            return  embedBuilder;
        } else if (messageKey.equalsIgnoreCase("Map")) {
            var latitude = sensorRepository.getGpsSensor().getPosition().get(IGPSSensor.LATITUDE_KEY);
            var longitude = sensorRepository.getGpsSensor().getPosition().get(IGPSSensor.LONGITUDE_KEY);
            return (googleMap + latitude.toString() + "," + longitude.toString());
        } else if (messageKey.equalsIgnoreCase("Thread status")) {
            embedBuilder.setColor(Color.YELLOW)
                        .setTitle("Notification thread");
            for (Thread thread : Thread.getAllStackTraces().keySet()) {
                if (thread.getName().equals(ThreadName.DISCORD_NOTIFICATION.getName())) {
                    embedBuilder.addField("State: ", thread.getState().toString(), false);
                    embedBuilder.addField("Is alive: ", thread.isAlive() ? "Yes": "No", false);
                    return embedBuilder;
                }
            }
            embedBuilder.addField("Status","There is no notification thread active.",false);
            return embedBuilder;
        } else if(messageKey.equalsIgnoreCase("Thread interrupt")) {
            embedBuilder.setColor(Color.YELLOW)
                    .setTitle("Notification thread interrupt");
            for (Thread thread : Thread.getAllStackTraces().keySet()) {
                if (thread.getName().equals(ThreadName.DISCORD_NOTIFICATION.getName())) {
                    thread.interrupt();
                    embedBuilder.addField("State: ", thread.getState().toString(), false);
                    embedBuilder.addField("Is alive: ", thread.isAlive() ? "Yes": "No", false);
                    embedBuilder.addField(ThreadName.DISCORD_NOTIFICATION.getName(), "Interrupted",false);
                    return embedBuilder;
                }
            }
            embedBuilder.addField("Status","There is no notification thread active.",false);
            return embedBuilder;
        }

        return "Error - Request \"" + messageKey + "\" not recognized.";
    }
}
