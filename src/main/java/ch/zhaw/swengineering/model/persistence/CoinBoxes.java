package ch.zhaw.swengineering.model.persistence;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Daniel Brun
 * 
 *         Contains all available coin boxes.
 */
@XmlRootElement(name = "coinBoxes")
@XmlAccessorType(XmlAccessType.FIELD)
public class CoinBoxes {
    @XmlElement(name = "coinBox")
    private List<CoinBox> coinBoxes;

    /**
     * Creates an new instance of this class.
     */
    public CoinBoxes() {
        coinBoxes = new ArrayList<>();
    }

    /**
     * Gets a specific coin box.
     * @param aValue
     *            the coin value.
     * @return the coin box or null if no corresponding coin box could be found.
     */
    public final CoinBox getCoinBox(BigDecimal aValue) {
        CoinBox box = null;

        for (CoinBox cb : coinBoxes) {
            if (cb.getCoinValue().equals(aValue)) {
                box = cb;
                break;
            }
        }

        return box;
    }

    /**
     * @return the coin boxes
     */
    public final List<CoinBox> getCoinBoxes() {
        return coinBoxes;
    }
}
