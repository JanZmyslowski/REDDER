package pl.edu.pwr.pwrinspace.poliwrocket.Thred;

import javafx.beans.InvalidationListener;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.powermock.reflect.Whitebox;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

class CountdownThreadTest {

    private CountdownThread countdownThreadUnderTest;

    private ArrayList<InvalidationListener> listOfMockObservers = new ArrayList();

    @Mock
    private InvalidationListener mockListener;


    @BeforeEach
    void setUp() {
        initMocks(this);
        countdownThreadUnderTest = new CountdownThread();
        listOfMockObservers = new ArrayList<>();
        listOfMockObservers.add(mockListener);
        Whitebox.setInternalState(countdownThreadUnderTest,"observers", listOfMockObservers);
    }

    @Test
    void testUpdate() {
        // Setup

        // Run the test
        countdownThreadUnderTest.update(5000);

        // Verify the results
        verify(mockListener,times(1)).invalidated(countdownThreadUnderTest);
        assertEquals("-00:00:05:000",countdownThreadUnderTest.getFormattedTimeResult());

        // Run the test
        countdownThreadUnderTest.update(0);

        // Verify the results
        verify(mockListener,times(2)).invalidated(countdownThreadUnderTest);
        assertEquals("+00:00:00:000",countdownThreadUnderTest.getFormattedTimeResult());

        // Run the test
        countdownThreadUnderTest.update(300);

        // Verify the results
        verify(mockListener,times(3)).invalidated(countdownThreadUnderTest);
        assertEquals("-00:00:00:300",countdownThreadUnderTest.getFormattedTimeResult());

        // Run the test
        countdownThreadUnderTest.update(-20000100);

        // Verify the results
        verify(mockListener,times(4)).invalidated(countdownThreadUnderTest);
        assertEquals("+05:33:20:100",countdownThreadUnderTest.getFormattedTimeResult());

        // Run the test
        countdownThreadUnderTest.update(20000100);

        // Verify the results
        verify(mockListener,times(5)).invalidated(countdownThreadUnderTest);
        assertEquals("-05:33:20:100",countdownThreadUnderTest.getFormattedTimeResult());
    }

    @Test
    void testResetCountdown() {
        // Setup

        // Run the test
        countdownThreadUnderTest.resetCountdown();

        // Verify the results
        verify(mockListener,times(1)).invalidated(countdownThreadUnderTest);
        assertEquals(10000,countdownThreadUnderTest.getCountdownTime());
        assertEquals("-00:00:10:000",countdownThreadUnderTest.getFormattedTimeResult());
        assertFalse(countdownThreadUnderTest.isCanRun());
    }
}
