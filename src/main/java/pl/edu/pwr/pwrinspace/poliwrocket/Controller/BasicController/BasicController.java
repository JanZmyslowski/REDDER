package pl.edu.pwr.pwrinspace.poliwrocket.Controller.BasicController;

import javafx.beans.InvalidationListener;

public abstract class BasicController implements InvalidationListener {
    protected ControllerNameEnumInterface controllerNameEnum;

    public ControllerNameEnumInterface getControllerNameEnum() {
        return this.controllerNameEnum;
    }
}
