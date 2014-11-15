/**
 * 
 */
package ch.zhaw.swengineering.view;

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
}
