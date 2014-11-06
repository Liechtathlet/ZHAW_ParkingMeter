package ch.zhaw.swengineering.model;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author Daniel Brun
 * 
 *         Class which holds all values which are calculated in a parking lot
 *         booking process or are needed for the booking process.
 */
public class ParkingLotBooking {

    private boolean notEnoughMoney;

    private int parkingLotNumber;

    private Date paidFrom;
    private Date paidTill;

    private BigDecimal insertedMoney;
    private BigDecimal chargedMoney;
    private BigDecimal drawbackMoney;

    /**
     * Creates a new instance of this class.
     * 
     * @param aParkingLotNumber
     *            The parkingLotNumber which was choosen by the user.
     * @param aInsertedMoney
     *            The amount of money which was inserted by the user.
     */
    public ParkingLotBooking(int aParkingLotNumber, BigDecimal aInsertedMoney) {
        this.parkingLotNumber = aParkingLotNumber;
        this.insertedMoney = aInsertedMoney;
        this.notEnoughMoney = false;
    }

    /**
     * @return the paidFrom
     */
    public Date getPaidFrom() {
        return paidFrom;
    }

    /**
     * @param paidFrom
     *            the paidFrom to set
     */
    public void setPaidFrom(Date paidFrom) {
        this.paidFrom = paidFrom;
    }

    /**
     * @return the paidTill
     */
    public Date getPaidTill() {
        return paidTill;
    }

    /**
     * @param paidTill
     *            the paidTill to set
     */
    public void setPaidTill(Date paidTill) {
        this.paidTill = paidTill;
    }

    /**
     * @return the chargedMoney
     */
    public BigDecimal getChargedMoney() {
        return chargedMoney;
    }

    /**
     * @param chargedMoney
     *            the chargedMoney to set
     */
    public void setChargedMoney(BigDecimal chargedMoney) {
        this.chargedMoney = chargedMoney;
    }

    /**
     * @return the drawbackMoney
     */
    public BigDecimal getDrawbackMoney() {
        return drawbackMoney;
    }

    /**
     * @param drawbackMoney
     *            the drawbackMoney to set
     */
    public void setDrawbackMoney(BigDecimal drawbackMoney) {
        this.drawbackMoney = drawbackMoney;
    }

    /**
     * @return the parkingLotNumber
     */
    public int getParkingLotNumber() {
        return parkingLotNumber;
    }

    /**
     * @return the insertedMoney
     */
    public BigDecimal getInsertedMoney() {
        return insertedMoney;
    }

    /**
     * @return the notEnoughMoney
     */
    public boolean isNotEnoughMoney() {
        return notEnoughMoney;
    }

    /**
     * @param notEnoughMoney
     *            the notEnoughMoney to set
     */
    public void setNotEnoughMoney(boolean notEnoughMoney) {
        this.notEnoughMoney = notEnoughMoney;
    }
}
