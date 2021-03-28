package pl.edu.pwr.pwrinspace.poliwrocket.Controller;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.SubScene;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Scale;
import javafx.stage.Stage;
import org.javatuples.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.edu.pwr.pwrinspace.poliwrocket.Controller.BasicController.BasicController;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.MessageParser.IMessageParser;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.SerialPort.ISerialPortManager;
import pl.edu.pwr.pwrinspace.poliwrocket.Thred.UI.UIThreadManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class MainController extends BasicController implements InvalidationListener {

    private static final Logger logger = LoggerFactory.getLogger(MainController.class);
    private static final double initWidth = 1550.4;
    private static final double initHeight = 838.4;

    @FXML
    private ScrollPane inCommingPanel;

    @FXML
    private TextArea inComing;

    @FXML
    private ScrollPane outGoingPanel;

    @FXML
    private TextArea outGoing;

    @FXML
    private SubScene connectionScene;

    @FXML
    private AnchorPane footer;

    @FXML
    private ImageView poliwrocketLogo;

    @FXML
    private ImageView inSpaceLogo;

    @FXML
    private SubScene chartScene;

    private final MainController.SmartGroup root = new SmartGroup();

    private Stage primaryStage;

    private final List<Node> nodes = new ArrayList<>();
    private final HashMap<Node,Pair<Double,Double>> nodesInitPositions = new HashMap<>();

    public void initSubScenes(FXMLLoader loaderConnection, FXMLLoader loaderChart) {
        try {

            connectionScene.setRoot(loaderConnection.load());
            chartScene.setRoot(loaderChart.load());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void initialize() {
        controllerNameEnum = ControllerNameEnum.MAIN_CONTROLLER;

        //add nodes to list

        nodes.add(connectionScene);
        nodes.add(chartScene);
        nodes.add(outGoingPanel);
        nodes.add(inCommingPanel);
        nodes.add(footer);

        nodes.forEach(scene -> nodesInitPositions.put(scene,new Pair<>(scene.getLayoutX(),scene.getLayoutY())));

        //set logo
        poliwrocketLogo.setImage(new Image(Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream("Poliwrocket.png"))));
        inSpaceLogo.setImage(new Image(Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream("inSpaceLogo.png"))));
    }

    @Override
    public void invalidated(Observable observable) {
        if (observable instanceof IMessageParser) {
            UIThreadManager.getInstance().addImmediate(() -> inComing.appendText(((IMessageParser) observable).getLastMessage()));
        } else if (observable instanceof ISerialPortManager) {
            UIThreadManager.getInstance().addImmediate(() -> outGoing.appendText(((ISerialPortManager) observable).getLastSend() + "\n"));
        } else if(primaryStage.heightProperty().equals(observable) || primaryStage.widthProperty().equals(observable)) {
            scaleSubScenes(primaryStage.widthProperty().doubleValue()/initWidth,primaryStage.heightProperty().doubleValue()/initHeight);
        }
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    private void scaleSubScenes(double scaleX, double scaleY) {
        nodes.forEach(scene -> {
            if(!scene.getTransforms().isEmpty()) {
                scene.getTransforms().clear();
            }

            scene.getTransforms().add(new Scale(scaleX,scaleY));
            scene.setLayoutX(nodesInitPositions.get(scene).getValue0() * scaleX);
            scene.setLayoutY(nodesInitPositions.get(scene).getValue1() * scaleY);
        });
    }


    static class SmartGroup extends Group {

        private final Rotate rotateX = new Rotate(0, Rotate.X_AXIS);
        private final Rotate rotateY = new Rotate(0, Rotate.Y_AXIS);
        private final Rotate rotateZ = new Rotate(0, Rotate.Z_AXIS);

        public SmartGroup() {
            this.getTransforms().addAll(rotateX, rotateY, rotateZ);
        }

        void rotateByX(int ang) {
            rotateX.setAngle(ang);
        }

        void rotateByY(int ang) {
            rotateY.setAngle(ang);
        }

        void rotateByZ(int ang) {
            rotateZ.setAngle(ang);
        }
    }
}
