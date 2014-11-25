package ch.zhaw.swengineering.view.gui;

import ch.zhaw.swengineering.view.gui.listeners.NumberInputActionListener;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import javax.swing.*;
import java.math.BigInteger;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.TimeoutException;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ParkingMeterPanelTest {

    @Mock
    private CyclicBarrier cyclicBarrier;

    @Mock
    private NumberInputActionListener numberInputActionListener;

    @Mock
    private JTextArea display;

    @Mock
    private JTextArea infoDisplay;

    @Mock
    private JTextArea errorDisplay;

    @InjectMocks
    private ParkingMeterPanel parkingMeterPanel;

    @Before
    public void setUp() throws BrokenBarrierException, InterruptedException {
        MockitoAnnotations.initMocks(this);

        // Required to set actionExecuted to true.
        doReturn(0).when(cyclicBarrier).await();
        parkingMeterPanel.interruptWait();
    }

    @Test
    public void testInterruptWaitCallsCorrectMethod()
            throws BrokenBarrierException, InterruptedException, TimeoutException {

        // Mock
        doReturn(0).when(cyclicBarrier).await();

        // Run
        parkingMeterPanel.interruptWait();

        // Assert
        verify(cyclicBarrier, times(2)).await();
    }

    @Test
    public void testWaitForOKCallsCorrectMethod()
            throws BrokenBarrierException, InterruptedException {

        // Mock
        doReturn(0).when(cyclicBarrier).await();

        // Run
        boolean resultOK = parkingMeterPanel.waitForOK();

        // Assert
        verify(cyclicBarrier, times(2)).await();
        assertEquals(true, resultOK);
    }

    @Test
    public void testReadIntegerGetsCorrectNumber() {
        int expectedResult = 0;

        // Mock
        doReturn(BigInteger.valueOf(expectedResult))
                .when(numberInputActionListener).getIntegerInput();

        // Run
        int result = parkingMeterPanel.readInteger();

        // Assert
        assertEquals(expectedResult, result);
    }

    @Test
    public void testReadIntegerReturnsNullWhenNull() {
        Integer expectedResult = null;

        // Mock
        doReturn(expectedResult)
                .when(numberInputActionListener).getIntegerInput();

        // Run
        Integer result = parkingMeterPanel.readInteger();

        // Assert
        assertEquals(expectedResult, result);
    }

    @Test
    public void testAppendTextToPromptDisplayDoesAppendText() {
        String text = "darth vader";

        // Preparation
        doReturn("err").when(errorDisplay).getText();
        parkingMeterPanel.lastErrorText = "old err";
        parkingMeterPanel.printPrompt("");

        // Run
        parkingMeterPanel
                .appendTextToPromptDisplay(text);

        // Assert
        verify(display).setText(text);
    }

    @Test
    public void testPrintPromptCallsCorrectMethods() {
        String text = "darth vader";

        // Mock
        doReturn("err").when(errorDisplay).getText();

        // Run
        parkingMeterPanel.printPrompt(text);

        // Assert
        verify(display).setText(text);
        verify(numberInputActionListener).reset();
    }

//    @Test
//    public void testPrintPromptResetsErrorDisplay() {
//        String text = "err";
//
//        // Mock
//        parkingMeterPanel.lastErrorText = text;
//        doReturn(text).when(errorDisplay).getText();
//
//        // Run
//        parkingMeterPanel.printPrompt(text);
//
//        // Assert
//        verify(parkingMeterPanel).resetErrorDisplay();
//    }

//    @Test
//    public void testPrintPromptStoresLastErrorMessage() {
//        String text = "err";
//
//        // Mock
//        doReturn(text).when(errorDisplay).getText();
//
//        // Run
//        parkingMeterPanel.printPrompt(text);
//
//        // Assert
//        verify(parkingMeterPanel).resetErrorDisplay();
//    }

    @Test
    public void testPrintErrorShowsError() {
        String text = "err";

        // Run
        parkingMeterPanel.printError(text);

        // Assert
        verify(errorDisplay).setText(text);
    }

    @Test
    public void testPrintInfoShowsText() {
        String text = "Lightsaber";

        // Preparation
        parkingMeterPanel.currentInfoBufferElements = 3;

        // Run
        parkingMeterPanel.printInfo(text);

        // Assert
        verify(infoDisplay).append(text);
        verify(infoDisplay).append("\n");
        assertEquals(1, parkingMeterPanel.currentInfoBufferElements);
    }

//    @Test
//    public void testPrintInfoResetsDisplay() {
//        String text = "Lightsaber";
//        int buffer = 2;
//
//        // Preparation
//        parkingMeterPanel.currentInfoBufferElements = buffer;
//
//        // Run
//        parkingMeterPanel.printInfo(text);
//
//        // Assert
//        verify(parkingMeterPanel).resetInfoDisplay();
//    }

    @Test
    public void testResetPromptDisplayCallsCorrectMethod() {

        // Run
        parkingMeterPanel.resetPromptDisplay();

        // Assert
        verify(display).setText("");
    }

    @Test
    public void testSetNumberBlockBlockedCallsCorrectMethod() {

        boolean isBlocked = true;

        // Run
        parkingMeterPanel.setNumberBlockBlocked(isBlocked);

        // Assert
        verify(numberInputActionListener).setSuspendEvent(isBlocked);
    }

    @Test
    public void testIncreaseInfoBufferSizeTemporarilySetsCorrectBufferSize() {

        int expectedResult = 5;

        // Run
        parkingMeterPanel.increaseInfoBufferSizeTemporarily(expectedResult);

        // Assert
        assertEquals(expectedResult, parkingMeterPanel.infoMsgBufferSize);
    }
}