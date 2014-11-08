package ch.zhaw.swengineering.model;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import ch.zhaw.swengineering.model.persistence.ParkingLot;

/**
 * @author Roland Hofer
 * 
 *         Class which get all actually booked ParkingLots with. the time when
 *         Booking is finished and the actually remaining time.
 */
public class AllParkingCharge {

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

		// Aktuelle Uhrzeit im Format hh:mm ermitteln
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
		ArrayList<ParkingLot> allParkingLots = new ArrayList<ParkingLot>(
				parkingLots);

		ArrayList<ParkingLot> bookedParkingLots = new ArrayList<ParkingLot>();

		ParkingLot parkingLot = null;
		for (ParkingLot pl : allParkingLots) {
			Date paidUntil = null;
			int minutesRemain = 0;
			// TODO Action
			// paidUntil = ParkingLot.getParkingLot(pl)
			// irgendwie Vergleich, ob 'bezahlt bis' > als sdf,
			// Parkplatz in ArrayList bookedParkingLots aufnehmen mit pnr,
			// bezbis und Berechnung der restlichen Zeit in m.
		}
	}

}
