package pl.edu.pwr.pwrinspace.poliwrocket.Controller;

import eu.hansolo.medusa.Gauge;
import javafx.beans.Observable;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import org.javatuples.Triplet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.edu.pwr.pwrinspace.poliwrocket.Controller.BasicController.BasicSensorController;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.Sensor.ISensor;
import pl.edu.pwr.pwrinspace.poliwrocket.Thred.UI.UIThreadManager;

import java.util.HashMap;

public class PowerController extends BasicSensorController {

    @FXML
    private Gauge powerGauge4;

    @FXML
    private Gauge powerGauge5;

    @FXML
    private Gauge powerGauge6;

    @FXML
    private Gauge powerGauge3;

    @FXML
    private Gauge powerGauge7;

    @FXML
    private Gauge powerGauge2;

    @FXML
    private Gauge powerGauge1;

    @FXML
    private Label powerLabel7;

    @FXML
    private Label powerLabel6;

    @FXML
    private Label powerLabel5;

    @FXML
    private Label powerLabel4;

    @FXML
    private Label powerLabel3;

    @FXML
    private Label powerLabel2;

    @FXML
    private Label powerLabel1;
    @FXML
    private Label powerValue1;

    @FXML
    private Label powerValue2;

    @FXML
    private Label powerValue3;

    @FXML
    private Label powerValue4;

    @FXML
    private Label powerValue5;

    @FXML
    private Label powerValue6;

    @FXML
    private Label powerValue7;

    private final HashMap<String, Triplet<Gauge, Label, Label>> powerHashMap = new HashMap<>();

    private static final Logger logger = LoggerFactory.getLogger(PowerController.class);

    @FXML
    void initialize() {
        controllerNameEnum = ControllerNameEnum.POWER_CONTROLLER;

        powerHashMap.put(powerGauge1.getId(), new Triplet<>(powerGauge1, powerLabel1, powerValue1));
        powerHashMap.put(powerGauge2.getId(), new Triplet<>(powerGauge2, powerLabel2, powerValue2));
        powerHashMap.put(powerGauge3.getId(), new Triplet<>(powerGauge3, powerLabel3, powerValue3));
        powerHashMap.put(powerGauge4.getId(), new Triplet<>(powerGauge4, powerLabel4, powerValue4));
        powerHashMap.put(powerGauge5.getId(), new Triplet<>(powerGauge5, powerLabel5, powerValue5));
        powerHashMap.put(powerGauge6.getId(), new Triplet<>(powerGauge6, powerLabel6, powerValue6));
        powerHashMap.put(powerGauge7.getId(), new Triplet<>(powerGauge7, powerLabel7, powerValue7));

        powerHashMap.forEach((s, tuple) -> {
            tuple.getValue0().setVisible(false);
            tuple.getValue1().setVisible(false);
            tuple.getValue2().setVisible(false);
        });
    }

    @Override
    public void invalidated(Observable observable) {
        if (observable instanceof ISensor) {
            var sensor = ((ISensor) observable);
            UIThreadManager.getInstance().addNormal(() -> {
                powerHashMap.get(sensor.getDestination()).getValue0().setValue(Math.round((sensor.getValue() - sensor.getMinRange())/(sensor.getMaxRange()-sensor.getMinRange())*1000)/10.0);
                powerHashMap.get(sensor.getDestination()).getValue2().setText((Math.round(sensor.getValue()*100)/100.0) + sensor.getUnit());
            });
        }
    }

    @Override
    protected void setUIBySensors() {
        for (ISensor sensor : sensors) {
            var triplet = powerHashMap.get(sensor.getDestination());
            if (triplet != null) {
                triplet.getValue0().setVisible(true);
                triplet.getValue0().setThresholdColor(Color.BLUE);
                triplet.getValue0().setValueVisible(true);
                triplet.getValue0().setThresholdVisible(true);
                triplet.getValue0().setUnit(sensor.getUnit());
                triplet.getValue1().setVisible(true);
                triplet.getValue1().setText(sensor.getName());
                triplet.getValue2().setVisible(true);
                if(!sensor.isBoolean()) {
                    UIThreadManager.getInstance().addActiveSensor();
                }
            } else {
                logger.error("Wrong UI binding - destination not found: {}",sensor.getDestination());
            }
        }
    }
}
