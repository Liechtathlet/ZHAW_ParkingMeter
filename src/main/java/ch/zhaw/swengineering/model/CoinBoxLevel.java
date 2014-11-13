/**
 * 
 */
package ch.zhaw.swengineering.model;

import java.math.BigDecimal;

/**
 * @author Daniel Brun
 * 
 */
public class CoinBoxLevel {
    private final BigDecimal coinValue;
    private int currentCoinCount;
    private int maxCoinCount;

    /**
     * Creates a new instance of this class.
     * 
     * @param aCoinValue
     *            the value of the coin as a decimal.
     * @param aCurrentCoinCount
     *            the number of coins in the coin box.
     * @param aMaxCoinCount
     *            the max number of coins which can be held by the coin box.
     */
    public CoinBoxLevel(final BigDecimal aCoinValue, int aCurrentCoinCount,
            int aMaxCoinCount) {
        super();
        this.coinValue = aCoinValue;
        this.currentCoinCount = aCurrentCoinCount;
        this.maxCoinCount = aMaxCoinCount;
    }

    /**
     * @return the coinValue
     */
    public BigDecimal getCoinValue() {
        return coinValue;
    }

    /**
     * @return the currentCoinCount
     */
    public int getCurrentCoinCount() {
        return currentCoinCount;
    }

    /**
     * @return the maxCoinCount
     */
    public int getMaxCoinCount() {
        return maxCoinCount;
    }

    /**
     * @param currentCoinCount
     *            the currentCoinCount to set
     */
    public void setCurrentCoinCount(int currentCoinCount) {
        this.currentCoinCount = currentCoinCount;
    }
}
