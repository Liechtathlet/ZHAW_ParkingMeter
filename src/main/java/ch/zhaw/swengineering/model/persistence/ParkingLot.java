package ch.zhaw.swengineering.model.persistence;

import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

@XmlAccessorType(XmlAccessType.FIELD)
public class ParkingLot {

	@XmlAttribute
	public int number;

	@XmlAttribute
	public Date paidUntil;

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
	public ParkingLot(int number, Date paidUntil) {
		this.number = number;
		this.paidUntil = paidUntil;
	}

	public int getParkingLotNumber() {
		return number;
	}

	public Date getParkingLotPaidUntil() {
		return paidUntil;
	}

}
