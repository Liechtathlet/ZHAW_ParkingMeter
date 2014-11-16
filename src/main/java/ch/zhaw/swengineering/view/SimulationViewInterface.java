/**
 * 
 */
package ch.zhaw.swengineering.view;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import ch.zhaw.swengineering.event.ViewEventListener;
import ch.zhaw.swengineering.model.CoinBoxLevel;
import ch.zhaw.swengineering.model.persistence.ParkingLot;

/**
 * @author Daniel Brun
 * 
 *         Interface for the public methods of the simulation view.
 */
public interface SimulationViewInterface {

    /**
     * Starts the simulation view.
     */
    void startSimulationView();

    /**
     * Shuts down the view.
     */
    void shutdown();

    /**
     * Registers a view event listener.
     * 
     * @param aListener
     *            The listener to register.
     */
    void addViewEventListener(final ViewEventListener aListener);

    /**
     * Deregisters a view event listener.
     * 
     * @param aListener
     *            The listener to deregister.
     */
    void removeViewEventListener(final ViewEventListener aListener);

    /**
     * Prompts the user to choose / enter a parking lot number.
     */
    void promptForParkingLotNumber();

    /**
     * Prompts the user to drop in some money.
     * 
     * @param aParkingLotNumber
     *            The number of the parking lot.
     */
    void promptForMoney(Integer aParkingLotNumber);

    /**
     * Prompts the user for the new coin box levels.
     * 
     * @param someCurrentCoinBoxLevels
     *            The current coin box levels.
     */
    void promptForNewCoinBoxLevels(List<CoinBoxLevel> someCurrentCoinBoxLevels);

    /**
     * Displays the information about the current parking lot.
     * 
     * @param aParkingLotNumber
     *            The number of the parking lot.
     * @param aPaidParkingtime
     *            The time until the parking lot is / was paid.
     */
    void displayParkingLotNumberAndParkingTime(int aParkingLotNumber,
            Date aPaidParkingtime);

    /**
     * Displays a message, that the entered parking lot number was invalid.
     */
    void displayErrorParkingLotNumberInvalid();

    /**
     * Displays a message, that the system is shutting down.
     */
    void displayShutdownMessage();

    /**
     * Displays a message with the error message, that the entered coin count
     * was too high.
     * 
     * @param aCoinValue
     *            The coin value of the coin box.
     */
    void displayCoinCountTooHigh(BigDecimal aCoinValue);

    /**
     * Displays an error message, that not enough money was inserted.
     */
    void displayNotEnoughMoneyError();

    /**
     * Displays the information about the current booking parking lots.
     * 
     * @param parkingLots
     *            The List of all existing parking lots with all Information
     *            about the ParkingLot.
     */
    void displayBookedParkingLots(List<ParkingLot> parkingLots);

    /**
     * Displays all available information. TODO: Anpassen.
     */
    void displayAllInformation();

    /**
     * Displays a message with the drawback.
     */
    void displayMessageForDrawback();
}
