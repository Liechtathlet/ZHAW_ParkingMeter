package ch.zhaw.swengineering.event;

import java.util.EventObject;
import java.util.List;

import ch.zhaw.swengineering.model.CoinBoxLevel;

/**
 * @author Daniel Brun
 * 
 *         Event which is thrown if the user has defined the new levels for the
 *         coin boxes.
 */
public class CoinBoxLevelEnteredEvent extends EventObject {

    /**
     * GUID.
     */
    private static final long serialVersionUID = 7257553615509542001L;

    private List<CoinBoxLevel> coinBoxLevels;

    /**
     * Creates a new instance of this class.
     * 
     * @param source
     *            the source object.
     * @param someCoinBoxLevels
     *            The coin box levels.
     */
    public CoinBoxLevelEnteredEvent(final Object source,
            List<CoinBoxLevel> someCoinBoxLevels) {
        super(source);
        this.coinBoxLevels = someCoinBoxLevels;
    }

    /**
     * @return the coinBoxes
     */
    public List<CoinBoxLevel> getCoinBoxLevels() {
        return coinBoxLevels;
    }

}
