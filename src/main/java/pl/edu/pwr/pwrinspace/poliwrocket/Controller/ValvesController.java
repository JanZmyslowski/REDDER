package pl.edu.pwr.pwrinspace.poliwrocket.Controller;

import eu.hansolo.tilesfx.Tile;
import eu.hansolo.tilesfx.addons.Indicator;
import javafx.beans.Observable;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import pl.edu.pwr.pwrinspace.poliwrocket.Controller.BasicController.BasicButtonSensorController;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.Sensor.ISensor;
import pl.edu.pwr.pwrinspace.poliwrocket.Thred.UI.UIThreadManager;

import java.util.HashMap;

public class ValvesController extends BasicButtonSensorController {

    @FXML
    private Indicator valveIndicator1;

    @FXML
    private Label valveLabel1;

    @FXML
    private Button valveOpenButton1;

    @FXML
    private Button valveCloseButton1;

    @FXML
    private Indicator valveIndicator2;

    @FXML
    private Label valveLabel2;

    @FXML
    private Button valveOpenButton2;

    @FXML
    private Button valveCloseButton2;

    @FXML
    private Indicator valveIndicator3;

    @FXML
    private Label valveLabel3;

    @FXML
    private Button valveOpenButton3;

    @FXML
    private Button valveCloseButton3;

    @FXML
    private Indicator valveIndicator4;

    @FXML
    private Label valveLabel4;

    @FXML
    private Button valveOpenButton4;

    @FXML
    private Button valveCloseButton4;

    @FXML
    private Indicator valveIndicator5;

    @FXML
    private Label valveLabel5;

    @FXML
    private Button valveOpenButton5;

    @FXML
    private Button valveCloseButton5;


    private final HashMap<String,Indicator> indicatorHashMap = new HashMap<>();

    @FXML
    void initialize() {
        controllerNameEnum = ControllerNameEnum.VALVES_CONTROLLER;

        buttonHashMap.put(valveOpenButton1.getId(),valveOpenButton1);
        buttonHashMap.put(valveOpenButton2.getId(),valveOpenButton2);
        buttonHashMap.put(valveOpenButton3.getId(),valveOpenButton3);
        buttonHashMap.put(valveOpenButton4.getId(),valveOpenButton4);
        buttonHashMap.put(valveOpenButton5.getId(),valveOpenButton5);

        buttonHashMap.put(valveCloseButton1.getId(),valveCloseButton1);
        buttonHashMap.put(valveCloseButton2.getId(),valveCloseButton2);
        buttonHashMap.put(valveCloseButton3.getId(),valveCloseButton3);
        buttonHashMap.put(valveCloseButton4.getId(),valveCloseButton4);
        buttonHashMap.put(valveCloseButton5.getId(),valveCloseButton5);

        indicatorHashMap.put(valveIndicator1.getId(),valveIndicator1);
        indicatorHashMap.put(valveIndicator2.getId(),valveIndicator2);
        indicatorHashMap.put(valveIndicator3.getId(),valveIndicator3);
        indicatorHashMap.put(valveIndicator4.getId(),valveIndicator4);
        indicatorHashMap.put(valveIndicator5.getId(),valveIndicator5);
    }

    @Override
    protected void setUIBySensors(){
        for (ISensor s : sensors) {
            var indicator = indicatorHashMap.get(s.getDestination());
            indicator.setVisible(true);
        }
    }

    @Override
    public void invalidated(Observable observable) {
        try {
            var sensor = ((ISensor) observable);
            UIThreadManager.getInstance().addImmediate(() -> {
                if(sensor.getValue() != 1.0 && sensor.getValue() != 0.0){
                    indicatorHashMap.get(sensor.getDestination()).setDotOnColor(Color.RED);
                } else {
                    indicatorHashMap.get(sensor.getDestination()).setDotOnColor(Tile.BLUE);
                }
                indicatorHashMap.get(sensor.getDestination()).setOn(sensor.getValue() >= 1.0);
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
