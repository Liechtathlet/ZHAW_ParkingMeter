/**
 * 
 */
package ch.zhaw.swengineering.view;

import java.util.Date;

import ch.zhaw.swengineering.event.ViewEventListener;

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
}
