/**
 * 
 */
package ch.zhaw.swengineering.view;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import ch.zhaw.swengineering.event.ViewEventListener;
import ch.zhaw.swengineering.model.CoinBoxLevel;
import ch.zhaw.swengineering.model.persistence.ParkingLot;
import ch.zhaw.swengineering.model.persistence.ParkingTimeDefinition;

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
     * Displays a message with the drawback.
     */
    void displayMessageForDrawback();

    /**
     * Displays the coin box levels.
     * 
     * @param someCoinBoxLevels
     *            the coin box levels to display.
     */
    void displayContentOfCoinBoxes(List<CoinBoxLevel> someCoinBoxLevels);

    /**
     * Displays a message with the inserted coin couns and the total.
     * 
     * @param aParkingLot the parking lot number.
     * @param somePaymentMap
     *            the payment map with the inserted count per coin.
     */
    void displayParkingLotPayment(int aParkingLot, Map<BigDecimal, Integer> somePaymentMap);

    /**
     * Increases the message buffer size temporarily to the given number. After
     * the number is reached, it will be reseted to one.
     * 
     * @param aCount
     *            the number to increase.
     */
    void increaseInfoBufferSizeTemporarily(int aCount);

    /**
     * Displays the transaction log.
     */
    void displayAllTransactionLogs();

    /**
     * Displays the last 24 hours of the transaction log.
     */
    void displayLast24HoursOfTransactionLog();

    /**
     * Shows a prompt asking for the number of transaction log entries to show.
     */
    void promptForNumberOfTransactionLogEntriesToShow();

    /**
     * Displays the last n transaction log entries.
     * 
     * @param n
     *            Number of transaction log entries to show.
     */
    void displayNTransactionLogEntries(int n);

    /**
     * Displays all information of the parking meter.
     *
     * @param coinBoxLevels Coin box values.
     * @param parkingTimeDefinitions Parking time defintions.
     * @param currentDate Current date.
     * @param parkingLots Parking lots.
     */
    void displayAllInformation(
            List<CoinBoxLevel> coinBoxLevels,
            List<ParkingTimeDefinition> parkingTimeDefinitions,
            Date currentDate,
            List<ParkingLot> parkingLots);
}
