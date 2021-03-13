package pl.edu.pwr.pwrinspace.poliwrocket.Model.MessageParser;

import javafx.beans.InvalidationListener;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.Sensor.ISensorRepository;

import java.util.LinkedList;
import java.util.List;

public abstract class BaseMessageParser implements IMessageParser {

    protected final ISensorRepository sensorRepository;

    protected String lastMessage;

    protected ParsingResultStatus parsingResultStatus;

    private final List<InvalidationListener> observers = new LinkedList<>();

    public BaseMessageParser(ISensorRepository sensorRepository) {
        this.sensorRepository = sensorRepository;
    }

    @Override
    public String getLastMessage() {
        return lastMessage;
    }

    @Override
    public ParsingResultStatus getParsingStatus() {
        return parsingResultStatus;
    }


    @Override
    public void addListener(InvalidationListener invalidationListener) {
        this.observers.add(invalidationListener);
    }

    @Override
    public void removeListener(InvalidationListener invalidationListener) {
        this.observers.remove(invalidationListener);
    }

    protected void notifyObserver(){
        this.observers.forEach(obs -> obs.invalidated(this));
    }
}
