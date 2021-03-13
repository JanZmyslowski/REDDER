package pl.edu.pwr.pwrinspace.poliwrocket.Model.Configuration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.edu.pwr.pwrinspace.poliwrocket.Controller.BasicController.BasicButtonController;
import pl.edu.pwr.pwrinspace.poliwrocket.Controller.BasicController.BasicButtonSensorController;
import pl.edu.pwr.pwrinspace.poliwrocket.Controller.BasicController.BasicController;
import pl.edu.pwr.pwrinspace.poliwrocket.Controller.BasicController.BasicSensorController;
import pl.edu.pwr.pwrinspace.poliwrocket.Controller.ControllerNameEnum;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.Command.Command;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.MessageParser.MessageParserEnum;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.Notification.Schedule;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.Sensor.GPSSensor;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.Sensor.GyroSensor;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.Sensor.Sensor;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.Sensor.SensorRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

class ConfigurationTest {

    private Configuration configurationUnderTest;

    @BeforeEach
    void setUp() {
        configurationUnderTest = Configuration.getInstance();
    }

    @Test
    void testSetupConfigInstance() {
        // Setup
        final ConfigurationSaveModel config = defaultTestConfiguration();

        // Run the test
        configurationUnderTest.setupConfigInstance(config);

        // Verify the results
        assertEquals(config.FPS,configurationUnderTest.FPS);
        assertEquals(config.START_POSITION_LAT,configurationUnderTest.START_POSITION_LAT,0.001);
        assertEquals(config.START_POSITION_LON,configurationUnderTest.START_POSITION_LON,0.001);
        assertEquals(config.PARSER_TYPE,configurationUnderTest.PARSER_TYPE);
        assertEquals(config.DISCORD_TOKEN,configurationUnderTest.DISCORD_TOKEN);
        assertEquals(config.DISCORD_CHANNEL_NAME,configurationUnderTest.DISCORD_CHANNEL_NAME);
        assertEquals(config.FRAME_DELIMITER,configurationUnderTest.FRAME_DELIMITER);
        assertEquals(config.FRAME_PATTERN,configurationUnderTest.FRAME_PATTERN);
        assertEquals(config.commandsList,configurationUnderTest.commandsList);
        assertEquals(config.notificationSchedule,configurationUnderTest.notificationSchedule);
        assertEquals(config.notificationMessageKeys,configurationUnderTest.notificationMessageKeys);
        assertEquals(config.sensorRepository,configurationUnderTest.sensorRepository);
    }

    @Test
    void testSetupApplicationConfig() {
        // Setup

        //prepare mocks
        var mockControllerS = mock(BasicSensorController.class);
        var mockControllerB = mock(BasicButtonController.class);
        var mockControllerBS = mock(BasicButtonSensorController.class);
        when(mockControllerB.getControllerNameEnum()).thenReturn(ControllerNameEnum.START_CONTROL_CONTROLLER);
        when(mockControllerS.getControllerNameEnum()).thenReturn(ControllerNameEnum.DATA_CONTROLLER);
        when(mockControllerBS.getControllerNameEnum()).thenReturn(ControllerNameEnum.VALVES_CONTROLLER);
        var mockSensorS1 = mock(Sensor.class);
        when(mockSensorS1.getDestinationControllerNames()).thenReturn(Arrays.asList(ControllerNameEnum.DATA_CONTROLLER));
        when(mockSensorS1.getName()).thenReturn("S1");
        var mockSensorS2 = mock(Sensor.class);
        when(mockSensorS2.getDestinationControllerNames()).thenReturn(Arrays.asList(ControllerNameEnum.DATA_CONTROLLER));
        when(mockSensorS2.getName()).thenReturn("S2");
        var mockSensorS3 = mock(Sensor.class);
        when(mockSensorS3.getDestinationControllerNames()).thenReturn(Arrays.asList(ControllerNameEnum.DATA_CONTROLLER));
        when(mockSensorS3.getName()).thenReturn("S3");
        var mockSensorBS1 = mock(Sensor.class);
        when(mockSensorBS1.getDestinationControllerNames()).thenReturn(Arrays.asList(ControllerNameEnum.VALVES_CONTROLLER));
        when(mockSensorBS1.getName()).thenReturn("BS1");
        var mockSensorBS2 = mock(Sensor.class);
        when(mockSensorBS2.getDestinationControllerNames()).thenReturn(Arrays.asList(ControllerNameEnum.VALVES_CONTROLLER));
        when(mockSensorBS2.getName()).thenReturn("BS2");
        var mockCommandB1 = mock(Command.class);
        when(mockCommandB1.getDestinationControllerNames()).thenReturn(Arrays.asList(ControllerNameEnum.START_CONTROL_CONTROLLER));
        var mockCommandB2 = mock(Command.class);
        when(mockCommandB2.getDestinationControllerNames()).thenReturn(Arrays.asList(ControllerNameEnum.START_CONTROL_CONTROLLER));
        var mockCommandBS1 = mock(Command.class);
        when(mockCommandBS1.getDestinationControllerNames()).thenReturn(Arrays.asList(ControllerNameEnum.VALVES_CONTROLLER));
        var mockCommandBS2 = mock(Command.class);
        when(mockCommandBS2.getDestinationControllerNames()).thenReturn(Arrays.asList(ControllerNameEnum.VALVES_CONTROLLER));
        var mockCommandBS3 = mock(Command.class);
        when(mockCommandBS3.getDestinationControllerNames()).thenReturn(Arrays.asList(ControllerNameEnum.VALVES_CONTROLLER));

        //prepare func arg
       final List<BasicController> controllerList = List.of(mockControllerS,mockControllerB,mockControllerBS);
        //prepare config instance
        Configuration.getInstance().sensorRepository = new SensorRepository();
        Configuration.getInstance().commandsList = new ArrayList<>();
        Configuration.getInstance().sensorRepository.addSensor(mockSensorS1);
        Configuration.getInstance().sensorRepository.addSensor(mockSensorS2);
        Configuration.getInstance().sensorRepository.addSensor(mockSensorS3);
        Configuration.getInstance().sensorRepository.addSensor(mockSensorBS1);
        Configuration.getInstance().sensorRepository.addSensor(mockSensorBS2);
        Configuration.getInstance().commandsList.add(mockCommandBS1);
        Configuration.getInstance().commandsList.add(mockCommandBS2);
        Configuration.getInstance().commandsList.add(mockCommandBS3);
        Configuration.getInstance().commandsList.add(mockCommandB1);
        Configuration.getInstance().commandsList.add(mockCommandB2);

        // Run the test
        Configuration.setupApplicationConfig(controllerList);

        // Verify the results
        verify(mockControllerS,times(1)).injectSensorsModels(List.of(mockSensorS1,mockSensorS2,mockSensorS3));
        verify(mockControllerBS,times(1)).injectSensorsModels(List.of(mockSensorBS1,mockSensorBS2));
        verify(mockControllerBS,times(1)).assignsCommands(List.of(mockCommandBS1,mockCommandBS2,mockCommandBS3));
        verify(mockControllerB,times(1)).assignsCommands(List.of(mockCommandB1,mockCommandB2));
    }

    @Test
    void testSetupApplicationConfigEmpty() {
        // Setup

        //prepare mocks
        var mockControllerS = mock(BasicSensorController.class);
        var mockControllerB = mock(BasicButtonController.class);
        var mockControllerBS = mock(BasicButtonSensorController.class);
        when(mockControllerB.getControllerNameEnum()).thenReturn(ControllerNameEnum.START_CONTROL_CONTROLLER);
        when(mockControllerS.getControllerNameEnum()).thenReturn(ControllerNameEnum.DATA_CONTROLLER);
        when(mockControllerBS.getControllerNameEnum()).thenReturn(ControllerNameEnum.VALVES_CONTROLLER);
        var mockSensorS1 = mock(Sensor.class);
        when(mockSensorS1.getDestinationControllerNames()).thenReturn(Arrays.asList(ControllerNameEnum.DATA_CONTROLLER));
        when(mockSensorS1.getName()).thenReturn("S1");
        var mockSensorS2 = mock(Sensor.class);
        when(mockSensorS2.getDestinationControllerNames()).thenReturn(Arrays.asList(ControllerNameEnum.DATA_CONTROLLER));
        when(mockSensorS2.getName()).thenReturn("S2");
        var mockSensorS3 = mock(Sensor.class);
        when(mockSensorS3.getDestinationControllerNames()).thenReturn(Arrays.asList(ControllerNameEnum.DATA_CONTROLLER));
        when(mockSensorS3.getName()).thenReturn("S3");
        var mockSensorBS1 = mock(Sensor.class);
        when(mockSensorBS1.getDestinationControllerNames()).thenReturn(Arrays.asList(ControllerNameEnum.VALVES_CONTROLLER));
        when(mockSensorBS1.getName()).thenReturn("BS1");
        var mockSensorBS2 = mock(Sensor.class);
        when(mockSensorBS2.getDestinationControllerNames()).thenReturn(Arrays.asList(ControllerNameEnum.VALVES_CONTROLLER));
        when(mockSensorBS2.getName()).thenReturn("BS2");
        var mockCommandB1 = mock(Command.class);
        when(mockCommandB1.getDestinationControllerNames()).thenReturn(Arrays.asList(ControllerNameEnum.START_CONTROL_CONTROLLER));
        var mockCommandB2 = mock(Command.class);
        when(mockCommandB2.getDestinationControllerNames()).thenReturn(Arrays.asList(ControllerNameEnum.START_CONTROL_CONTROLLER));
        var mockCommandBS1 = mock(Command.class);
        when(mockCommandBS1.getDestinationControllerNames()).thenReturn(Arrays.asList(ControllerNameEnum.VALVES_CONTROLLER));
        var mockCommandBS2 = mock(Command.class);
        when(mockCommandBS2.getDestinationControllerNames()).thenReturn(Arrays.asList(ControllerNameEnum.VALVES_CONTROLLER));
        var mockCommandBS3 = mock(Command.class);
        when(mockCommandBS3.getDestinationControllerNames()).thenReturn(Arrays.asList(ControllerNameEnum.VALVES_CONTROLLER));

        //prepare func arg
       final List<BasicController> controllerList = new LinkedList<>();
        //prepare config instance
        Configuration.getInstance().sensorRepository = new SensorRepository();
        Configuration.getInstance().commandsList = new ArrayList<>();
        Configuration.getInstance().sensorRepository.addSensor(mockSensorS1);
        Configuration.getInstance().sensorRepository.addSensor(mockSensorS2);
        Configuration.getInstance().sensorRepository.addSensor(mockSensorS3);
        Configuration.getInstance().sensorRepository.addSensor(mockSensorBS1);
        Configuration.getInstance().sensorRepository.addSensor(mockSensorBS2);
        Configuration.getInstance().commandsList.add(mockCommandBS1);
        Configuration.getInstance().commandsList.add(mockCommandBS2);
        Configuration.getInstance().commandsList.add(mockCommandBS3);
        Configuration.getInstance().commandsList.add(mockCommandB1);
        Configuration.getInstance().commandsList.add(mockCommandB2);

        // Run the test
        Configuration.setupApplicationConfig(controllerList);

        // Verify the results
        verify(mockControllerS,never()).injectSensorsModels(List.of(mockSensorS1,mockSensorS2,mockSensorS3));
        verify(mockControllerBS,never()).injectSensorsModels(List.of(mockSensorBS1,mockSensorBS2));
        verify(mockControllerBS,never()).assignsCommands(List.of(mockCommandBS1,mockCommandBS2,mockCommandBS3));
        verify(mockControllerB,never()).assignsCommands(List.of(mockCommandB1,mockCommandB2));
    }

    @Test
    void testGetInstance() {
        // Setup

        // Run the test
        final Configuration result = Configuration.getInstance();

        // Verify the results
        assertEquals(Configuration.getInstance(),result);
    }

    private static ConfigurationSaveModel defaultTestConfiguration() {
        ConfigurationSaveModel defaultConfig = new ConfigurationSaveModel();
        defaultConfig.sensorRepository = new SensorRepository();
        defaultConfig.FPS = 2;
        defaultConfig.PARSER_TYPE = MessageParserEnum.STANDARD;
        defaultConfig.commandsList = new LinkedList<>();
        defaultConfig.DISCORD_CHANNEL_NAME = "rocket";
        defaultConfig.DISCORD_TOKEN = "token";
        defaultConfig.START_POSITION_LON = 16.9333977;
        defaultConfig.START_POSITION_LAT = 51.1266727;

        Sensor basicSensor = new Sensor();
        basicSensor.setDestination("dataGauge1");
        basicSensor.getDestinationControllerNames().add(ControllerNameEnum.DATA_CONTROLLER);
        defaultConfig.sensorRepository.addSensor(basicSensor);

        //utworzenie 3xSensor for GYRO
        Sensor gryro1 = new Sensor();
        gryro1.setDestination("dataGauge3");
        gryro1.setName("Gyro X");
        gryro1.getDestinationControllerNames().add(ControllerNameEnum.DATA_CONTROLLER);

        Sensor gryro2 = new Sensor();
        gryro2.setDestination("dataGauge5");
        gryro2.setName("Gyro Y");
        gryro2.getDestinationControllerNames().add(ControllerNameEnum.DATA_CONTROLLER);

        Sensor gryro3 = new Sensor();
        gryro3.setDestination("dataGauge7");
        gryro3.setName("Gyro Z");
        gryro3.getDestinationControllerNames().add(ControllerNameEnum.DATA_CONTROLLER);

        //nowy gryo
        GyroSensor gyroSensor = new GyroSensor(gryro1, gryro2, gryro3);
        gyroSensor.getDestinationControllerNames().add(ControllerNameEnum.MAIN_CONTROLLER);
        defaultConfig.sensorRepository.setGyroSensor(gyroSensor);
        //--------

        //nowy gps
        Sensor latitude = new Sensor();
        latitude.setName("lat");
        Sensor longitude = new Sensor();
        longitude.setName("long");

        GPSSensor gpsSensor = new GPSSensor(latitude, longitude);
        gpsSensor.getDestinationControllerNames().add(ControllerNameEnum.MAP_CONTROLLER);
        defaultConfig.sensorRepository.setGpsSensor(gpsSensor);
        //--------

        //komendy
        Command command = new Command("open valveOpenButton1", "valveOpenButton1");
        command.getDestinationControllerNames().add(ControllerNameEnum.VALVES_CONTROLLER);
        defaultConfig.commandsList.add(command);
        Command command2 = new Command("open valveOpenButton2", "valveOpenButton2");
        command2.getDestinationControllerNames().add(ControllerNameEnum.VALVES_CONTROLLER);
        defaultConfig.commandsList.add(command2);
        Command command3 = new Command("open valveOpenButton3", "valveOpenButton3");
        command3.getDestinationControllerNames().add(ControllerNameEnum.VALVES_CONTROLLER);
        defaultConfig.commandsList.add(command3);
        Command command4 = new Command("open valveOpenButton4", "valveOpenButton4");
        command4.getDestinationControllerNames().add(ControllerNameEnum.VALVES_CONTROLLER);
        defaultConfig.commandsList.add(command4);
        Command command5 = new Command("test1", "test1");
        command5.getDestinationControllerNames().add(ControllerNameEnum.CONNECTION_CONTROLLER);
        defaultConfig.commandsList.add(command5);
        Command command6 = new Command("test2", "test2");
        command6.getDestinationControllerNames().add(ControllerNameEnum.CONNECTION_CONTROLLER);
        defaultConfig.commandsList.add(command6);
        Command abort = new Command("ABORT", "abortButton");
        abort.getDestinationControllerNames().add(ControllerNameEnum.ABORT_CONTROLLER);
        defaultConfig.commandsList.add(abort);
        Command fire = new Command("FIRE", "fireButton");
        fire.getDestinationControllerNames().add(ControllerNameEnum.START_CONTROL_CONTROLLER);
        defaultConfig.commandsList.add(fire);
        //--------

        //frame
        defaultConfig.FRAME_DELIMITER = ",";
        defaultConfig.FRAME_PATTERN.add("Gyro X");
        defaultConfig.FRAME_PATTERN.add("Gyro Y");
        defaultConfig.FRAME_PATTERN.add("Gyro Z");
        defaultConfig.FRAME_PATTERN.add("Velocity");
        defaultConfig.FRAME_PATTERN.add("Altitude2");
        defaultConfig.FRAME_PATTERN.add("lat");
        defaultConfig.FRAME_PATTERN.add("long");
        defaultConfig.FRAME_PATTERN.add("Ind 1");
        defaultConfig.PARSER_TYPE = MessageParserEnum.STANDARD;
        //

        Sensor velocity = new Sensor();
        velocity.setDestination("dataGauge9");
        velocity.setName("Velocity");
        velocity.setMinRange(0);
        velocity.setMaxRange(400);
        velocity.setUnit("m/s");
        velocity.getDestinationControllerNames().add(ControllerNameEnum.MORE_DATA_CONTROLLER);
        defaultConfig.sensorRepository.addSensor(velocity);

        Sensor altitude = new Sensor();
        altitude.setDestination("dataGauge10");
        altitude.setName("Altitude2");
        altitude.setMinRange(0);
        altitude.setMaxRange(4500);
        altitude.setUnit("m");
        altitude.getDestinationControllerNames().add(ControllerNameEnum.MORE_DATA_CONTROLLER);
        defaultConfig.sensorRepository.addSensor(altitude);

        Sensor indicator1 = new Sensor();
        indicator1.setDestination("dataIndicator1");
        indicator1.setName("Ind 1");
        indicator1.setBoolean(true);
        indicator1.getDestinationControllerNames().add(ControllerNameEnum.MORE_DATA_CONTROLLER);
        defaultConfig.sensorRepository.addSensor(indicator1);
        Sensor indicator2 = new Sensor();
        indicator2.setDestination("dataIndicator2");
        indicator2.setName("Ind 2");
        indicator2.setBoolean(true);
        indicator2.getDestinationControllerNames().add(ControllerNameEnum.MORE_DATA_CONTROLLER);
        defaultConfig.sensorRepository.addSensor(indicator2);
        Sensor indicator3 = new Sensor();
        indicator3.setDestination("dataIndicator3");
        indicator3.setName("Ind 3");
        indicator3.setBoolean(true);
        indicator3.getDestinationControllerNames().add(ControllerNameEnum.MORE_DATA_CONTROLLER);
        defaultConfig.sensorRepository.addSensor(indicator3);
        Sensor indicator4 = new Sensor();
        indicator4.setDestination("dataIndicator4");
        indicator4.setName("Ind 4");
        indicator4.setBoolean(true);
        indicator4.getDestinationControllerNames().add(ControllerNameEnum.MORE_DATA_CONTROLLER);
        defaultConfig.sensorRepository.addSensor(indicator4);

        //notification
        List<String> notificationsListStrings = new ArrayList<>();
        notificationsListStrings.add("Map");
        notificationsListStrings.add("Position");
        notificationsListStrings.add("Data");
        notificationsListStrings.add("Max");
        notificationsListStrings.add("Thread status");
        defaultConfig.notificationMessageKeys = notificationsListStrings;

        List<Schedule> schedules = new ArrayList<>();
        schedules.add( new Schedule("Map",5));
        schedules.add( new Schedule("Data",10));
        defaultConfig.notificationSchedule = schedules;
        //---------------

        //power
        Sensor power1 = new Sensor();
        power1.setMaxRange(8.2);
        power1.setMinRange(7.2);
        power1.setName("Main computer");
        power1.setDestination("powerGauge1");
        power1.setUnit("V");
        power1.getDestinationControllerNames().add(ControllerNameEnum.POWER_CONTROLLER);
        defaultConfig.sensorRepository.addSensor(power1);
        Sensor power2 = new Sensor();
        power2.setMaxRange(8.2);
        power2.setMinRange(7.2);
        power2.setName("Recovery 1");
        power2.setDestination("powerGauge2");
        power2.setUnit("V");
        power2.getDestinationControllerNames().add(ControllerNameEnum.POWER_CONTROLLER);
        defaultConfig.sensorRepository.addSensor(power2);
        Sensor power3 = new Sensor();
        power3.setMaxRange(8.2);
        power3.setMinRange(7.2);
        power3.setName("Recovery 2");
        power3.setDestination("powerGauge3");
        power3.setUnit("V");
        power3.getDestinationControllerNames().add(ControllerNameEnum.POWER_CONTROLLER);
        defaultConfig.sensorRepository.addSensor(power3);

        //---------------
        return defaultConfig;
    }
}
