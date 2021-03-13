package pl.edu.pwr.pwrinspace.poliwrocket.Model.MessageParser;

import javafx.beans.InvalidationListener;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.Configuration.Configuration;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.Sensor.ISensorRepository;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.Sensor.Sensor;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

class StandardMessageParserTest {

    @Mock
    private ISensorRepository mockSensorRepository;

    @Mock
    private Sensor mockSensor1;
    @Mock
    private Sensor mockSensor2;
    @Mock
    private Sensor mockSensor3;
    @Mock
    private Sensor mockSensor4;
    @Mock
    private InvalidationListener mockListener;

    private StandardMessageParser standardMessageParserUnderTest;

    @BeforeEach
    void setUp() {
        initMocks(this);
        standardMessageParserUnderTest = new StandardMessageParser(mockSensorRepository);
        standardMessageParserUnderTest.addListener(mockListener);
    }

    @Test
    void testParseMessageValid() {
        // Setup
        final String frameContent = "-999.12340;2.0;3.0;-4.0";
        final var time = LocalDateTime.of(2020, 1, 1, 0, 0, 0, 0).toInstant(ZoneOffset.UTC);
        final Frame frame = new Frame(frameContent, time);
        Configuration.getInstance().FRAME_PATTERN = Arrays.asList("name1","name2","name3","name4");
        Configuration.getInstance().FRAME_DELIMITER = ";";
        when(mockSensorRepository.getSensorByName("name1")).thenReturn(mockSensor1);
        when(mockSensorRepository.getSensorByName("name2")).thenReturn(mockSensor2);
        when(mockSensorRepository.getSensorByName("name3")).thenReturn(mockSensor3);
        when(mockSensorRepository.getSensorByName("name4")).thenReturn(mockSensor4);
        when(mockSensorRepository.getSensorsKeys()).thenReturn(Set.of("name1","name2","name3","name4"));
        // Run the test
        standardMessageParserUnderTest.parseMessage(frame);

        // Verify the results
        assertEquals(frameContent, standardMessageParserUnderTest.getLastMessage());
        assertEquals(time,frame.getTimeInstant());
        verify(mockSensor1).setValue(-999.12340);
        verify(mockSensor2).setValue(2.0);
        verify(mockSensor3).setValue(3.0);
        verify(mockSensor4).setValue(-4.0);

        verify(mockListener).invalidated(standardMessageParserUnderTest);
    }

    @Test
    void testParseMessageValidChangedDelimiter() {
        // Setup
        final String frameContent = "-999.12340#2.0#3.0#-4.0";
        final var time = LocalDateTime.of(2020, 1, 1, 0, 0, 0, 0).toInstant(ZoneOffset.UTC);
        final Frame frame = new Frame(frameContent, time);
        Configuration.getInstance().FRAME_PATTERN = Arrays.asList("name1","name2","name3","name4");
        Configuration.getInstance().FRAME_DELIMITER = "#";
        when(mockSensorRepository.getSensorByName("name1")).thenReturn(mockSensor1);
        when(mockSensorRepository.getSensorByName("name2")).thenReturn(mockSensor2);
        when(mockSensorRepository.getSensorByName("name3")).thenReturn(mockSensor3);
        when(mockSensorRepository.getSensorByName("name4")).thenReturn(mockSensor4);
        when(mockSensorRepository.getSensorsKeys()).thenReturn(Set.of("name1","name2","name3","name4"));
        // Run the test
        standardMessageParserUnderTest.parseMessage(frame);

        // Verify the results
        assertEquals(frameContent, standardMessageParserUnderTest.getLastMessage());
        assertEquals(time,frame.getTimeInstant());
        verify(mockSensor1).setValue(-999.12340);
        verify(mockSensor2).setValue(2.0);
        verify(mockSensor3).setValue(3.0);
        verify(mockSensor4).setValue(-4.0);

        verify(mockListener).invalidated(standardMessageParserUnderTest);
    }

    @Test
    void testParseMessageValidRemoveListener() {
        // Setup
        final String frameContent = "-999.12340;2.0;3.0;-4.0";
        final var time = LocalDateTime.of(2020, 1, 1, 0, 0, 0, 0).toInstant(ZoneOffset.UTC);
        final Frame frame = new Frame(frameContent, time);
        Configuration.getInstance().FRAME_PATTERN = Arrays.asList("name1","name2","name3","name4");
        Configuration.getInstance().FRAME_DELIMITER = ";";
        when(mockSensorRepository.getSensorByName("name1")).thenReturn(mockSensor1);
        when(mockSensorRepository.getSensorByName("name2")).thenReturn(mockSensor2);
        when(mockSensorRepository.getSensorByName("name3")).thenReturn(mockSensor3);
        when(mockSensorRepository.getSensorByName("name4")).thenReturn(mockSensor4);
        when(mockSensorRepository.getSensorsKeys()).thenReturn(Set.of("name1","name2","name3","name4"));
        // Run the test
        standardMessageParserUnderTest.removeListener(mockListener);
        standardMessageParserUnderTest.parseMessage(frame);

        // Verify the results
        assertEquals(frameContent, standardMessageParserUnderTest.getLastMessage());
        assertEquals(time,frame.getTimeInstant());
        verify(mockSensor1).setValue(-999.12340);
        verify(mockSensor2).setValue(2.0);
        verify(mockSensor3).setValue(3.0);
        verify(mockSensor4).setValue(-4.0);

        verify(mockListener, never()).invalidated(standardMessageParserUnderTest);
    }

    @Test
    void testParseMessageValidChangeOrder() {
        // Setup
        final String frameContent = "-999.12340;2.0;3.0;-4.0";
        final Frame frame = new Frame(frameContent, LocalDateTime.of(2020, 1, 1, 0, 0, 0, 0).toInstant(ZoneOffset.UTC));
        Configuration.getInstance().FRAME_PATTERN = Arrays.asList("name2","name4","name3","name1");
        Configuration.getInstance().FRAME_DELIMITER = ";";
        when(mockSensorRepository.getSensorByName("name1")).thenReturn(mockSensor1);
        when(mockSensorRepository.getSensorByName("name2")).thenReturn(mockSensor2);
        when(mockSensorRepository.getSensorByName("name3")).thenReturn(mockSensor3);
        when(mockSensorRepository.getSensorByName("name4")).thenReturn(mockSensor4);
        when(mockSensorRepository.getSensorsKeys()).thenReturn(Set.of("name2","name4","name3","name1"));
        // Run the test
        standardMessageParserUnderTest.parseMessage(frame);

        // Verify the results
        assertEquals(frameContent, standardMessageParserUnderTest.getLastMessage());
        verify(mockSensor2).setValue(-999.12340);
        verify(mockSensor4).setValue(2.0);
        verify(mockSensor3).setValue(3.0);
        verify(mockSensor1).setValue(-4.0);

        verify(mockListener).invalidated(standardMessageParserUnderTest);
    }

    @Test
    void testParseMessageInvalidNotNumber() {
        // Setup
        final String frameContent = "-999.12340;2.0;3.0;-3da";
        final Frame frame = new Frame(frameContent, LocalDateTime.of(2020, 1, 1, 0, 0, 0, 0).toInstant(ZoneOffset.UTC));
        Configuration.getInstance().FRAME_PATTERN = Arrays.asList("name1","name2","name3","name4");
        Configuration.getInstance().FRAME_DELIMITER = ";";
        when(mockSensorRepository.getSensorByName("name1")).thenReturn(mockSensor1);
        when(mockSensorRepository.getSensorByName("name2")).thenReturn(mockSensor2);
        when(mockSensorRepository.getSensorByName("name3")).thenReturn(mockSensor3);
        when(mockSensorRepository.getSensorByName("name4")).thenReturn(mockSensor4);
        when(mockSensorRepository.getSensorsKeys()).thenReturn(Set.of("name1","name2","name3","name4"));
        // Run the test
        standardMessageParserUnderTest.parseMessage(frame);

        // Verify the results
        assertEquals("Invalid: " + frameContent, standardMessageParserUnderTest.getLastMessage());
        verify(mockSensor1).setValue(-999.12340);
        verify(mockSensor2).setValue(2.0);
        verify(mockSensor3).setValue(3.0);
        verify(mockSensor4,never()).setValue(any(double.class));

        verify(mockListener).invalidated(standardMessageParserUnderTest);
    }

    @Test
    void testParseMessageInvalidDelimiter() {
        // Setup
        final String frameContent = "-999.12340#2.0#3.0#-3.0";
        final Frame frame = new Frame(frameContent, LocalDateTime.of(2020, 1, 1, 0, 0, 0, 0).toInstant(ZoneOffset.UTC));
        Configuration.getInstance().FRAME_PATTERN = Arrays.asList("name1","name2","name3","name4");
        Configuration.getInstance().FRAME_DELIMITER = ";";
        when(mockSensorRepository.getSensorByName("name1")).thenReturn(mockSensor1);
        when(mockSensorRepository.getSensorByName("name2")).thenReturn(mockSensor2);
        when(mockSensorRepository.getSensorByName("name3")).thenReturn(mockSensor3);
        when(mockSensorRepository.getSensorByName("name4")).thenReturn(mockSensor4);
        when(mockSensorRepository.getSensorsKeys()).thenReturn(Set.of("name1","name2","name3","name4"));
        // Run the test
        standardMessageParserUnderTest.parseMessage(frame);

        // Verify the results
        assertEquals("Invalid: " + frameContent, standardMessageParserUnderTest.getLastMessage());
        verify(mockSensor1,never()).setValue(any(double.class));
        verify(mockSensor2,never()).setValue(any(double.class));
        verify(mockSensor3,never()).setValue(any(double.class));
        verify(mockSensor4,never()).setValue(any(double.class));
        verify(mockSensorRepository,never()).getSensorByName(anyString());
        verify(mockListener).invalidated(standardMessageParserUnderTest);
    }

    @Test
    void testParseMessageInvalidLengthLess() {
        // Setup
        final String frameContent = "-999.12340;2.0;3.0";
        final Frame frame = new Frame(frameContent, LocalDateTime.of(2020, 1, 1, 0, 0, 0, 0).toInstant(ZoneOffset.UTC));
        Configuration.getInstance().FRAME_PATTERN = Arrays.asList("name1","name2","name3","name4");
        Configuration.getInstance().FRAME_DELIMITER = ";";
        when(mockSensorRepository.getSensorByName("name1")).thenReturn(mockSensor1);
        when(mockSensorRepository.getSensorByName("name2")).thenReturn(mockSensor2);
        when(mockSensorRepository.getSensorByName("name3")).thenReturn(mockSensor3);
        when(mockSensorRepository.getSensorByName("name4")).thenReturn(mockSensor4);
        when(mockSensorRepository.getSensorsKeys()).thenReturn(Set.of("name1","name2","name3","name4"));
        // Run the test
        standardMessageParserUnderTest.parseMessage(frame);

        // Verify the results
        assertEquals("Invalid: " + frameContent, standardMessageParserUnderTest.getLastMessage());
        verify(mockSensor1,never()).setValue(any(double.class));
        verify(mockSensor2,never()).setValue(any(double.class));
        verify(mockSensor3,never()).setValue(any(double.class));
        verify(mockSensor4,never()).setValue(any(double.class));
        verify(mockSensorRepository,never()).getSensorByName(anyString());
        verify(mockListener).invalidated(standardMessageParserUnderTest);
    }

    @Test
    void testParseMessageInvalidLengthMore() {
        // Setup
        final String frameContent = "-999.12340;2.0;3.0;-1.0;-9292.2";
        final Frame frame = new Frame(frameContent, LocalDateTime.of(2020, 1, 1, 0, 0, 0, 0).toInstant(ZoneOffset.UTC));
        Configuration.getInstance().FRAME_PATTERN = Arrays.asList("name1","name2","name3","name4");
        Configuration.getInstance().FRAME_DELIMITER = ";";
        when(mockSensorRepository.getSensorByName("name1")).thenReturn(mockSensor1);
        when(mockSensorRepository.getSensorByName("name2")).thenReturn(mockSensor2);
        when(mockSensorRepository.getSensorByName("name3")).thenReturn(mockSensor3);
        when(mockSensorRepository.getSensorByName("name4")).thenReturn(mockSensor4);
        when(mockSensorRepository.getSensorsKeys()).thenReturn(Set.of("name1","name2","name3","name4"));
        // Run the test
        standardMessageParserUnderTest.parseMessage(frame);

        // Verify the results
        assertEquals("Invalid: " + frameContent, standardMessageParserUnderTest.getLastMessage());
        verify(mockSensor1,never()).setValue(any(double.class));
        verify(mockSensor2,never()).setValue(any(double.class));
        verify(mockSensor3,never()).setValue(any(double.class));
        verify(mockSensor4,never()).setValue(any(double.class));
        verify(mockSensorRepository,never()).getSensorByName(anyString());
        verify(mockListener).invalidated(standardMessageParserUnderTest);
    }
}
