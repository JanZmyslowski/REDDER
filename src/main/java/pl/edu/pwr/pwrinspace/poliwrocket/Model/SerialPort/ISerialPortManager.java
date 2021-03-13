package pl.edu.pwr.pwrinspace.poliwrocket.Model.SerialPort;

import javafx.beans.Observable;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.MessageParser.IMessageParser;
import pl.edu.pwr.pwrinspace.poliwrocket.Service.Save.FrameSaveService;

public interface ISerialPortManager extends Observable {
    void initialize();
    void close();
    void initialize(String portName, int dataRate);
    boolean isPortOpen();
    void write(String message);
    String getLastSend();
    void setMessageParser(IMessageParser messageParser);
    void setFrameSaveService(FrameSaveService frameSaveService);
}
