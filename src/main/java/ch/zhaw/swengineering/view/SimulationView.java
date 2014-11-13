package ch.zhaw.swengineering.view;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import ch.zhaw.swengineering.event.ActionAbortedEvent;
import ch.zhaw.swengineering.event.CoinBoxLevelEnteredEvent;
import ch.zhaw.swengineering.event.MoneyInsertedEvent;
import ch.zhaw.swengineering.event.ParkingLotEnteredEvent;
import ch.zhaw.swengineering.event.ShutdownEvent;
import ch.zhaw.swengineering.event.ViewEventListener;
import ch.zhaw.swengineering.model.CoinBoxLevel;

/**
 * @author Daniel Brun Interface which defines the actions which the controller
 *         can invoke on the simulation view.
 */
/**
 * @author Daniel Brun
 *
 */
/**
 * @author Daniel Brun
 * 
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

    /**
     * Shuts down the view.
     */
    public abstract void shutdown();

    /**
     * Prompts the user to choose / enter a parking lot number.
     */
    public abstract void promptForParkingLotNumber();

    /**
     * Prompts the user to drop in some money.
     * 
     * @param aParkingLotNumber
     *            The number of the parking lot.
     */
    public abstract void promptForMoney(int aParkingLotNumber);

    /**
     * Prompts the user for the new coin box levels.
     * 
     * @param someCurrentCoinBoxLevels
     *            The current coin box levels.
     */
    public abstract void promptForNewCoinBoxLevels(
            List<CoinBoxLevel> someCurrentCoinBoxLevels);

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
     * Displays a message, that the system is shutting down.
     */
    public abstract void displayShutdownMessage();

    /**
     * Displays all available information. TODO: Anpassen.
     */
    public abstract void displayAllInformation();

    /**
     * Displays an error message, that not enough money was inserted.
     */
    public final void displayNotEnoughMoneyError() {
        print("view.booking.not.enough.money", false);
    }

    /**
     * Displays a message with the drawback.
     */
    public abstract void displayMessageForDrawback();

    /**
     * Displays a message with the error message, that the entered coin count
     * was too high.
     * 
     * @param aCoinValue The coin value of the coin box.
     */
    public final void displayCoinCountTooHigh(BigDecimal aCoinValue) {
        print("view.slot.machine.coin.box.level.too.high", false, aCoinValue);
    }

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

    /**
     * Notifies all attached listeners about the entered parking lot.
     * 
     * @param parkingLotNumber
     *            The parking lot number.
     */
    protected void notifyForParkingLotNumberEntered(final int parkingLotNumber) {
        ParkingLotEnteredEvent event = new ParkingLotEnteredEvent(this,
                parkingLotNumber);

        for (ViewEventListener listener : eventListeners) {
            listener.parkingLotEntered(event);
        }
    }

    /**
     * Notifies all attached listeners about the aborted action.
     */
    protected void notifyForActionAborted() {
        ActionAbortedEvent event = new ActionAbortedEvent(this);

        for (ViewEventListener listener : eventListeners) {
            listener.actionAborted(event);
        }
    }

    /**
     * Notifies all attached listeners about the entered money.
     * 
     * @param aParkingLotNumber
     *            The parking lot number.
     */
    protected void notifyForMoneyInserted(final int aParkingLotNumber) {
        MoneyInsertedEvent event = new MoneyInsertedEvent(this,
                aParkingLotNumber);

        for (ViewEventListener listener : eventListeners) {
            listener.moneyInserted(event);
        }
    }

    /**
     * Notifies all attached listeners about the entered coin box level.
     * 
     * @param someCoinBoxLevels
     *            The coin box levels.
     */
    protected void notifyForCoinBoxLevelEntered(
            final List<CoinBoxLevel> someCoinBoxLevels) {
        CoinBoxLevelEnteredEvent event = new CoinBoxLevelEnteredEvent(this,
                someCoinBoxLevels);

        for (ViewEventListener listener : eventListeners) {
            listener.coinBoxLevelEntered(event);
        }
    }

    /**
     * Prints a message to the output.
     * 
     * @param aKey
     *            the key of the message.
     * @param prompt
     *            True if the message is a prompt.
     * @param arguments
     *            The arguments for the message.
     */
    protected abstract void print(final String aKey, final boolean prompt,
            final Object... arguments);

}
