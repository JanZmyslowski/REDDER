package pl.edu.pwr.pwrinspace.poliwrocket.Model.Configuration;

import com.google.gson.annotations.Expose;
import pl.edu.pwr.pwrinspace.poliwrocket.Controller.ControllerNameEnum;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.BaseSaveModel;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.Command.Command;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.MessageParser.MessageParserEnum;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.Notification.Schedule;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.Sensor.GPSSensor;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.Sensor.GyroSensor;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.Sensor.ISensor;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.Sensor.Sensor;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.Sensor.SensorRepository;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class ConfigurationSaveModel extends BaseSaveModel {

    @Expose
    public int FPS = 10;

    @Expose
    public double START_POSITION_LAT;

    @Expose
    public double START_POSITION_LON;

    @Expose
    public MessageParserEnum PARSER_TYPE = MessageParserEnum.STANDARD;

    @Expose
    public String FRAME_DELIMITER = ",";

    @Expose
    public String DISCORD_TOKEN = "";

    @Expose
    public String DISCORD_CHANNEL_NAME = "rocket";

    @Expose
    public List<String> FRAME_PATTERN = new ArrayList<>();

    @Expose
    public List<Command> commandsList = new LinkedList<>();

    @Expose
    public List<Schedule> notificationSchedule = new LinkedList<>();

    @Expose
    public List<String> notificationMessageKeys = new LinkedList<>();

    @Expose
    public SensorRepository sensorRepository = new SensorRepository();

    public ConfigurationSaveModel() {
        super(Configuration.CONFIG_PATH, Configuration.CONFIG_FILE_NAME);
    }

    public static ConfigurationSaveModel getConfigurationSaveModel(Configuration configuration) {
        ConfigurationSaveModel config = new ConfigurationSaveModel();
        config.FPS = configuration.FPS;
        config.START_POSITION_LAT = configuration.START_POSITION_LAT;
        config.START_POSITION_LON = configuration.START_POSITION_LON;
        config.PARSER_TYPE = configuration.PARSER_TYPE;
        config.FRAME_DELIMITER = configuration.FRAME_DELIMITER;
        config.FRAME_PATTERN = configuration.FRAME_PATTERN;
        config.DISCORD_TOKEN = configuration.DISCORD_TOKEN;
        config.DISCORD_CHANNEL_NAME = configuration.DISCORD_CHANNEL_NAME;
        config.commandsList = configuration.commandsList;
        config.notificationMessageKeys = configuration.notificationMessageKeys;
        config.notificationSchedule = configuration.notificationSchedule;
        config.sensorRepository.setGpsSensor(configuration.sensorRepository.getGpsSensor());
        config.sensorRepository.setGyroSensor(configuration.sensorRepository.getGyroSensor());
        List<ISensor> partOfSensor = new ArrayList<>();
        partOfSensor.add(configuration.sensorRepository.getGpsSensor().getLatitude());
        partOfSensor.add(configuration.sensorRepository.getGpsSensor().getLongitude());
        partOfSensor.add(configuration.sensorRepository.getGyroSensor().getAxis_x());
        partOfSensor.add(configuration.sensorRepository.getGyroSensor().getAxis_y());
        partOfSensor.add(configuration.sensorRepository.getGyroSensor().getAxis_z());
        configuration.sensorRepository.getSensorsKeys().forEach(s -> {
            if(!partOfSensor.contains(configuration.sensorRepository.getSensorByName(s))){
                config.sensorRepository.addSensor(configuration.sensorRepository.getSensorByName(s));
            }
        });

        return config;
    }

    public static ConfigurationSaveModel defaultConfiguration() {
        ConfigurationSaveModel defaultConfig = new ConfigurationSaveModel();
        defaultConfig.sensorRepository = new SensorRepository();
        defaultConfig.FPS = 10;
        defaultConfig.PARSER_TYPE = MessageParserEnum.STANDARD;
        defaultConfig.commandsList = new LinkedList<>();
        defaultConfig.DISCORD_CHANNEL_NAME = "";
        defaultConfig.DISCORD_TOKEN = "";
        defaultConfig.START_POSITION_LON = 16.9333977;
        defaultConfig.START_POSITION_LAT = 51.1266727;

        Sensor basicSensor = new Sensor();
        basicSensor.setName("Altitude");
        basicSensor.setDestination("dataGauge1");
        basicSensor.setMaxRange(2000);
        basicSensor.setMinRange(0);
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
