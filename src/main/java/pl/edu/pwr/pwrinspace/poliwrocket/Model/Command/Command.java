package pl.edu.pwr.pwrinspace.poliwrocket.Model.Command;

import com.google.gson.annotations.Expose;
import pl.edu.pwr.pwrinspace.poliwrocket.Controller.ControllerNameEnum;

import java.util.ArrayList;
import java.util.List;

public class Command implements ICommand {

    @Expose
    private String value = "x";

    @Expose
    private String trigger = "y";

    @Expose
    private List<ControllerNameEnum> destinationControllerNames = new ArrayList<>();

    public Command(String value, String trigger) {
        this.value = value;
        this.trigger = trigger;
    }

    @Override
    public String toString() {
        return trigger;
    }

    @Override
    public int hashCode() {
        return trigger.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    @Override
    public String getCommandValue() {
        return value;
    }

    @Override
    public String getCommandTriggerKey() {
        return trigger;
    }

    public List<ControllerNameEnum> getDestinationControllerNames() {
        return destinationControllerNames;
    }

    public void setDestinationControllerNames(List<ControllerNameEnum> destinationControllerNames) {
        this.destinationControllerNames = destinationControllerNames;
    }
}
