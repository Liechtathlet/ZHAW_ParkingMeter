package ch.zhaw.swengineering.view.gui.listeners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;

import javax.swing.JButton;

import ch.zhaw.swengineering.view.gui.DisplayTextAppenderInterface;

/**
 * @author Daniel Brun
 * 
 * Action listener for all coin input buttons.
 */
public class CoinInputActionListener implements ActionListener {

    private BigDecimal coinInput;

    private DisplayTextAppenderInterface displayer;

    /**
     * Creates a new instance of this class.
     * 
     * @param aDisplayer
     *            The displayer to output the coin value.
     */
    public CoinInputActionListener(DisplayTextAppenderInterface aDisplayer) {
        coinInput = null;
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

            BigDecimal enteredCoin = new BigDecimal(button.getText());
            enteredCoin = enteredCoin.setScale(2);

            if (coinInput == null) {
                coinInput = enteredCoin;
            } else {
                coinInput = coinInput.add(enteredCoin);
            }
            displayer.appendTextToDisplay(coinInput.toString());
        }
    }

    /**
     * Resets the coin input.
     */
    public void reset() {
        coinInput = null;
        displayer.appendTextToDisplay("");
    }

    /**
     * @return the coinInput
     */
    public BigDecimal getCoinInput() {
        if (coinInput == null) {
            return BigDecimal.ZERO;
        } else {
            return coinInput;
        }
    }
}
