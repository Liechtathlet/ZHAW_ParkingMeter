package ch.zhaw.swengineering.slotmachine.exception;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author Daniel Brun
 * 
 *         Exception which is thrown when an invalid coin was inserted.
 */
public class InvalidCoinException extends Exception {

    /**
     * GUID.
     */
    private static final long serialVersionUID = 1296875112608348585L;

    private List<BigDecimal> validCoins;

    /**
     * Creates a new instance of this class.
     * 
     * @param aMessage
     *            the message.
     * @param someValidCoins
     *            the coins which are valid.
     */
    public InvalidCoinException(String aMessage, List<BigDecimal> someValidCoins) {
        super(aMessage);
        validCoins = someValidCoins;
    }

    /**
     * @return the validCoins
     */
    public List<BigDecimal> getValidCoins() {
        return validCoins;
    }

}
