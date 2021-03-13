package pl.edu.pwr.pwrinspace.poliwrocket.Controller;

import eu.hansolo.medusa.Gauge;
import eu.hansolo.tilesfx.addons.Indicator;
import eu.hansolo.tilesfx.addons.Switch;
import javafx.beans.Observable;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import pl.edu.pwr.pwrinspace.poliwrocket.Controller.BasicController.BasicButtonSensorController;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.Sensor.ISensor;
import pl.edu.pwr.pwrinspace.poliwrocket.Thred.UI.UIThreadManager;

import java.util.HashMap;

public class StatesController extends BasicButtonSensorController {

    @FXML
    private Switch safeSwitch1;

    @FXML
    private Switch safeSwitch2;

    @FXML
    private Switch safeSwitch3;

    @FXML
    private Switch safeSwitch4;

    @FXML
    private Button stateButton1;

    @FXML
    private Button stateButton2;

    @FXML
    private Button stateButton3;

    @FXML
    private Button stateButton4;

    @FXML
    private Indicator stateIndicator1;

    @FXML
    private Indicator stateIndicator2;

    @FXML
    private Indicator stateIndicator3;

    @FXML
    private Indicator stateIndicator4;

    HashMap<String, Object> visualizationsHashMap = new HashMap<>();

    @FXML
    void initialize() {
        controllerNameEnum = ControllerNameEnum.STATES_CONTROLLER;

        visualizationsHashMap.put(stateIndicator1.getId(),stateIndicator1);
        visualizationsHashMap.put(stateIndicator2.getId(),stateIndicator2);
        visualizationsHashMap.put(stateIndicator3.getId(),stateIndicator3);
        visualizationsHashMap.put(stateIndicator4.getId(),stateIndicator4);

        stateButton1.setDisable(true);
        stateButton2.setDisable(true);
        stateButton3.setDisable(true);
        stateButton4.setDisable(true);

        buttonHashMap.put(stateButton1.getId(), stateButton1);
        buttonHashMap.put(stateButton2.getId(), stateButton2);
        buttonHashMap.put(stateButton3.getId(), stateButton3);
        buttonHashMap.put(stateButton4.getId(), stateButton4);

        safeSwitch1.setOnMouseClicked(actionEvent ->
                stateButton1.setDisable(!safeSwitch1.isActive())
        );

        safeSwitch2.setOnMouseClicked(actionEvent ->
                stateButton2.setDisable(!safeSwitch1.isActive() || !safeSwitch2.isActive())
        );
        safeSwitch3.setOnMouseClicked(actionEvent ->
                stateButton3.setDisable(!safeSwitch1.isActive() || !safeSwitch2.isActive() || !safeSwitch3.isActive())
        );
        safeSwitch4.setOnMouseClicked(actionEvent ->
                stateButton4.setDisable(!safeSwitch1.isActive() || !safeSwitch2.isActive() || !safeSwitch3.isActive() || !safeSwitch4.isActive())
        );

    }

    @Override
    protected void setUIBySensors() {
        //on this panel are not customizable UI
    }

    @Override
    public void invalidated(Observable observable) {
        try {
            var sensor = ((ISensor) observable);
            UIThreadManager.getInstance().addImmediate(() -> {
                var visualization = visualizationsHashMap.get(sensor.getDestination());
                if(visualization instanceof Indicator) {
                    ((Indicator)visualization).setOn(sensor.getValue() == 1.0);
                } else if (visualization instanceof  Gauge){
                    ((Gauge)visualization).setValue(sensor.getValue());
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
