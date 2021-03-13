package pl.edu.pwr.pwrinspace.poliwrocket.Model.MessageParser;

import javafx.beans.Observable;

public interface IMessageParser extends Observable {
    void parseMessage(Frame frame);
    String getLastMessage();
    ParsingResultStatus getParsingStatus();
}

