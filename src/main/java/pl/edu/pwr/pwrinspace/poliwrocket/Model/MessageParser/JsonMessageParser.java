package pl.edu.pwr.pwrinspace.poliwrocket.Model.MessageParser;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.Configuration.Configuration;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.Sensor.ISensorRepository;

public class JsonMessageParser extends BaseMessageParser {

    private static final Logger logger = LoggerFactory.getLogger(JsonMessageParser.class);

    public JsonMessageParser(ISensorRepository sensorRepository) {
        super(sensorRepository);
    }

    @Override
    public void parseMessage(Frame frame) {
        lastMessage = frame.getContent();
        logger.info("Message received: {}", lastMessage);
        JsonObject jsonObject = null;
        parsingResultStatus = ParsingResultStatus.PENDING;
        try {
            jsonObject = JsonParser.parseString(lastMessage).getAsJsonObject();
            String parsedMessage = "";

            if(Configuration.getInstance().FRAME_PATTERN.size() == jsonObject.entrySet().size()){
                for (String sensorName : Configuration.getInstance().FRAME_PATTERN) {
                    try {
                        parsedMessage += jsonObject.get(sensorName).getAsString() + Configuration.getInstance().FRAME_DELIMITER;
                        sensorRepository.getSensorByName(sensorName).setValue(Double.parseDouble(jsonObject.get(sensorName).getAsString()));
                    } catch (NumberFormatException e) {
                        parsingResultStatus = ParsingResultStatus.ERROR;
                        this.lastMessage = "Invalid: " + this.lastMessage;
                        logger.warn("Wrong message, value is not a number! {}", lastMessage + " -> " + jsonObject.get(sensorName).getAsString());
                    }
                }
                frame.setFormattedContent(parsedMessage.substring(0,parsedMessage.length()-1) + "\n");
            } else {
                parsingResultStatus = ParsingResultStatus.ERROR;
                this.lastMessage = "Invalid: " + this.lastMessage;
                var logMessage = Configuration.getInstance().FRAME_PATTERN.size() + " got: " + jsonObject.entrySet().size();
                logger.warn("Wrong message length! Expected: {}", logMessage);
                frame.setFormattedContent(lastMessage);
            }
            parsingResultStatus = ParsingResultStatus.OK;

        } catch (Exception e) {
            parsingResultStatus = ParsingResultStatus.ERROR;
            this.lastMessage = "Not valid json: " + lastMessage;
            logger.error("Not valid json: {}",lastMessage);
        }

        notifyObserver();
    }
}
