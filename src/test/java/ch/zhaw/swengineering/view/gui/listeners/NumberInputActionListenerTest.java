package ch.zhaw.swengineering.view.gui.listeners;

import ch.zhaw.swengineering.view.gui.DisplayTextAppenderInterface;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.math.BigInteger;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class NumberInputActionListenerTest {

    @Mock
    private DisplayTextAppenderInterface displayer;

    @Mock
    private ActionEvent event;

    @Mock
    private JButton button;

    @InjectMocks
    private NumberInputActionListener listener;

    @Before
    public final void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testActionPerformed() throws Exception {

        // Mock
        String text = "6";
        when(button.getText()).thenReturn(text);

        when(event.getSource()).thenReturn(button);

        // Run
        listener.actionPerformed(event);
        listener.actionPerformed(event);

        // Assert
        assertEquals(
                listener.getIntegerInput(),
                BigInteger.valueOf(6 * 10 + 6));
    }

    @Test
    public void testReset() throws Exception {

        // Run
        listener.reset();

        // Assert
        verify(displayer).appendTextToPromptDisplay("");
    }

    @Test
    public void testGetIntegerInput() throws Exception {

        // Run
        BigInteger integerInput = listener.getIntegerInput();

        // Assert
        assertEquals(integerInput, null);
    }

    @Test
    public void testSetSuspendEvent() throws Exception {

        // Mock
        boolean suspendEvent = false;

        // Run
        listener.setSuspendEvent(suspendEvent);

        // Assert
        assertEquals(suspendEvent, listener.suspendEvent);
    }
}