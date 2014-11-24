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
    private int bookingInMinutes;

    private Date paidFrom;

    private Date paidTill;
    private BigDecimal insertedMoney;
    private BigDecimal chargedMoney;

    private BigDecimal drawbackMoney;

    /**
     * Creates a new instance of this class.
     * 
     * @param aParkingLotNumber
     *            The parkingLotNumber which was chosen by the user.
     * @param aInsertedMoney
     *            The amount of money which was inserted by the user.
     */
    public ParkingLotBooking(int aParkingLotNumber, BigDecimal aInsertedMoney) {
        this.parkingLotNumber = aParkingLotNumber;
        this.insertedMoney = aInsertedMoney;
        this.notEnoughMoney = false;
    }

    /**
     * Creates a new instance of this class.
     * 
     * @param notEnoughMoney
     *            Set to true if not enough money was inserted for the booking.
     * @param parkingLotNumber
     *            The parking lot number.
     * @param paidFrom
     *            The date from when the parking lot is paid.
     * @param paidTill
     *            The date till when the parking lot is paid.
     * @param insertedMoney
     *            The inserted money.
     * @param chargedMoney
     *            The charged money.
     * @param drawbackMoney
     *            The drawback.
     */
    public ParkingLotBooking(boolean notEnoughMoney, int parkingLotNumber,
            Date paidFrom, Date paidTill, BigDecimal insertedMoney,
            BigDecimal chargedMoney, BigDecimal drawbackMoney) {
        super();
        this.notEnoughMoney = notEnoughMoney;
        this.parkingLotNumber = parkingLotNumber;
        this.paidFrom = paidFrom;
        this.paidTill = paidTill;
        this.insertedMoney = insertedMoney;
        this.chargedMoney = chargedMoney;
        this.drawbackMoney = drawbackMoney;
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

    /**
     * Stores the length of the booking in minutes.
     *
     * @param bookingInMinutes Minutes count the user has paid for.
     */
    public void setBookingInMinutes(int bookingInMinutes) {
        this.bookingInMinutes = bookingInMinutes;
    }

    /**
     * Gets the length of the booking in minutes.
     *
     * @return Minutes count the user has paid for.
     */
    public int getBookingInMinutes() {
        return bookingInMinutes;
    }
}
