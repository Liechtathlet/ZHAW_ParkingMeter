package ch.zhaw.swengineering.model.persistence;

import java.math.BigDecimal;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

/**
 * @author Daniel Brun
 * 
 *         Contains the details of one coin box and the number of coins which
 *         are currently in the box.
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class CoinBox {
    private BigDecimal coinValue;
    private int currentCoinCount;
    private int maxCoinCount;

    /**
     * Creates a new instance of this class.
     */
    public CoinBox() {
        coinValue = null;
        currentCoinCount = 0;
        maxCoinCount = 0;
    }

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
    public CoinBox(BigDecimal aCoinValue, int aCurrentCoinCount,
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
     * @param coinValue
     *            the coinValue to set
     */
    public void setCoinValue(BigDecimal coinValue) {
        this.coinValue = coinValue;
    }

    /**
     * @param currentCoinCount
     *            the currentCoinCount to set
     */
    public void setCurrentCoinCount(int currentCoinCount) {
        this.currentCoinCount = currentCoinCount;
    }

}
