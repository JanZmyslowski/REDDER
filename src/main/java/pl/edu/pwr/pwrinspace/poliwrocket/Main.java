package pl.edu.pwr.pwrinspace.poliwrocket;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.edu.pwr.pwrinspace.poliwrocket.Controller.BasicController.BasicController;
import pl.edu.pwr.pwrinspace.poliwrocket.Controller.ChartsController;
import pl.edu.pwr.pwrinspace.poliwrocket.Controller.ConnectionController;
import pl.edu.pwr.pwrinspace.poliwrocket.Controller.MainController;
import pl.edu.pwr.pwrinspace.poliwrocket.Event.Discord.NotificationDiscordEvent;
import pl.edu.pwr.pwrinspace.poliwrocket.Event.NotificationEvent;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.Configuration.Configuration;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.Configuration.ConfigurationSaveModel;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.MessageParser.IMessageParser;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.MessageParser.JsonMessageParser;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.MessageParser.MessageParserEnum;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.MessageParser.StandardMessageParser;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.Notification.DiscordNotification;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.Notification.INotification;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.SerialPort.SerialPortManager;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.Speech.TextToSpeechDictionary;
import pl.edu.pwr.pwrinspace.poliwrocket.Service.Notification.NotificationFormatDiscordService;
import pl.edu.pwr.pwrinspace.poliwrocket.Service.Notification.NotificationFormatService;
import pl.edu.pwr.pwrinspace.poliwrocket.Service.Notification.NotificationSendService;
import pl.edu.pwr.pwrinspace.poliwrocket.Service.Rule.RuleValidationService;
import pl.edu.pwr.pwrinspace.poliwrocket.Service.Save.FrameSaveService;
import pl.edu.pwr.pwrinspace.poliwrocket.Service.Save.ImageSaveService;
import pl.edu.pwr.pwrinspace.poliwrocket.Service.Save.ModelAsJsonSaveService;
import pl.edu.pwr.pwrinspace.poliwrocket.Thred.Notification.NotificationThread;
import pl.edu.pwr.pwrinspace.poliwrocket.Thred.UI.UIThreadManager;

import java.util.*;

public class Main extends Application {

    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    private final ModelAsJsonSaveService modelAsJsonSaveService = new ModelAsJsonSaveService();
    private final FrameSaveService frameSaveService = new FrameSaveService();
    private NotificationSendService notificationSendService;
    private NotificationThread notificationThread;
    private NotificationFormatService notificationFormatService;
    private IMessageParser messageParser;
    private NotificationEvent notificationEvent;
    //private TextToSpeechService ttsService;
    private TextToSpeechDictionary textToSpeechDictionary;
    private final RuleValidationService ruleValidationService = new RuleValidationService();
    private final ImageSaveService imageSaveService = new ImageSaveService();

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
            FXMLLoader loaderChart = new FXMLLoader(getClass().getClassLoader().getResource("ChartsView.fxml"));
            FXMLLoader loaderConnection = new FXMLLoader(getClass().getClassLoader().getResource("ConnectionView.fxml"));

            Scene scene = new Scene(loaderMain.load(), 1550, 750);
            //--------------

            //Controllers
            MainController mainController = loaderMain.getController();
            mainController.initSubScenes(loaderConnection, loaderChart);
            mainController.setPrimaryStage(primaryStage);

            ConnectionController connectionController = loaderConnection.getController();
            ChartsController chartsController = loaderChart.getController();
            //--------------

            //Mapping sensors and commands to controllers
            List<BasicController> controllerList = new ArrayList<>();
            controllerList.add(mainController);

            controllerList.add(connectionController);
            controllerList.add(chartsController);
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
//            ttsService = new TextToSpeechService(ruleValidationService, textToSpeechDictionary);

            //Add SpeechService as listener
//            Configuration.getInstance().sensorRepository.getAllBasicSensors().forEach((s, sensor) -> {
//                sensor.addListener(ttsService);
//            });
            //--------------

            //ImageSaveService
            chartsController.injectSaveService(imageSaveService);
            //--------------

            //stage settings
            primaryStage.setTitle("REDDER");
            primaryStage.setMaximized(true);
            primaryStage.setScene(scene);
            primaryStage.getIcons().add(new Image(Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream("Poliwrocket.png"))));
            primaryStage.heightProperty().addListener(mainController);
            primaryStage.widthProperty().addListener(mainController);
            primaryStage.show();
            //--------------

            new Thread(() -> {
                float value = 0;
                try {
                    Thread.sleep(2000);
                    while (true) {
                        Configuration.getInstance().sensorRepository.getSensorByName("Thrust").setValue(value);
                        value += 0.5;
                        Thread.sleep(15);
                    }
                } catch (Exception e){

                }


            }).start();
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
        //ttsService.deallocate();
        if (SerialPortManager.getInstance().isPortOpen()) {
            SerialPortManager.getInstance().close();
        }
        System.exit(-1);
    }
}
