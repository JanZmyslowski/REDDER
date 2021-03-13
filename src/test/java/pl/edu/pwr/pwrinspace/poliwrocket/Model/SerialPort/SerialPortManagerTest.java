package pl.edu.pwr.pwrinspace.poliwrocket.Model.SerialPort;

import gnu.io.NRSerialPort;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import javafx.beans.InvalidationListener;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.reflect.Whitebox;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.MessageParser.Frame;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.MessageParser.IMessageParser;
import pl.edu.pwr.pwrinspace.poliwrocket.Service.Save.FrameSaveService;

import java.io.InputStream;
import java.util.ArrayList;

import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

class SerialPortManagerTest {

    private ArrayList<InvalidationListener> listOfMockObservers = new ArrayList();

    @Mock
    private NRSerialPort mockSerialPort;

    @Mock
    private InvalidationListener mockListener;

    @Mock
    private IMessageParser mockMessageParser;

    @Mock
    private FrameSaveService mockFrameSaveService;

    private SerialPortManager serialPortManagerUnderTest;

    @BeforeEach
    void setUp() {
        initMocks(this);
        serialPortManagerUnderTest = PowerMockito.spy(SerialPortManager.getInstance());
        listOfMockObservers = new ArrayList<>();
        Whitebox.setInternalState(serialPortManagerUnderTest,"observers", listOfMockObservers);
        Whitebox.setInternalState(serialPortManagerUnderTest,"serialPort",mockSerialPort);

    }

    @Test
    void testAddListener() {
        // Setup
        final InvalidationListener mockInvalidationListener = mock(InvalidationListener.class);

        // Run the test
        serialPortManagerUnderTest.addListener(mockInvalidationListener);

        // Verify the results
        assertEquals(1, listOfMockObservers.size());
    }

    @Test
    void testRemoveListener() {
        // Setup
        final InvalidationListener mockInvalidationListener = mock(InvalidationListener.class);

        // Run the test
        serialPortManagerUnderTest.addListener(mockInvalidationListener);

        // Verify the results
        assertEquals(1, listOfMockObservers.size());

        // Run the test
        serialPortManagerUnderTest.removeListener(mockInvalidationListener);

        // Verify the results
        assertEquals(0, listOfMockObservers.size());
    }

    @Test
    void testClose() throws Exception {
        // Setup
        Mockito.doNothing().when(mockSerialPort).removeEventListener();
        Mockito.doNothing().when(mockSerialPort).disconnect();
        when(mockSerialPort.isConnected()).thenReturn(false);
        Whitebox.setInternalState(serialPortManagerUnderTest,"observers", new ArrayList<>());
        serialPortManagerUnderTest.addListener(mockListener);
        // Run the test
        serialPortManagerUnderTest.close();

        // Verify the results
        verify(mockSerialPort, times(1)).removeEventListener();
        verify(mockSerialPort, times(1)).disconnect();
        verify(mockSerialPort, times(1)).isConnected();
        assertFalse(serialPortManagerUnderTest.isPortOpen());
        verify(mockListener,times(1)).invalidated(serialPortManagerUnderTest);

    }

    @Test
    void testCloseNull() throws Exception {
        // Setup
        Mockito.doNothing().when(mockSerialPort).removeEventListener();
        Mockito.doNothing().when(mockSerialPort).disconnect();
        when(mockSerialPort.isConnected()).thenReturn(false);
        Whitebox.setInternalState(serialPortManagerUnderTest,"serialPort", (NRSerialPort)null);
        final boolean beforeState = serialPortManagerUnderTest.isPortOpen();
        serialPortManagerUnderTest.addListener(mockListener);
        // Run the test
        serialPortManagerUnderTest.close();

        // Verify the results
        assertEquals(beforeState, serialPortManagerUnderTest.isPortOpen());
        verify(mockSerialPort, never()).removeEventListener();
        verify(mockSerialPort, never()).disconnect();
        verify(mockSerialPort, never()).isConnected();
        verify(mockListener,never()).invalidated(serialPortManagerUnderTest);
    }

    @Test
    void testSerialEvent() {
        // Setup
        final SerialPortEvent oEvent = new SerialPortEvent(Mockito.mock(SerialPort.class), 1, false, false);
        var mockInputStream = mock(InputStream.class);
        Whitebox.setInternalState(serialPortManagerUnderTest,"inputStream", mockInputStream);

        serialPortManagerUnderTest.setMessageParser(mockMessageParser);
        serialPortManagerUnderTest.setFrameSaveService(mockFrameSaveService);
        // Run the test
        serialPortManagerUnderTest.serialEvent(oEvent);

        // Verify the results
        verify(mockMessageParser,times(1)).parseMessage(any(Frame.class));
        verify(mockFrameSaveService,times(1)).saveFrameToFile(any(Frame.class));
    }

    @Test
    void testSerialEventOnlyParser() {
        // Setup
        final SerialPortEvent oEvent = new SerialPortEvent(Mockito.mock(SerialPort.class), 1, false, false);
        var mockInputStream = mock(InputStream.class);
        Whitebox.setInternalState(serialPortManagerUnderTest,"inputStream", mockInputStream);

        serialPortManagerUnderTest.setMessageParser(mockMessageParser);
        // Run the test
        serialPortManagerUnderTest.serialEvent(oEvent);

        // Verify the results
        verify(mockMessageParser,times(1)).parseMessage(any(Frame.class));
        verify(mockFrameSaveService,never()).saveFrameToFile(any(Frame.class));
    }

    @Test
    void testWrite() {
        // Setup
        var mockWriter = mock(SerialPortManager.SerialWriter.class);
        Whitebox.setInternalState(serialPortManagerUnderTest,"serialWriter", mockWriter);
        serialPortManagerUnderTest.addListener(mockListener);
        // Run the test
        serialPortManagerUnderTest.write("message");

        // Verify the results
        verify(mockWriter,times(1)).send("message");
        verify(mockListener,times(1)).invalidated(serialPortManagerUnderTest);
        assertEquals("message", serialPortManagerUnderTest.getLastSend());
    }
}
