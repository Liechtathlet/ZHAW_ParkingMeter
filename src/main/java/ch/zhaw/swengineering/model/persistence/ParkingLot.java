package ch.zhaw.swengineering.model.persistence;

import java.text.ParseException;
import java.text.SimpleDateFormat;
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

	private long remainingMillisec;

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

	/**
	 * @return the number
	 */
	public int getNumber() {
		return number;
	}

	/**
	 * @param number
	 *            the number to set
	 */
	public void setNumber(int number) {
		this.number = number;
	}

	/**
	 * @return the paidUntil
	 */
	public Date getPaidUntil() {
		return paidUntil;
	}

	/**
	 * @param paidUntil
	 *            the paidUntil to set
	 */
	public void setPaidUntil(Date paidUntil) {
		this.paidUntil = paidUntil;
	}

	public long getRemainingTimeInMillisec() {
		Date now = new Date();
		Date init = null;
		String initDate = "01.11.2014";
		SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
		try {
			init = sdf.parse(initDate);
		} catch (ParseException e) {
			// ToDo LOG.
		}
		if (paidUntil == null) {
			setPaidUntil(init);
		}
		this.remainingMillisec = (paidUntil.getTime() - now.getTime());
		return remainingMillisec;
	}

}
