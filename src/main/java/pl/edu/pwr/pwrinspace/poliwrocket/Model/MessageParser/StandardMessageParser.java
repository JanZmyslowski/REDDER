package pl.edu.pwr.pwrinspace.poliwrocket.Model.MessageParser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.Configuration.Configuration;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.Sensor.ISensorRepository;

public class StandardMessageParser extends BaseMessageParser {

    private static final Logger logger = LoggerFactory.getLogger(StandardMessageParser.class);

    public StandardMessageParser(ISensorRepository sensorRepository) {
        super(sensorRepository);
    }

    @Override
    public void parseMessage(Frame frame) {
        lastMessage = frame.getContent();
        logger.info("Message received: {}", lastMessage);
        String[] splitted = lastMessage.split(Configuration.getInstance().FRAME_DELIMITER);
        frame.setFormattedContent(lastMessage);

        if(Configuration.getInstance().FRAME_PATTERN.size() == splitted.length){
            int currentPosition = 0;
            for (String sensorName : Configuration.getInstance().FRAME_PATTERN) {
                try {
                    sensorRepository.getSensorByName(sensorName).setValue(Double.parseDouble(splitted[currentPosition]));
                } catch (NumberFormatException e) {
                    this.lastMessage = "Invalid: " + this.lastMessage;
                    logger.warn("Wrong message, value is not a number! {}", lastMessage + " -> " + splitted[currentPosition]);
                } finally {
                    currentPosition++;
                }
            }
        } else {
            this.lastMessage = "Invalid: " + this.lastMessage;
            var logMessage = Configuration.getInstance().FRAME_PATTERN.size() + " got: " + splitted.length;
            logger.warn("Wrong message length! Expected: {}", logMessage);
        }
        notifyObserver();
    }
}
