package ch.zhaw.swengineering.model.persistence;

import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

@XmlAccessorType(XmlAccessType.FIELD)
public class ParkingLot {

    @XmlAttribute
    private int number;

    @XmlAttribute
    private Date paidUntil;

    /**
     * Empty constructor for serialization
     */
    public ParkingLot() {
        // Empty constructor for serialization
    }

    /**
     * Creates a new instance of this class.
     * 
     * @param number
     *            The parking lot number.
     * @param paidUntil
     *            The date until which the parking lot is paid
     */
    public ParkingLot(int number, final Date paidUntil) {
        this.number = number;
        this.paidUntil = (Date) paidUntil.clone();
    }

    /**
     * @return the number
     */
    public int getNumber() {
        return number;
    }

    /**
     * @return the paidUntil
     */
    public Date getPaidUntil() {
        return (Date) paidUntil.clone();
    }

    /**
     * @param paidUntil
     *            the paidUntil to set
     */
    public void setPaidUntil(final Date paidUntil) {
        this.paidUntil = (Date) paidUntil.clone();
    }
}
