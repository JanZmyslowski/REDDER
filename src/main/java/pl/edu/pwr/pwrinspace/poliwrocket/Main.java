package pl.edu.pwr.pwrinspace.poliwrocket;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.sothawo.mapjfx.Projection;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.edu.pwr.pwrinspace.poliwrocket.Controller.*;
import pl.edu.pwr.pwrinspace.poliwrocket.Controller.BasicController.BasicController;
import pl.edu.pwr.pwrinspace.poliwrocket.Event.Discord.NotificationDiscordEvent;
import pl.edu.pwr.pwrinspace.poliwrocket.Event.NotificationEvent;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.Configuration.Configuration;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.Configuration.ConfigurationSaveModel;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.MessageParser.*;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.Notification.DiscordNotification;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.Notification.INotification;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.SerialPort.SerialPortManager;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.Speech.TextToSpeechDictionary;
import pl.edu.pwr.pwrinspace.poliwrocket.Service.Notification.NotificationFormatDiscordService;
import pl.edu.pwr.pwrinspace.poliwrocket.Service.Notification.NotificationFormatService;
import pl.edu.pwr.pwrinspace.poliwrocket.Service.Notification.NotificationSendService;
import pl.edu.pwr.pwrinspace.poliwrocket.Service.Rule.RuleValidationService;
import pl.edu.pwr.pwrinspace.poliwrocket.Service.Save.ModelAsJsonSaveService;
import pl.edu.pwr.pwrinspace.poliwrocket.Service.Save.FrameSaveService;
import pl.edu.pwr.pwrinspace.poliwrocket.Service.Speech.TextToSpeechService;
import pl.edu.pwr.pwrinspace.poliwrocket.Thred.Notification.NotificationThread;
import pl.edu.pwr.pwrinspace.poliwrocket.Thred.UI.UIThreadManager;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

public class Main extends Application {

    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    private final ModelAsJsonSaveService modelAsJsonSaveService = new ModelAsJsonSaveService();
    private final FrameSaveService frameSaveService = new FrameSaveService();
    private NotificationSendService notificationSendService;
    private NotificationThread notificationThread;
    private NotificationFormatService notificationFormatService;
    private IMessageParser messageParser;
    private NotificationEvent notificationEvent;
    private TextToSpeechService ttsService;
    private TextToSpeechDictionary textToSpeechDictionary;
    private final RuleValidationService ruleValidationService = new RuleValidationService();

    @Override
    public void start(Stage primaryStage) {
        try {
            //Read config file
            try {
                Configuration.getInstance().setupConfigInstance((ConfigurationSaveModel) modelAsJsonSaveService.readFromFile(new ConfigurationSaveModel()));
            } catch (Exception e) {
                logger.error("Bad config file, overwritten by default and loaded");
                logger.error(e.getMessage());
                logger.error(Arrays.toString(e.getStackTrace()));
                logger.error(e.toString());
                modelAsJsonSaveService.persistOldFile(new ConfigurationSaveModel());
                modelAsJsonSaveService.saveToFile(ConfigurationSaveModel.defaultConfiguration());
                Configuration.getInstance().setupConfigInstance((ConfigurationSaveModel) modelAsJsonSaveService.readFromFile(new ConfigurationSaveModel()));
            }
            //--------------

            //Read speech file
            try {
                textToSpeechDictionary = (TextToSpeechDictionary) modelAsJsonSaveService.readFromFile(new TextToSpeechDictionary());
            } catch (Exception e) {
                logger.error("Bad speech file, overwritten by default and loaded");
                logger.error(e.getMessage());
                logger.error(Arrays.toString(e.getStackTrace()));
                logger.error(e.toString());
                modelAsJsonSaveService.persistOldFile(new TextToSpeechDictionary());
                modelAsJsonSaveService.saveToFile(TextToSpeechDictionary.defaultModel());
                textToSpeechDictionary = (TextToSpeechDictionary) modelAsJsonSaveService.readFromFile(new TextToSpeechDictionary());
            }
            //--------------

            //FXMLLoader
            FXMLLoader loaderMain = new FXMLLoader(getClass().getClassLoader().getResource("MainView.fxml"));
            FXMLLoader loaderData = new FXMLLoader(getClass().getClassLoader().getResource("DataView.fxml"));
            FXMLLoader loaderMap = new FXMLLoader(getClass().getClassLoader().getResource("MapViewNew.fxml"));
            FXMLLoader loaderPower = new FXMLLoader(getClass().getClassLoader().getResource("PowerView.fxml"));
            FXMLLoader loaderValves = new FXMLLoader(getClass().getClassLoader().getResource("ValvesView.fxml"));
            FXMLLoader loaderMoreData = new FXMLLoader(getClass().getClassLoader().getResource("MoreDataView.fxml"));
            FXMLLoader loaderAbort = new FXMLLoader(getClass().getClassLoader().getResource("AbortView.fxml"));
            FXMLLoader loaderStates = new FXMLLoader(getClass().getClassLoader().getResource("StatesView.fxml"));
            FXMLLoader loaderStart = new FXMLLoader(getClass().getClassLoader().getResource("StartControlView.fxml"));
            FXMLLoader loaderConnection = new FXMLLoader(getClass().getClassLoader().getResource("ConnectionView.fxml"));

            Scene scene = new Scene(loaderMain.load(), 1550, 750);
            //--------------

            //Controllers
            MainController mainController = loaderMain.getController();
            mainController.initSubScenes(loaderData, loaderMap, loaderPower, loaderValves, loaderMoreData,
                    loaderAbort, loaderStates, loaderStart, loaderConnection);
            mainController.setPrimaryStage(primaryStage);

            DataController dataController = loaderData.getController();
            NewMapController mapController = loaderMap.getController();
            final Projection projection = getParameters().getUnnamed().contains("wgs84")
                    ? Projection.WGS_84 : Projection.WEB_MERCATOR;
            mapController.initMapAndControls(projection);

            PowerController powerController = loaderPower.getController();
            ValvesController valvesController = loaderValves.getController();
            MoreDataController moreDataController = loaderMoreData.getController();
            AbortController abortController = loaderAbort.getController();
            StatesController statesController = loaderStates.getController();
            StartControlController startControlController = loaderStart.getController();
            ConnectionController connectionController = loaderConnection.getController();
            //--------------

            //Mapping sensors and commands to controllers
            List<BasicController> controllerList = new ArrayList<>();
            controllerList.add(mainController);
            controllerList.add(dataController);
            controllerList.add(mapController);
            controllerList.add(powerController);
            controllerList.add(valvesController);
            controllerList.add(moreDataController);
            controllerList.add(abortController);
            controllerList.add(statesController);
            controllerList.add(startControlController);
            controllerList.add(connectionController);
            Configuration.setupApplicationConfig(controllerList);
            //--------------

            //IMessageParser setup
            if(Configuration.getInstance().PARSER_TYPE == MessageParserEnum.JSON){
                messageParser = new JsonMessageParser(Configuration.getInstance().sensorRepository);
            } else if (Configuration.getInstance().PARSER_TYPE == MessageParserEnum.STANDARD) {
                messageParser = new StandardMessageParser(Configuration.getInstance().sensorRepository);
            } else {
                messageParser = new StandardMessageParser(Configuration.getInstance().sensorRepository);
            }
            messageParser.addListener(mainController);
            messageParser.addListener(UIThreadManager.getInstance());
            //--------------

            //FrameSaveService setup
            frameSaveService.writeFileHeader(Configuration.getInstance().FRAME_PATTERN);
            //--------------

            //SerialPortManager setup
            SerialPortManager.getInstance().setMessageParser(messageParser);
            SerialPortManager.getInstance().setFrameSaveService(frameSaveService);
            SerialPortManager.getInstance().addListener(connectionController);
            SerialPortManager.getInstance().addListener(mainController);
            //--------------

            //Notification setup
            if (!Configuration.getInstance().DISCORD_TOKEN.equals("")) {
                notificationFormatService = new NotificationFormatDiscordService(Configuration.getInstance().sensorRepository);
                notificationEvent = new NotificationDiscordEvent(notificationFormatService);
                INotification discord = new DiscordNotification(notificationEvent);
                discord.addListener(connectionController);
                notificationSendService = new NotificationSendService(discord, notificationFormatService);
                notificationThread = new NotificationThread(notificationSendService);
                notificationThread.setupSchedule(Configuration.getInstance().notificationSchedule);
                connectionController.injectNotification(notificationSendService, Configuration.getInstance().notificationMessageKeys, notificationThread);
                discord.setup();
            }
            //--------------

            //SpeechService setup
            ttsService = new TextToSpeechService(ruleValidationService, textToSpeechDictionary);

            //Add SpeechService as listener
            Configuration.getInstance().sensorRepository.getAllBasicSensors().forEach((s, sensor) -> {
                sensor.addListener(ttsService);
            });
            //--------------

            //stage settings
            primaryStage.setTitle("SouRCE");
            primaryStage.setMaximized(true);
            primaryStage.setScene(scene);
            primaryStage.getIcons().add(new Image(Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream("Poliwrocket.png"))));
            primaryStage.heightProperty().addListener(mainController);
            primaryStage.widthProperty().addListener(mainController);
            primaryStage.show();
            //--------------

            //testMode();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void stop() {
        logger.info("Stage is closing");
        ttsService.deallocate();
        if (SerialPortManager.getInstance().isPortOpen()) {
            SerialPortManager.getInstance().close();
        }
        System.exit(-1);
    }

    private void testMode() {

            JsonObject raw = new Gson().fromJson("{\n" +
                    "\t\"accel\": 100,\n" +
                    "\t\"pressure\": 48,\n" +
                    "\t\"satelites\": 12,\n" +
                    "\t\"fix\": 1,\n" +
                    "\t\"rocketWeight\": 24357,\n" +
                    "\t\"rssi\": 57,\n" +
                    "\t\"motherWeight\": 5357,\n" +
                    "\t\"bat1\": 8.1,\n" +
                    "\t\"bat2\": 8.2,\n" +
                    "\t\"bat3\": 8.3,\n" +
                    "\t\"bat4\": 8.0,\n" +
                    "\t\"bat5\": 7.9,\n" +
                    "\t\"bat6\": 7.8,\n" +
                    "\t\"alt\": 0,\n" +
                    "\t\"vel\": 12,\n" +
                    "\t\"igni\": 0,\n" +
                    "\t\"pilot\": 0,\n" +
                    "\t\"apoge\": 1,\n" +
                    "\t\"mainSchute\": 0,\n" +
                    "\t\"lat\": 51.20395820043864,\n" +
                    "\t\"lon\": 16.99416435080908\n" +
                    "}",JsonObject.class);
            new Thread(() -> {
                try {
                    Thread.sleep(4000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                while (true) {
                    if(SerialPortManager.getInstance().isPortOpen()) {
                        Frame frame = new Frame(raw.toString(), Instant.now());
                        messageParser.parseMessage(frame);
                        frameSaveService.saveFrameToFile(frame);

                        for (String sensorKey: Configuration.getInstance().FRAME_PATTERN) {
                            if(sensorKey.equals("lat")) {
                                raw.addProperty(sensorKey,Double.parseDouble(raw.get(sensorKey).toString())+0.000025);
                            } else if(sensorKey.equals("lon")) {
                                raw.addProperty(sensorKey,Double.parseDouble(raw.get(sensorKey).toString())+0.000025);
                            } else if(sensorKey.contains("bat")) {
                                double r = ThreadLocalRandom.current().nextDouble(7.0,8.4);
                                raw.addProperty(sensorKey,r);
                            } else if(sensorKey.equals("accel")){
                                if(Double.parseDouble(raw.get(sensorKey).toString()) < 380){
                                    double r = ThreadLocalRandom.current().nextDouble(Double.parseDouble(raw.get(sensorKey).toString()),Double.parseDouble(raw.get(sensorKey).toString())+10);
                                    raw.addProperty(sensorKey,r);

                                }
                            } else if(sensorKey.equals("alt")){
                                if(Double.parseDouble(raw.get(sensorKey).toString()) < 3850){
                                    double r = ThreadLocalRandom.current().nextDouble(Double.parseDouble(raw.get(sensorKey).toString()),Double.parseDouble(raw.get(sensorKey).toString())+20);
                                    raw.addProperty(sensorKey,r);

                                }
                            }else if(sensorKey.equals("vel")){
                                if(Double.parseDouble(raw.get(sensorKey).toString()) < 380){
                                    double r = ThreadLocalRandom.current().nextDouble(Double.parseDouble(raw.get(sensorKey).toString()),Double.parseDouble(raw.get(sensorKey).toString())+5);
                                    raw.addProperty(sensorKey,r);

                                }
                            } else if(sensorKey.equals("rocketWeight")){
                                double r = ThreadLocalRandom.current().nextDouble(16000,36000);
                                raw.addProperty(sensorKey,r);
                            } else if(sensorKey.equals("motherWeight")){
                                double r = ThreadLocalRandom.current().nextDouble(4000,11600);
                                raw.addProperty(sensorKey,r);
                            }else if(sensorKey.equals("rssi")){
                                double r = ThreadLocalRandom.current().nextDouble(50,100);
                                raw.addProperty(sensorKey,r);
                            }else if(sensorKey.equals("apoge")){
                                var r = ThreadLocalRandom.current().nextBoolean() ? 1.0 : 0;
                                raw.addProperty(sensorKey,r);
                            }else if(sensorKey.equals("igni")){
                                var r = ThreadLocalRandom.current().nextBoolean() ? 1.0 : 0;
                                raw.addProperty(sensorKey,r);
                            }else if(sensorKey.equals("pilot")){
                                var r = ThreadLocalRandom.current().nextBoolean() ? 1.0 : 0;
                                raw.addProperty(sensorKey,r);
                            }else if(sensorKey.equals("mainSchute")){
                                var r = ThreadLocalRandom.current().nextBoolean() ? 1.0 : 0;
                                raw.addProperty(sensorKey,r);
                            }
                        }

                    }
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
    }
}
