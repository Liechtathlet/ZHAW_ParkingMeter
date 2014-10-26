package ch.zhaw.swengineering.controller;

import org.springframework.stereotype.Controller;

import ch.zhaw.swengineering.model.ParkingLot;

/**
 * @author Daniel Brun Interface for ParkingMeter Controllers Provides methods
 *         to access the parking lot information, book parking lots and so on.
 */
@Controller
public interface ParkingMeterController {

    /**
     * Gets the information about the given parking lot.
     * @param aNumber
     *            the parking lot number.
     * @return the information corresponding to the parking lot or null, if no
     *         matching parking lot could be found.
     */
    ParkingLot getParkingLot(int aNumber);
}
