package pl.edu.pwr.pwrinspace.poliwrocket.Controller;

import eu.hansolo.medusa.Gauge;
import eu.hansolo.tilesfx.Tile;
import eu.hansolo.tilesfx.addons.Indicator;
import javafx.beans.Observable;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import pl.edu.pwr.pwrinspace.poliwrocket.Controller.BasicController.BasicSensorController;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.Sensor.ISensor;
import pl.edu.pwr.pwrinspace.poliwrocket.Thred.UI.UIThreadManager;

import java.util.HashMap;

public class MoreDataController extends BasicSensorController {

    private final static Double thresholdPercent = 0.8;

    @FXML
    private Gauge dataGauge9;

    @FXML
    private Gauge dataGauge10;

    @FXML
    private Indicator dataIndicator1;

    @FXML
    private Indicator dataIndicator2;

    @FXML
    private Indicator dataIndicator4;

    @FXML
    private Indicator dataIndicator3;

    @FXML
    private Label indicatorLabel1;

    @FXML
    private Label indicatorLabel2;

    @FXML
    private Label indicatorLabel3;

    @FXML
    private Label indicatorLabel4;

    HashMap<String, Object> visualizationsHashMap = new HashMap<>();
    HashMap<String, Label> labelsHashMap = new HashMap<>();

    @FXML
    void initialize() {
        controllerNameEnum = ControllerNameEnum.MORE_DATA_CONTROLLER;

        visualizationsHashMap.put(dataIndicator1.getId(),dataIndicator1);
        visualizationsHashMap.put(dataIndicator2.getId(),dataIndicator2);
        visualizationsHashMap.put(dataIndicator3.getId(),dataIndicator3);
        visualizationsHashMap.put(dataIndicator4.getId(),dataIndicator4);
        visualizationsHashMap.put(dataGauge9.getId(),dataGauge9);
        visualizationsHashMap.put(dataGauge10.getId(),dataGauge10);

        labelsHashMap.put(dataIndicator1.getId(),indicatorLabel1);
        labelsHashMap.put(dataIndicator2.getId(),indicatorLabel2);
        labelsHashMap.put(dataIndicator3.getId(),indicatorLabel3);
        labelsHashMap.put(dataIndicator4.getId(),indicatorLabel4);

        visualizationsHashMap.forEach((s, o) -> {
            if(o instanceof Indicator) {
                ((Indicator)visualizationsHashMap.get(s)).setVisible(false);
            } else if (o instanceof  Gauge){
                ((Gauge)visualizationsHashMap.get(s)).setVisible(false);
            }
        });
        labelsHashMap.forEach((s, label) -> labelsHashMap.get(s).setVisible(false));
    }

    @Override
    protected void setUIBySensors() {
        for (ISensor sensor : sensors) {
            var visualization = this.visualizationsHashMap.get(sensor.getDestination());
            if (visualization != null) {
                if(visualization instanceof Indicator) {
                    ((Indicator)visualization).setVisible(true);
                    var label = this.labelsHashMap.get(((Indicator)visualization).getId());
                    label.setText(sensor.getName());
                    label.setVisible(true);
                } else if (visualization instanceof  Gauge){
                    ((Gauge)visualization).setMaxValue(sensor.getMaxRange());
                    ((Gauge)visualization).setMinValue(sensor.getMinRange());
                    ((Gauge)visualization).setTitle(sensor.getName());
                    ((Gauge)visualization).setUnit(sensor.getUnit());
                    ((Gauge)visualization).setThreshold(sensor.getMaxRange()*this.thresholdPercent);
                    ((Gauge)visualization).setVisible(true);
                }
                if(!sensor.isBoolean()) {
                    UIThreadManager.getInstance().addActiveSensor();
                }
            }
        }
    }

    @Override
    public void invalidated(Observable observable) {
        try {
            var sensor = ((ISensor) observable);

                var visualization = visualizationsHashMap.get(sensor.getDestination());
                if(visualization instanceof Indicator) {
                    UIThreadManager.getInstance().addImmediate(() -> {
                        if(sensor.getValue() != 1.0 && sensor.getValue() != 0.0){
                            ((Indicator)visualization).setDotOnColor(Color.RED);
                        } else {
                            ((Indicator)visualization).setDotOnColor(Tile.BLUE);
                        }
                        ((Indicator)visualization).setOn(sensor.getValue() >= 1.0);
                    });
                } else if (visualization instanceof  Gauge){
                    UIThreadManager.getInstance().addNormal(() -> ((Gauge)visualization).setValue(sensor.getValue()));
                }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
