package pl.edu.pwr.pwrinspace.poliwrocket.Controller;

import eu.hansolo.medusa.Gauge;
import gnu.io.NRSerialPort;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.edu.pwr.pwrinspace.poliwrocket.Controller.BasicController.BasicButtonSensorController;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.Command.ICommand;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.Notification.INotification;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.Sensor.ISensor;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.SerialPort.ISerialPortManager;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.SerialPort.SerialPortManager;
import pl.edu.pwr.pwrinspace.poliwrocket.Service.Notification.NotificationSendService;
import pl.edu.pwr.pwrinspace.poliwrocket.Thred.Notification.INotificationThread;
import pl.edu.pwr.pwrinspace.poliwrocket.Thred.ThreadName;
import pl.edu.pwr.pwrinspace.poliwrocket.Thred.UI.UIThreadManager;

import java.util.Collection;
import java.util.List;

public class ConnectionController extends BasicButtonSensorController {

    @FXML
    private ComboBox<String> serialPorts;

    @FXML
    private Button connectionButton;

    @FXML
    private TextField baudRate;

    @FXML
    private Label connectionStatus;

    @FXML
    private ComboBox<String> notifications;

    @FXML
    private Button sendNotification;

    @FXML
    private Label notificationStatus;

    @FXML
    private Button threadButton;

    @FXML
    private Label threadStatus;

    @FXML
    private Gauge signal;

    @FXML
    private ComboBox<ICommand> serialMessages;

    @FXML
    private Button sendSerialMessage;

    private static final Logger logger = LoggerFactory.getLogger(ConnectionController.class);

    private final ObservableList<String> availableSerialPorts = FXCollections.observableArrayList();

    private final ObservableList<String> availableNotifications = FXCollections.observableArrayList();

    private final ObservableList<ICommand> availableMessages = FXCollections.observableArrayList();

    private NotificationSendService notificationSendService;

    private INotificationThread notificationThreadRunnable;

    private Thread notificationThread;

    @FXML
    void initialize() {
        controllerNameEnum = ControllerNameEnum.CONNECTION_CONTROLLER;

        serialSetup();
        sendNotification.setDisable(true);
        notifications.setDisable(true);
        threadButton.setDisable(true);

        serialPorts.setOnMouseClicked(mouseEvent -> serialSetup());

        connectionButton.setOnMouseClicked(mouseEvent -> {
            if (availableSerialPorts.isEmpty()) {
                connectionStatus.setText("Error: no ports");
            } else {
                if (!SerialPortManager.getInstance().isPortOpen()) {
                    connectionStatus.setText("Connecting");
                    baudRate.setDisable(true);
                    serialPorts.setDisable(true);
                    SerialPortManager.getInstance().initialize(serialPorts.getValue(), Integer.parseInt(baudRate.getText()));
                } else {
                    connectionStatus.setText("Disconnecting");
                    sendSerialMessage.setDisable(true);
                    SerialPortManager.getInstance().close();
                }
            }
        });

        sendNotification.setOnMouseClicked(mouseEvent -> {
            if (notificationSendService != null) {
                notificationSendService.sendNotification(notifications.getValue());
            } else {
                sendNotification.setDisable(true);
                notificationStatus.setText("Service error");
            }
        });

        threadButton.setOnMouseClicked(mouseEvent -> {
            if (notificationThreadRunnable != null && (notificationThread == null || !notificationThread.isAlive())) {
                notificationThread = new Thread(notificationThreadRunnable, ThreadName.DISCORD_NOTIFICATION.getName());
                notificationThread.start();
                threadStatus.setText("Running");
            } else if (notificationThread != null && notificationThread.isAlive()) {
                notificationThread.interrupt();
                threadStatus.setText("Interrupted");
            }
        });

        sendSerialMessage.setOnMouseClicked(mouseEvent -> SerialPortManager.getInstance().write(serialMessages.getValue().getCommandValue()));
    }

    public void injectNotification(NotificationSendService notificationSendService, List<String> notificationsList, INotificationThread notificationThreadRunnable) {
        this.notificationSendService = notificationSendService;
        this.availableNotifications.clear();
        this.availableNotifications.addAll(notificationsList);
        notifications.setItems(availableNotifications);
        if (!notificationsList.isEmpty()) {
            notifications.setValue(notificationsList.get(0));
        }
        this.notificationThreadRunnable = notificationThreadRunnable;
    }

    @Override
    public void assignsCommands(Collection<ICommand> messagesList) {
        this.availableMessages.clear();
        this.availableMessages.addAll(messagesList);
        serialMessages.setItems(availableMessages);
        if (!messagesList.isEmpty()) {
            serialMessages.setValue(availableMessages.get(0));
        }
    }

    private void serialSetup() {
        availableSerialPorts.clear();
        availableSerialPorts.addAll(NRSerialPort.getAvailableSerialPorts());
        serialPorts.setItems(availableSerialPorts);
        serialPorts.setValue(!availableSerialPorts.isEmpty() ? availableSerialPorts.get(0) : "No ports available");
    }

    @Override
    protected void setUIBySensors() {
        for (ISensor sensor : sensors) {
            if (sensor.getDestination().equals(signal.getId())) {
                signal.setVisible(true);
                signal.setUnit(sensor.getUnit());
                if(!sensor.isBoolean()) {
                    UIThreadManager.getInstance().addActiveSensor();
                }
            } else {
                logger.error("Wrong UI binding - destination not found: {}",sensor.getDestination());
            }
        }
    }

    @Override
    public void invalidated(Observable observable) {
        if (observable instanceof ISerialPortManager) {
            if (((ISerialPortManager) observable).isPortOpen()) {
                connectionStatus.setText("Connected");
                sendSerialMessage.setDisable(false);
                baudRate.setDisable(true);
                serialPorts.setDisable(true);
            } else {
                if (connectionStatus.getText().equals("Connecting")) {
                    connectionStatus.setText("Failed");
                } else {
                    connectionStatus.setText("Disconnected");
                }
                baudRate.setDisable(false);
                serialPorts.setDisable(false);
                sendSerialMessage.setDisable(true);
            }
        } else if (observable instanceof INotification) {
            boolean status = ((INotification) observable).isConnected();
            sendNotification.setDisable(!status);
            notifications.setDisable(!status);
            threadButton.setDisable(!status);
            notificationStatus.setText(status ? "Connected" : "Not connected");
            threadStatus.setText(status ? "Not running" : "Not enabled");
        } else if (observable instanceof ISensor) {
            var sensor = ((ISensor) observable);
            UIThreadManager.getInstance().addNormal(() -> signal.setValue(Math.round((sensor.getValue() - sensor.getMinRange())/(sensor.getMaxRange()-sensor.getMinRange())*1000)/1000.0));
        }
    }
}
