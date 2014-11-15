package ch.zhaw.swengineering.view.gui.listeners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigInteger;

import javax.swing.JButton;

import ch.zhaw.swengineering.view.gui.DisplayTextAppenderInterface;

/**
 * @author Daniel Brun
 * 
 */
public class NumberInputActionListener implements ActionListener {

    private BigInteger integerInput;

    private DisplayTextAppenderInterface displayer;

    /**
     * Creates a new instance of this class.
     * 
     * @param aDisplayer
     *            The displayer to output the integer.
     */
    public NumberInputActionListener(DisplayTextAppenderInterface aDisplayer) {
        integerInput = null;
        displayer = aDisplayer;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    @Override
    public void actionPerformed(ActionEvent anEvent) {
        if (anEvent.getSource() instanceof JButton) {
            JButton button = (JButton) anEvent.getSource();
            BigInteger enteredInt = new BigInteger(button.getText());

            if (integerInput == null) {
                integerInput = enteredInt;
            } else {
                integerInput = integerInput.multiply(BigInteger.TEN);
                integerInput = integerInput.add(enteredInt);
            }
            displayer.appendTextToDisplay(integerInput.toString());
        }
    }

    /**
     * Resets the integer input.
     */
    public void reset() {
        integerInput = null;
        displayer.appendTextToDisplay("");
    }

    /**
     * @return the integerInput
     */
    public BigInteger getIntegerInput() {
        if (integerInput == null) {
            return BigInteger.ZERO;
        } else {
            return integerInput;
        }
    }
}
