package pl.edu.pwr.pwrinspace.poliwrocket.Controller;

import eu.hansolo.tilesfx.Tile;
import javafx.beans.Observable;
import javafx.fxml.FXML;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.edu.pwr.pwrinspace.poliwrocket.Controller.BasicController.BasicSensorController;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.Sensor.ISensor;
import pl.edu.pwr.pwrinspace.poliwrocket.Thred.UI.UIThreadManager;

import java.time.Duration;
import java.util.HashMap;

public class DataController extends BasicSensorController {

    private static final int _duration = 10;

    private static final Duration DURATION = Duration.ofSeconds(_duration);

    @FXML
    private Tile dataGauge1;

    @FXML
    private Tile dataGauge2;

    @FXML
    private Tile dataGauge3;

    @FXML
    private Tile dataGauge4;

    @FXML
    private Tile dataGauge5;

    @FXML
    private Tile dataGauge6;

    @FXML
    private Tile dataGauge7;

    @FXML
    private Tile dataGauge8;

    HashMap<String, Tile> tileHashMap = new HashMap<>();

    private static final Logger logger = LoggerFactory.getLogger(DataController.class);

    @FXML
    void initialize() {
        controllerNameEnum = ControllerNameEnum.DATA_CONTROLLER;

        tileHashMap.put(dataGauge1.getId(), dataGauge1);
        tileHashMap.put(dataGauge2.getId(), dataGauge2);
        tileHashMap.put(dataGauge3.getId(), dataGauge3);
        tileHashMap.put(dataGauge4.getId(), dataGauge4);
        tileHashMap.put(dataGauge5.getId(), dataGauge5);
        tileHashMap.put(dataGauge6.getId(), dataGauge6);
        tileHashMap.put(dataGauge7.getId(), dataGauge7);
        tileHashMap.put(dataGauge8.getId(), dataGauge8);

        tileHashMap.forEach((s, tile) -> tile.setVisible(false));
    }

    @Override
    protected void setUIBySensors() {

        for (ISensor sensor : sensors) {
            var tile = tileHashMap.get(sensor.getDestination());
            if (tile != null) {
                tile.setVisible(true);
                tile.setMaxValue(sensor.getMaxRange());
                tile.setMinValue(sensor.getMinRange());
                tile.setTitle(sensor.getName());
                tile.setUnit(sensor.getUnit());
                tile.setAverageVisible(true);
                tile.setSmoothing(true);
                tile.setTimePeriod(DURATION);
                if(!sensor.isBoolean()) {
                    UIThreadManager.getInstance().addActiveSensor();
                }
                //tile.setAveragingPeriod(_duration * Configuration.getInstance().FPS);
            } else {
                logger.error("Wrong UI binding - destination not found: {}",sensor.getDestination());
            }
        }
        if (!dataGauge2.visibleProperty().get()) {
            dataGauge1.setPrefWidth(dataGauge1.getPrefWidth() * 2 + 24);
        }
        if (!dataGauge4.visibleProperty().get()) {
            dataGauge3.setPrefWidth(dataGauge3.getPrefWidth() * 2 + 24);

        }
        if (!dataGauge6.visibleProperty().get()) {
            dataGauge5.setPrefWidth(dataGauge5.getPrefWidth() * 2 + 24);

        }
        if (!dataGauge8.visibleProperty().get()) {
            dataGauge7.setPrefWidth(dataGauge7.getPrefWidth() * 2 + 24);

        }

    }

    @Override
    public void invalidated(Observable observable) {
        try {
            var sensor = ((ISensor) observable);
            UIThreadManager.getInstance().addNormal(() -> tileHashMap.get(sensor.getDestination()).setValue(sensor.getValue()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
