package ch.zhaw.swengineering.model;

import java.util.ArrayList;
import java.util.Date;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import ch.zhaw.swengineering.model.persistence.ParkingLot;

/**
 * @author Roland Hofer
 * 
 *         Class which get all actually booked ParkingLots with. the time when
 *         Booking is finished and the actually remaining time.
 */
public class AllParkingCharge {
	/**
	 * The Logger.
	 */
	private static final Logger LOG = LogManager
			.getLogger(AllParkingCharge.class);

	private int parkingLotNumber;

	/**
	 * Creates a new instance of this class.
	 * 
	 * @param aParkingLotNumber
	 *            The parkingLotNumber which was choosen by the user.
	 * @param aInsertedMoney
	 *            The amount of money which was inserted by the user.
	 */
	public AllParkingCharge(ArrayList<ParkingLot> parkingLots) {

		ArrayList<ParkingLot> allParkingLots = new ArrayList<ParkingLot>(
				parkingLots);
		getAllBookedParkingLots(allParkingLots);
	}

	private ArrayList<Object> getAllBookedParkingLots(
			ArrayList<ParkingLot> allParkingLots) {

		Object[] arrBookedParkingLot = new Object[3];
		arrBookedParkingLot[0] = new ParkingLot();
		arrBookedParkingLot[1] = new Date();
		arrBookedParkingLot[2] = new long[10];

		ArrayList<Object> bookedParkingLots = new ArrayList<Object>();

		for (ParkingLot pl : allParkingLots) {
			Date paidUntil = null;
			long minutesRemain = 0;

			paidUntil = pl.getParkingLotPaidUntil();
			minutesRemain = new CalculateRemainingTime(paidUntil)
					.getDifferenceMinutes();

			arrBookedParkingLot[0] = pl;
			arrBookedParkingLot[1] = paidUntil;
			arrBookedParkingLot[2] = minutesRemain;

			LOG.info("AllParkingCharge - Adding to ArrayList:");
			LOG.info(pl + ", " + paidUntil + ", " + minutesRemain);

			bookedParkingLots.add(arrBookedParkingLot);

		}
		return bookedParkingLots;

	}

}
