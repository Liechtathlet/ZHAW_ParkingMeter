package ch.zhaw.swengineering.slotmachine.exception;

import java.math.BigDecimal;

/**
 * @author Daniel Brun
 * 
 *         Exception which is thrown when one or all coin boxes are full.
 */
public class CoinBoxFullException extends RuntimeException {

    /**
     * GUID.
     */
    private static final long serialVersionUID = -4637910607251890137L;

    private BigDecimal coinValue;
    private boolean allCoinBoxesFull;

    /**
     * Creates a new instance of this class. If this constructor is used, it is
     * assumed, that all coin boxes are full.
     * 
     * @param aMessage
     *            the message.
     */
    public CoinBoxFullException(String aMessage) {
        super(aMessage);
        allCoinBoxesFull = true;
    }

    /**
     * Creates a new instance of this class. If this constructor is used, it is
     * assumed, that one specific coin box is full.
     * 
     * @param aMessage
     *            the message.
     * @param aCoinValue
     *            The type of coins of the coin box.
     */
    public CoinBoxFullException(String aMessage, 
            BigDecimal aCoinValue) {
        super(aMessage);
        allCoinBoxesFull = false;
        coinValue = aCoinValue;
    }

    /**
     * @return the coinValue
     */
    public BigDecimal getCoinValue() {
        return coinValue;
    }

    /**
     * @return the allCoinBoxesFull
     */
    public boolean isAllCoinBoxesFull() {
        return allCoinBoxesFull;
    }
}
