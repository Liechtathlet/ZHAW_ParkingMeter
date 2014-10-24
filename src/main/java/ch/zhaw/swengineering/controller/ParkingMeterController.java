package ch.zhaw.swengineering.controller;

import org.springframework.stereotype.Controller;

/**
 * @author Daniel Brun
 * Interface for ParkingMeter Controllers Provides methods to access the
 * parking lot information, book parking lots and so on.
 */
@Controller
public interface ParkingMeterController {

	/**
	 * Checks the given number against the list of parking lots.
	 * 
	 * @param aNumber the number to check.
	 * @return true if the number is a parkingLot, false otherwise.
	 */
	boolean isParkingLot(int aNumber);
}
