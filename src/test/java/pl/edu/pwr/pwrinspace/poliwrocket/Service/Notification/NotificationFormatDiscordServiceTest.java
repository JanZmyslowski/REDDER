package pl.edu.pwr.pwrinspace.poliwrocket.Service.Notification;

import net.dv8tion.jda.api.EmbedBuilder;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.Sensor.GPSSensor;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.Sensor.Sensor;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.Sensor.SensorRepository;
import pl.edu.pwr.pwrinspace.poliwrocket.Thred.Notification.NotificationThread;
import pl.edu.pwr.pwrinspace.poliwrocket.Thred.ThreadName;

import java.util.Map;
import java.util.Objects;

import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

class NotificationFormatDiscordServiceTest {

    @Mock
    private SensorRepository mockSensorRepository;

    @Mock
    private NotificationThread mockNotificationThread;

    private NotificationFormatDiscordService notificationFormatDiscordServiceUnderTest;

    @BeforeEach
    void setUp() {
        initMocks(this);
        notificationFormatDiscordServiceUnderTest = new NotificationFormatDiscordService(mockSensorRepository);
    }

    @Test
    void testGetFormattedMessage() {
        // Setup

        // Configure SensorRepository.getAllBasicSensors(...).
        final Map<String, Sensor> stringSensorMap = Map.ofEntries(Map.entry("value", new Sensor()));
        when(mockSensorRepository.getAllBasicSensors()).thenReturn(stringSensorMap);

        // Configure SensorRepository.getGpsSensor(...).
        final GPSSensor gpsSensor = new GPSSensor(new Sensor(), new Sensor());
        when(mockSensorRepository.getGpsSensor()).thenReturn(gpsSensor);

        var messageKey = "messageKey";
        // Run the test
        final Object result = notificationFormatDiscordServiceUnderTest.getFormattedMessage(messageKey);

        // Verify the results
        Assert.assertEquals("Error - Request \"" + messageKey + "\" not recognized.",result);
    }

    @Test
    void testGetFormattedMessageMap() {
        // Setup

        // Configure SensorRepository.getAllBasicSensors(...).
        final double lat = 51.12;
        final double lon = 42.23;
        final Map<String, Sensor> stringSensorMap = Map.ofEntries(Map.entry("value", new Sensor()));
        when(mockSensorRepository.getAllBasicSensors()).thenReturn(stringSensorMap);

        // Configure SensorRepository.getGpsSensor(...).
        final GPSSensor gpsSensor = new GPSSensor(new Sensor(), new Sensor());
        when(mockSensorRepository.getGpsSensor()).thenReturn(gpsSensor);
        gpsSensor.setPosition(lat,lon);
        var messageKey = "Map";
        // Run the test
        final Object result = notificationFormatDiscordServiceUnderTest.getFormattedMessage(messageKey);

        // Verify the results
        Assert.assertTrue(result instanceof String);
        Assert.assertTrue(((String) result).contains(Double.toString(lat)));
        Assert.assertTrue(((String) result).contains(Double.toString(lon)));
    }

    @Test
    void testGetFormattedMessagePosition() {
        // Setup

        // Configure SensorRepository.getAllBasicSensors(...).
        final double lat = 51.12;
        final double lon = 42.23;
        final Map<String, Sensor> stringSensorMap = Map.ofEntries(Map.entry("value", new Sensor()));
        when(mockSensorRepository.getAllBasicSensors()).thenReturn(stringSensorMap);

        // Configure SensorRepository.getGpsSensor(...).
        final GPSSensor gpsSensor = new GPSSensor(new Sensor(), new Sensor());
        when(mockSensorRepository.getGpsSensor()).thenReturn(gpsSensor);
        gpsSensor.setPosition(lat,lon);
        var messageKey = "Position";
        // Run the test
        final Object result = notificationFormatDiscordServiceUnderTest.getFormattedMessage(messageKey);

        // Verify the results
        Assert.assertTrue(result instanceof EmbedBuilder);
        Assert.assertEquals(Double.toString(lat),((EmbedBuilder) result).getFields().get(0).getValue());
        Assert.assertEquals("Latitude",((EmbedBuilder) result).getFields().get(0).getName());

        Assert.assertEquals(Double.toString(lon),((EmbedBuilder) result).getFields().get(1).getValue());
        Assert.assertEquals("Longitude",((EmbedBuilder) result).getFields().get(1).getName());

        Assert.assertTrue(Objects.requireNonNull(((EmbedBuilder) result).getFields().get(2).getValue()).contains(Double.toString(lat)));
        Assert.assertTrue(Objects.requireNonNull(((EmbedBuilder) result).getFields().get(2).getValue()).contains(Double.toString(lon)));
        Assert.assertEquals(notificationFormatDiscordServiceUnderTest.googleMap + lat + "," + lon, Objects.requireNonNull(((EmbedBuilder) result).getFields().get(2).getValue()));
        Assert.assertEquals("Map",((EmbedBuilder) result).getFields().get(2).getName());
    }

    @Test
    void testGetFormattedMessageCurrent() {
        // Setup

        // Configure SensorRepository.getAllBasicSensors(...).
        final String sensorName = "testName";
        final Sensor sensor = new Sensor();
        sensor.setName(sensorName);
        final Map<String, Sensor> stringSensorMap = Map.ofEntries(Map.entry(sensor.getName(), sensor));
        when(mockSensorRepository.getAllBasicSensors()).thenReturn(stringSensorMap);

        var messageKey = "Data";
        // Run the test
        sensor.setValue(10.0);
        final Object result = notificationFormatDiscordServiceUnderTest.getFormattedMessage(messageKey);

        // Verify the results
        Assert.assertTrue(result instanceof EmbedBuilder);
        Assert.assertTrue(((EmbedBuilder) result).getFields().get(0).getValue().contains(Double.toString(10.0)));
        Assert.assertEquals(sensor.getName(),((EmbedBuilder) result).getFields().get(0).getName());

        // Run the test
        sensor.setValue(8.0);
        final Object result2 = notificationFormatDiscordServiceUnderTest.getFormattedMessage(messageKey);

        // Verify the results
        Assert.assertTrue(result2 instanceof EmbedBuilder);
        Assert.assertTrue(((EmbedBuilder) result2).getFields().get(0).getValue().contains(Double.toString(8.0)));
        Assert.assertEquals(sensor.getName(),((EmbedBuilder) result2).getFields().get(0).getName());

        // Run the test
        sensor.setValue(12.0);
        sensor.setName("testNameChanged");
        final Object result3 = notificationFormatDiscordServiceUnderTest.getFormattedMessage(messageKey);

        // Verify the results
        Assert.assertTrue(result3 instanceof EmbedBuilder);
        Assert.assertTrue(((EmbedBuilder) result3).getFields().get(0).getValue().contains(Double.toString(12.0)));
        Assert.assertEquals(sensor.getName(),((EmbedBuilder) result3).getFields().get(0).getName());

        // Run the test
        sensor.setValue(-9999.9999);
        sensor.setName(sensorName);
        final Object result4 = notificationFormatDiscordServiceUnderTest.getFormattedMessage(messageKey);

        // Verify the results
        Assert.assertTrue(result4 instanceof EmbedBuilder);
        Assert.assertTrue(((EmbedBuilder) result4).getFields().get(0).getValue().contains(Double.toString(-9999.9999)));
        Assert.assertEquals(sensor.getName(),((EmbedBuilder) result4).getFields().get(0).getName());
    }

    @Test
    void testGetFormattedMessageMax() {
        // Setup

        // Configure SensorRepository.getAllBasicSensors(...).
        final String sensorName = "testName";
        final Sensor sensor = new Sensor();
        sensor.setName(sensorName);
        final Map<String, Sensor> stringSensorMap = Map.ofEntries(Map.entry(sensor.getName(), sensor));
        when(mockSensorRepository.getAllBasicSensors()).thenReturn(stringSensorMap);

        var messageKey = "Max";
        // Run the test
        sensor.setValue(10.0);
        final Object result = notificationFormatDiscordServiceUnderTest.getFormattedMessage(messageKey);

        // Verify the results
        Assert.assertTrue(result instanceof EmbedBuilder);
        Assert.assertTrue(((EmbedBuilder) result).getFields().get(0).getValue().contains(Double.toString(10.0)));
        Assert.assertEquals(sensor.getName(),((EmbedBuilder) result).getFields().get(0).getName());

        // Run the test
        sensor.setValue(8.0);
        final Object result2 = notificationFormatDiscordServiceUnderTest.getFormattedMessage(messageKey);

        // Verify the results
        Assert.assertTrue(result2 instanceof EmbedBuilder);
        Assert.assertTrue(((EmbedBuilder) result2).getFields().get(0).getValue().contains(Double.toString(10.0)));
        Assert.assertEquals(sensor.getName(),((EmbedBuilder) result2).getFields().get(0).getName());

        // Run the test
        sensor.setValue(12.0);
        sensor.setName("testNameChanged");
        final Object result3 = notificationFormatDiscordServiceUnderTest.getFormattedMessage(messageKey);

        // Verify the results
        Assert.assertTrue(result3 instanceof EmbedBuilder);
        Assert.assertTrue(((EmbedBuilder) result3).getFields().get(0).getValue().contains(Double.toString(12.0)));
        Assert.assertEquals(sensor.getName(),((EmbedBuilder) result3).getFields().get(0).getName());

        // Run the test
        sensor.setValue(-9999.9999);
        sensor.setName(sensorName);
        final Object result4 = notificationFormatDiscordServiceUnderTest.getFormattedMessage(messageKey);

        // Verify the results
        Assert.assertTrue(result4 instanceof EmbedBuilder);
        Assert.assertTrue(((EmbedBuilder) result4).getFields().get(0).getValue().contains(Double.toString(12.0)));
        Assert.assertEquals(sensor.getName(),((EmbedBuilder) result4).getFields().get(0).getName());
    }

    @Test
    void testGetFormattedMessageStatus() {
        // Setup

        // Configure SensorRepository.getAllBasicSensors(...).
        Mockito.doNothing().when(mockNotificationThread).run();

        var messageKey = "Thread status";
        // Run the test
        final Object result = notificationFormatDiscordServiceUnderTest.getFormattedMessage(messageKey);

        // Verify the results
        Assert.assertTrue(result instanceof EmbedBuilder);
        Assert.assertEquals("There is no notification thread active.",((EmbedBuilder) result).getFields().get(0).getValue());
        Assert.assertEquals("Status",((EmbedBuilder) result).getFields().get(0).getName());

    }

    @Test
    void testGetFormattedMessageStatus2() {
        // Setup

        // Configure
        final Thread thread = new Thread(mockNotificationThread, ThreadName.DISCORD_NOTIFICATION.getName());
        Mockito.doAnswer((Answer<Void>) invocationOnMock -> {
            try {
                Thread.sleep(Integer.MAX_VALUE);
            } catch (InterruptedException e) {

            }
            return null;
        }).when(mockNotificationThread).run();
        thread.start();

        var messageKey = "Thread status";
        // Run the test
        final Object result = notificationFormatDiscordServiceUnderTest.getFormattedMessage(messageKey);

        // Verify the results
        Assert.assertTrue(result instanceof EmbedBuilder);
        Assert.assertEquals(thread.getState().toString(),((EmbedBuilder) result).getFields().get(0).getValue());
        Assert.assertEquals("State:",((EmbedBuilder) result).getFields().get(0).getName());

        Assert.assertEquals(thread.isAlive() ? "Yes": "No",((EmbedBuilder) result).getFields().get(1).getValue());
        Assert.assertEquals("Is alive:",((EmbedBuilder) result).getFields().get(1).getName());
        thread.interrupt();
    }
}
