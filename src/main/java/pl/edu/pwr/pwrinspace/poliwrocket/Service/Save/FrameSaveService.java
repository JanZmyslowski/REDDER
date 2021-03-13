package pl.edu.pwr.pwrinspace.poliwrocket.Service.Save;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.Configuration.Configuration;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.MessageParser.Frame;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class FrameSaveService {

    private static final Logger logger = LoggerFactory.getLogger(FrameSaveService.class);
    private final File flightDataFile = new File(Configuration.FLIGHT_DATA_PATH + Configuration.FLIGHT_DATA_FILE_NAME);

    public void saveFrameToFile(Frame frame) {
        try {
            FileWriter fileWriter = new FileWriter(flightDataFile, true);
            try (BufferedWriter output = new BufferedWriter(fileWriter)) {
                output.write(frame.getTimeInstant().toString() + Configuration.getInstance().FRAME_DELIMITER + frame.getFormattedContent());
            }
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    public void writeFileHeader(List<String> headers) {
        StringBuilder header = new StringBuilder();
        headers.forEach(h -> header.append(Configuration.getInstance().FRAME_DELIMITER).append(h));
        try {
            FileWriter fileWriter = new FileWriter(flightDataFile, true);
            try (BufferedWriter output = new BufferedWriter(fileWriter)) {
                output.write("Time" + header.toString());
                output.newLine();
            }
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }
}
