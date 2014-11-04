package ch.zhaw.swengineering.view;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import ch.zhaw.swengineering.event.ShutdownEvent;
import ch.zhaw.swengineering.event.ViewEventListener;

/**
 * @author Daniel Brun Interface which defines the actions which the controller
 *         can invoke on the simulation view.
 */
public abstract class SimulationView implements Runnable {

    private static final Logger LOG = LogManager
            .getLogger(SimulationView.class);

    private Thread thread;

    protected List<ViewEventListener> eventListeners;

    /**
     * Creates a new instance of this class.
     */
    public SimulationView() {
        eventListeners = new ArrayList<>();
    }

    /**
     * Starts the simulation gui.
     */
    public void startSimulationView() {
        LOG.info("Trying to start simulation view");

        if (thread == null) {
            // Create and start thread
            thread = new Thread(this);
            thread.start();
            
            LOG.info("Simulation started");
        } else {
            // TODO: Throw exception.
        }
    }

    /**
     * @see java.lang.Runnable#run()
     */
    public abstract void run();

    public abstract void displayDroppingInMoneyInvalid();

    /**
     * Shuts down the view.
     */
    public abstract void shutdown();
    
    /**
     * Prompts the user to choose / enter a parking lot number.
     */
    public abstract void promptForParkingLotNumber();

    /**
     * Prompts the user to choose / enter a secret code.
     */
    public abstract void promptForSecretCode();

    /**
     * Prompts the user to drop in some money.
     * 
     * @param aParkingLotNumber
     *            The number of the parking lot.
     */
    public abstract void promptForMoney(int aParkingLotNumber);

    /**
     * Displays the information about the current parking lot.
     * 
     * @param aParkingLotNumber
     *            The number of the parking lot.
     * @param aPaidParkingtime
     *            The time until the parking lot is / was paid.
     */
    public abstract void displayParkingLotNumberAndParkingTime(
            int aParkingLotNumber, Date aPaidParkingtime);

    /**
     * Displays a message, that the entered parking lot number was invalid.
     */
    public abstract void displayParkingLotNumberInvalid();

    /**
     * Displays a message, that the entered secret code was invalid.
     */
    public abstract void displaySecretCodeInvalid();
    
    /**
     * Displays a message, that the system is shutting down.
     */
    public abstract void displayShutdownMessage();
    
    /**
     * Registers a view event listener.
     * 
     * @param aListener
     *            The listener to register.
     */
    public void addViewEventListener(final ViewEventListener aListener) {
        if (aListener == null) {
            throw new IllegalArgumentException(
                    "The parameter 'aListener' must not be null!");
        }

        if (!eventListeners.contains(aListener)) {
            eventListeners.add(aListener);
        }
    }

    /**
     * Deregisters a view event listener.
     * 
     * @param aListener
     *            The listener to deregister.
     */
    public void removeViewEventListener(final ViewEventListener aListener) {
        if (aListener == null) {
            throw new IllegalArgumentException(
                    "The parameter 'aListener' must not be null!");
        }

        if (eventListeners.contains(aListener)) {
            eventListeners.remove(aListener);
        }
    }
    
    /**
     * Notifies all attached listeners about the shutdown request.
     */
    protected final void notifyForShutdownRequested() {
        ShutdownEvent event = new ShutdownEvent(this);

        for (ViewEventListener listener : eventListeners) {
            listener.shutdownRequested(event);
        }
    }

    public abstract void promptForMoneyEntered();
}
