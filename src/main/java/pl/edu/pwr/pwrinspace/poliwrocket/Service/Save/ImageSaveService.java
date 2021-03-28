package pl.edu.pwr.pwrinspace.poliwrocket.Service.Save;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.Configuration.Configuration;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.time.Instant;

public class ImageSaveService {

    private static final Logger logger = LoggerFactory.getLogger(ImageSaveService.class);

    public void saveImage(BufferedImage bufferedImage) {
        try {
            ByteArrayOutputStream bas = new ByteArrayOutputStream();
            ImageIO.write(bufferedImage, "png", bas);
            InputStream in = new ByteArrayInputStream(bas.toByteArray());
            BufferedImage image = ImageIO.read(in);
            File outputfile = new File(Configuration.FLIGHT_DATA_PATH + Instant.now().getEpochSecond() + "_thrust.png");
            ImageIO.write(image, "png", outputfile);
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }
}
