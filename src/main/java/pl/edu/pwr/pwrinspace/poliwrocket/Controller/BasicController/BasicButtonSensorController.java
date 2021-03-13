package pl.edu.pwr.pwrinspace.poliwrocket.Controller.BasicController;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.Command.ICommand;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.SerialPort.SerialPortManager;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

public abstract class BasicButtonSensorController extends BasicSensorController {

    protected HashMap<String,Button> buttonHashMap = new HashMap<>();

    protected HashSet<ICommand> commands = new HashSet<>();

    private static final Logger logger = LoggerFactory.getLogger(BasicButtonSensorController.class);

    public void assignsCommands(Collection<ICommand> commands){
        this.commands.addAll(commands);

        for (ICommand command : commands) {
            var button = buttonHashMap.get(command.getCommandTriggerKey());
            if (button != null){
                button.setOnAction(handleButtonsClickByCommand(button,command));
            } else {
                logger.warn("Trigger not found: {} , it`s maybe correct for fire button!", command.getCommandTriggerKey());
            }
        }
    }

    protected EventHandler<ActionEvent> handleButtonsClickByCommand(Button button, ICommand command){
        button.setVisible(true);

        return actionEvent -> SerialPortManager.getInstance().write(command.getCommandValue());
    }

}
