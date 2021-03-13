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
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

class JsonMessageParserTest {

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

    private JsonMessageParser jsonMessageParserUnderTest;

    @BeforeEach
    void setUp() {
        initMocks(this);
        jsonMessageParserUnderTest = new JsonMessageParser(mockSensorRepository);
        jsonMessageParserUnderTest.addListener(mockListener);
    }

    @Test
    void testParseMessageValid() {
        // Setup
        final String frameContent = "{\"name1\":1.0,\"name2\":-999.123,\"name3\":3.0,\"name4\":9999999}";
        final var time = LocalDateTime.of(2020, 1, 1, 0, 0, 0, 0).toInstant(ZoneOffset.UTC);
        final Frame frame = new Frame(frameContent, time);
        Configuration.getInstance().FRAME_PATTERN = Arrays.asList("name1","name2","name3","name4");
        when(mockSensorRepository.getSensorByName("name1")).thenReturn(mockSensor1);
        when(mockSensorRepository.getSensorByName("name2")).thenReturn(mockSensor2);
        when(mockSensorRepository.getSensorByName("name3")).thenReturn(mockSensor3);
        when(mockSensorRepository.getSensorByName("name4")).thenReturn(mockSensor4);
        when(mockSensorRepository.getSensorsKeys()).thenReturn(Set.of("name1","name2","name3","name4"));
        // Run the test
        jsonMessageParserUnderTest.parseMessage(frame);

        // Verify the results
        assertEquals(frameContent,jsonMessageParserUnderTest.getLastMessage());
        verify(mockSensor1).setValue(1.0);
        verify(mockSensor1,times(1)).setValue(anyDouble());
        verify(mockSensor2).setValue(-999.123);
        verify(mockSensor2,times(1)).setValue(anyDouble());
        verify(mockSensor3).setValue(3.0);
        verify(mockSensor3,times(1)).setValue(anyDouble());
        verify(mockSensor4).setValue(9999999);
        verify(mockSensor4,times(1)).setValue(anyDouble());

        verify(mockListener).invalidated(jsonMessageParserUnderTest);
    }

    @Test
    void testParseMessageValidRemoveListener() {
        // Setup
        final String frameContent = "{\"name1\":1.0,\"name2\":2.0,\"name3\":3.0,\"name4\":4.0}";
        final var time = LocalDateTime.of(2020, 1, 1, 0, 0, 0, 0).toInstant(ZoneOffset.UTC);
        final Frame frame = new Frame(frameContent, time);
        Configuration.getInstance().FRAME_PATTERN = Arrays.asList("name1","name2","name3","name4");
        when(mockSensorRepository.getSensorByName("name1")).thenReturn(mockSensor1);
        when(mockSensorRepository.getSensorByName("name2")).thenReturn(mockSensor2);
        when(mockSensorRepository.getSensorByName("name3")).thenReturn(mockSensor3);
        when(mockSensorRepository.getSensorByName("name4")).thenReturn(mockSensor4);
        when(mockSensorRepository.getSensorsKeys()).thenReturn(Set.of("name1","name2","name3","name4"));
        // Run the test
        jsonMessageParserUnderTest.removeListener(mockListener);
        jsonMessageParserUnderTest.parseMessage(frame);

        // Verify the results
        assertEquals(frameContent,jsonMessageParserUnderTest.getLastMessage());
        assertEquals(time,frame.getTimeInstant());
        verify(mockSensor1).setValue(1.0);
        verify(mockSensor2).setValue(2.0);
        verify(mockSensor3).setValue(3.0);
        verify(mockSensor4).setValue(4.0);

        verify(mockListener, never()).invalidated(jsonMessageParserUnderTest);
    }

    @Test
    void testParseMessageValidChangeOrder() {
        // Setup
        final String frameContent = "{\"name3\":1.0,\"name1\":2.0,\"name4\":3.0,\"name2\":4.0}";
        final Frame frame = new Frame(frameContent, LocalDateTime.of(2020, 1, 1, 0, 0, 0, 0).toInstant(ZoneOffset.UTC));
        Configuration.getInstance().FRAME_PATTERN = Arrays.asList("name1","name2","name3","name4");
        when(mockSensorRepository.getSensorByName("name1")).thenReturn(mockSensor1);
        when(mockSensorRepository.getSensorByName("name2")).thenReturn(mockSensor2);
        when(mockSensorRepository.getSensorByName("name3")).thenReturn(mockSensor3);
        when(mockSensorRepository.getSensorByName("name4")).thenReturn(mockSensor4);
        when(mockSensorRepository.getSensorsKeys()).thenReturn(Set.of("name1","name2","name3","name4"));
        // Run the test
        jsonMessageParserUnderTest.parseMessage(frame);

        // Verify the results
        assertEquals(frameContent,jsonMessageParserUnderTest.getLastMessage());
        verify(mockSensor3).setValue(1.0);
        verify(mockSensor1).setValue(2.0);
        verify(mockSensor4).setValue(3.0);
        verify(mockSensor2).setValue(4.0);

        verify(mockListener).invalidated(jsonMessageParserUnderTest);
    }

    @Test
    void testParseMessageInvalidNotNumber() {
        // Setup
        final String frameContent = "{\"name3\":as.0,\"name1\":2.0,\"name4\":3.0,\"name2\":4.0}";
        final Frame frame = new Frame(frameContent, LocalDateTime.of(2020, 1, 1, 0, 0, 0, 0).toInstant(ZoneOffset.UTC));
        Configuration.getInstance().FRAME_PATTERN = Arrays.asList("name1","name2","name3","name4");
        when(mockSensorRepository.getSensorByName("name1")).thenReturn(mockSensor1);
        when(mockSensorRepository.getSensorByName("name2")).thenReturn(mockSensor2);
        when(mockSensorRepository.getSensorByName("name3")).thenReturn(mockSensor3);
        when(mockSensorRepository.getSensorByName("name4")).thenReturn(mockSensor4);
        when(mockSensorRepository.getSensorsKeys()).thenReturn(Set.of("name1","name2","name3","name4"));
        // Run the test
        jsonMessageParserUnderTest.parseMessage(frame);

        // Verify the results
        assertEquals("Invalid: " + frameContent,jsonMessageParserUnderTest.getLastMessage());
        verify(mockSensor3,never()).setValue(any(double.class));
        verify(mockSensor1).setValue(2.0);
        verify(mockSensor4).setValue(3.0);
        verify(mockSensor2).setValue(4.0);

        verify(mockListener).invalidated(jsonMessageParserUnderTest);
    }

    @Test
    void testParseMessageInvalidNotJson() {
        // Setup
        final String frameContent = "{\"name3\":as.0,\"name1\":2.0\"name4\":3.0,\"name2\":4.";
        final Frame frame = new Frame(frameContent, LocalDateTime.of(2020, 1, 1, 0, 0, 0, 0).toInstant(ZoneOffset.UTC));
        Configuration.getInstance().FRAME_PATTERN = Arrays.asList("name1","name2","name3","name4");
        when(mockSensorRepository.getSensorByName("name1")).thenReturn(mockSensor1);
        when(mockSensorRepository.getSensorByName("name2")).thenReturn(mockSensor2);
        when(mockSensorRepository.getSensorByName("name3")).thenReturn(mockSensor3);
        when(mockSensorRepository.getSensorByName("name4")).thenReturn(mockSensor4);
        when(mockSensorRepository.getSensorsKeys()).thenReturn(Set.of("name1","name2","name3","name4"));
        // Run the test
        jsonMessageParserUnderTest.parseMessage(frame);

        // Verify the results
        assertEquals("Not valid json: " + frameContent,jsonMessageParserUnderTest.getLastMessage());
        verify(mockSensor1,never()).setValue(any(double.class));
        verify(mockSensor2,never()).setValue(any(double.class));
        verify(mockSensor3,never()).setValue(any(double.class));
        verify(mockSensor4,never()).setValue(any(double.class));
        verify(mockSensorRepository,never()).getSensorByName(anyString());
        verify(mockListener).invalidated(jsonMessageParserUnderTest);
    }

    @Test
    void testParseMessageInvalidLengthLess() {
        // Setup
        final String frameContent = "{\"name1\":1.0,\"name3\":3.0,\"name4\":4.0}";
        final Frame frame = new Frame(frameContent, LocalDateTime.of(2020, 1, 1, 0, 0, 0, 0).toInstant(ZoneOffset.UTC));
        Configuration.getInstance().FRAME_PATTERN = Arrays.asList("name1","name2","name3","name4");
        when(mockSensorRepository.getSensorByName("name1")).thenReturn(mockSensor1);
        when(mockSensorRepository.getSensorByName("name2")).thenReturn(mockSensor2);
        when(mockSensorRepository.getSensorByName("name3")).thenReturn(mockSensor3);
        when(mockSensorRepository.getSensorByName("name4")).thenReturn(mockSensor4);
        when(mockSensorRepository.getSensorsKeys()).thenReturn(Set.of("name1","name2","name3","name4"));
        // Run the test
        jsonMessageParserUnderTest.parseMessage(frame);

        // Verify the results
        assertEquals("Invalid: " + frameContent,jsonMessageParserUnderTest.getLastMessage());
        verify(mockSensor1,never()).setValue(any(double.class));
        verify(mockSensor2,never()).setValue(any(double.class));
        verify(mockSensor3,never()).setValue(any(double.class));
        verify(mockSensor4,never()).setValue(any(double.class));
        verify(mockSensorRepository,never()).getSensorByName(anyString());
        verify(mockListener).invalidated(jsonMessageParserUnderTest);
    }

    @Test
    void testParseMessageInvalidLengthMore() {
        // Setup
        final String frameContent = "{\"name1\":1.0,\"name2\":2.0,\"name3\":3.0,\"name4\":4.0,\"name5\":5.0}";
        final Frame frame = new Frame(frameContent, LocalDateTime.of(2020, 1, 1, 0, 0, 0, 0).toInstant(ZoneOffset.UTC));
        Configuration.getInstance().FRAME_PATTERN = Arrays.asList("name1","name2","name3","name4");
        when(mockSensorRepository.getSensorByName("name1")).thenReturn(mockSensor1);
        when(mockSensorRepository.getSensorByName("name2")).thenReturn(mockSensor2);
        when(mockSensorRepository.getSensorByName("name3")).thenReturn(mockSensor3);
        when(mockSensorRepository.getSensorByName("name4")).thenReturn(mockSensor4);
        when(mockSensorRepository.getSensorsKeys()).thenReturn(Set.of("name1","name2","name3","name4"));
        // Run the test
        jsonMessageParserUnderTest.parseMessage(frame);

        // Verify the results
        assertEquals("Invalid: " + frameContent,jsonMessageParserUnderTest.getLastMessage());
        verify(mockSensor1,never()).setValue(any(double.class));
        verify(mockSensor2,never()).setValue(any(double.class));
        verify(mockSensor3,never()).setValue(any(double.class));
        verify(mockSensor4,never()).setValue(any(double.class));
        verify(mockSensorRepository,never()).getSensorByName(anyString());
        verify(mockListener).invalidated(jsonMessageParserUnderTest);
    }
}
