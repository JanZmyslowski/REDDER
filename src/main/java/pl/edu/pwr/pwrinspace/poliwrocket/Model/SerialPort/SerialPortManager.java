package pl.edu.pwr.pwrinspace.poliwrocket.Model.SerialPort;

import gnu.io.NRSerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
import javafx.beans.InvalidationListener;
import org.slf4j.LoggerFactory;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.MessageParser.Frame;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.MessageParser.IMessageParser;
import pl.edu.pwr.pwrinspace.poliwrocket.Service.Save.FrameSaveService;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.lang.Thread.sleep;

public class SerialPortManager implements SerialPortEventListener, ISerialPortManager {

    private final List<InvalidationListener> observers = new ArrayList<>();

    private NRSerialPort serialPort;
    private String PORT_NAME = "COM3";
    private int DATA_RATE = 115200;
    private final Logger log = Logger.getLogger(getClass().getName());
    private OutputStream outputStream;
    private InputStream inputStream;
    private SerialWriter serialWriter;
    private boolean isPortOpen = false;
    private FrameSaveService frameSaveService;
    private IMessageParser messageParser;
    private String lastMessage = "";

    private SerialPortManager() {
        if (Holder.INSTANCE != null) {
            throw new IllegalStateException("Singleton already constructed");
        }
    }

    public static SerialPortManager getInstance() {
        return Holder.INSTANCE;
    }

    @Override
    public void addListener(InvalidationListener invalidationListener) {
        observers.add(invalidationListener);
    }

    @Override
    public void removeListener(InvalidationListener invalidationListener) {
        observers.remove(invalidationListener);
    }

    @Override
    public void setFrameSaveService(FrameSaveService frameSaveService) {
        this.frameSaveService = frameSaveService;
    }

    private void notifyObserver() {
        for (InvalidationListener obs : observers) {
            obs.invalidated(this);
        }
    }

    private static class Holder {
        private static final SerialPortManager INSTANCE = new SerialPortManager();
    }

    @Override
    public void setMessageParser(IMessageParser messageParser) {
        this.messageParser = messageParser;
    }

    @Override
    public void initialize(String portName, int dataRate) {
        this.PORT_NAME = portName;
        this.DATA_RATE = dataRate;
        this.initialize();
    }

    @Override
    public boolean isPortOpen() {
        return this.isPortOpen;
    }

    @Override
    public void initialize() {
        if(messageParser != null) {
            try {
                // otwieramy i konfigurujemy port
                serialPort = new NRSerialPort(PORT_NAME, DATA_RATE);
                serialPort.connect();
                if(serialPort.isConnected()) {
                    // strumień wejścia
                    inputStream = serialPort.getInputStream();

                    //strumień wyjścia
                    outputStream = serialPort.getOutputStream();
                    serialWriter = new SerialWriter(outputStream);
                    (new Thread(serialWriter)).start();

                    // dodajemy słuchaczy zdarzeń
                    serialPort.addEventListener(this);
                    serialPort.notifyOnDataAvailable(true);
                } else {
                    serialPort.disconnect();
                }
                isPortOpen = serialPort.isConnected();
            } catch (Exception e) {
                isPortOpen = serialPort.isConnected();
                log.log(Level.WARNING,e.toString());
            } finally {
                notifyObserver();
                log.log(Level.INFO, "Serialport status open: {}", isPortOpen);
            }
        } else {
            isPortOpen = serialPort.isConnected();
            notifyObserver();
            log.log(Level.WARNING,"IMessageParser not set");
            log.log(Level.INFO, "Serialport status open: {}", isPortOpen);
        }
        if(frameSaveService == null) {
            log.log(Level.WARNING,"FrameSaveService not set");
        }
    }

    @Override
    public synchronized void close() {
        if (serialPort != null) {
            serialPort.removeEventListener();
            serialPort.disconnect();
            log.log(Level.INFO, "Serialport closed.");
            isPortOpen = serialPort.isConnected();
            notifyObserver();
        }
    }

    @Override
    public synchronized void serialEvent(SerialPortEvent oEvent) {
        if (oEvent.getEventType() == SerialPortEvent.DATA_AVAILABLE) {
            try {
                byte[] buffer = new byte[1024];
                int length = this.inputStream.read(buffer);
                Frame frame = new Frame(new String(buffer, 0, length), Instant.now());
                messageParser.parseMessage(frame);
                if(frameSaveService != null) {
                    if(frame.getFormattedContent() == null) {
                        frame.setFormattedContent(frame.getContent());
                    }
                    frameSaveService.saveFrameToFile(frame);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    public synchronized void write(String message) {
        log.log(Level.INFO, "Written: {0}", message);
        serialWriter.send(message);
        this.lastMessage = message;
        notifyObserver();

    }

    @Override
    public String getLastSend() {
        return this.lastMessage;
    }

    public static class SerialWriter implements Runnable {
        OutputStream out;

        private static final org.slf4j.Logger logger = LoggerFactory.getLogger(SerialWriter.class);

        public SerialWriter(OutputStream out) {
            this.out = out;
        }

        @Override
        public void run() {
            try {
                sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
                Thread.currentThread().interrupt();
            }
        }

        public synchronized void send(String msg) {
            try {
                out.write(msg.getBytes());
                logger.info("Written: {}",msg);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
