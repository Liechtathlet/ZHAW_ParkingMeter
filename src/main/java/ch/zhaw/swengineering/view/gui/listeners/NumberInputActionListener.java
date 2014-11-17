package ch.zhaw.swengineering.view.gui.listeners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigInteger;

import javax.swing.JButton;

import ch.zhaw.swengineering.view.gui.DisplayTextAppenderInterface;

/**
 * @author Daniel Brun
 * 
 *         Action listener for all number buttons.
 */
public class NumberInputActionListener implements ActionListener {

    private BigInteger integerInput;

    private DisplayTextAppenderInterface displayer;

    private boolean suspendEvent;

    /**
     * Creates a new instance of this class.
     * 
     * @param aDisplayer
     *            The displayer to output the integer.
     */
    public NumberInputActionListener(DisplayTextAppenderInterface aDisplayer) {
        integerInput = null;
        displayer = aDisplayer;
        suspendEvent = false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    @Override
    public void actionPerformed(ActionEvent anEvent) {
        if (!suspendEvent) {
            if (anEvent.getSource() instanceof JButton) {
                JButton button = (JButton) anEvent.getSource();
                BigInteger enteredInt = new BigInteger(button.getText());

                if (integerInput == null) {
                    integerInput = enteredInt;
                } else {
                    integerInput = integerInput.multiply(BigInteger.TEN);
                    integerInput = integerInput.add(enteredInt);
                }
                displayer.appendTextToPromptDisplay(integerInput.toString());
            }
        }
    }

    /**
     * Resets the integer input.
     */
    public void reset() {
        integerInput = null;
        displayer.appendTextToPromptDisplay("");
    }

    /**
     * @return the integerInput
     */
    public BigInteger getIntegerInput() {
        return integerInput;
    }

    /**
     * @param suspendEvent
     *            the suspendEvent to set
     */
    public void setSuspendEvent(boolean suspendEvent) {
        this.suspendEvent = suspendEvent;
    }

}
