package ch.zhaw.swengineering.controller;

import java.math.BigDecimal;

import org.springframework.stereotype.Controller;

import ch.zhaw.swengineering.model.ParkingLotBooking;
import ch.zhaw.swengineering.model.persistence.ParkingLot;
import ch.zhaw.swengineering.model.persistence.SecretActionEnum;

/**
 * @author Daniel Brun Interface for ParkingMeter Controllers Provides methods
 *         to access the parking lot information, book parking lots and so on.
 */
@Controller
public interface ParkingMeterController {

	/**
	 * Gets the information about the given parking lot.
	 * 
	 * @param aNumber
	 *            the parking lot number.
	 * @return the information corresponding to the parking lot or null, if no
	 *         matching parking lot could be found.
	 */
	ParkingLot getParkingLot(int aNumber);

	/**
	 * Gets the secret action to secret key.
	 *
	 * @param secretKey
	 *            Secret key.
	 * @return The secret action to the given secret key.
	 * @throws Exception
	 *             when no secret actions loaded.
	 * @throws java.lang.IllegalArgumentException
	 *             when no matching secret action can be found.
	 */
	SecretActionEnum getSecretAction(int secretKey) throws Exception;

	/**
	 * Call Method for given bookedParkingLots
	 */
	void callAllBookedParkingLots();

	/**
	 * Calculates the booking for the parking lot and the given amount of money.
	 * 
	 * @param aParkingLot
	 *            The parking lot.
	 * @param someInsertedMoney
	 *            The amount of money.
	 * @return The calculated parking lot booking.
	 */
	ParkingLotBooking calculateBookingForParkingLot(int aParkingLot,
			BigDecimal someInsertedMoney);

	/**
	 * Persists the given booking.
	 * 
	 * @param aBooking
	 *            the booking to persist.
	 */
	void persistBooking(ParkingLotBooking aBooking);
}
