package ch.zhaw.swengineering.view;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.format.datetime.DateFormatter;

import ch.zhaw.swengineering.event.ActionAbortedEvent;
import ch.zhaw.swengineering.event.CoinBoxLevelEnteredEvent;
import ch.zhaw.swengineering.event.MoneyInsertedEvent;
import ch.zhaw.swengineering.event.ParkingLotEnteredEvent;
import ch.zhaw.swengineering.event.ShutdownEvent;
import ch.zhaw.swengineering.event.ViewEventListener;
import ch.zhaw.swengineering.model.CoinBoxLevel;
import ch.zhaw.swengineering.slotmachine.controller.IntelligentSlotMachineUserInteractionInterface;
import ch.zhaw.swengineering.slotmachine.exception.CoinBoxFullException;
import ch.zhaw.swengineering.slotmachine.exception.InvalidCoinException;
import ch.zhaw.swengineering.slotmachine.exception.NoTransactionException;
import ch.zhaw.swengineering.view.data.ViewDataStore;
import ch.zhaw.swengineering.view.helper.ViewOutputMode;

/**
 * @author Daniel Brun Interface which defines the actions which the controller
 *         can invoke on the simulation view.
 */
public abstract class SimulationView implements Runnable,
        SimulationViewInterface, IntelligentSlotMachineViewInterface {

    /**
     * Logger
     */
    private static final Logger LOG = LogManager
            .getLogger(SimulationView.class);

    /**
     * Date-Format.
     */
    private static final String DATE_FORMAT = "dd.MM.YYYY HH:mm";

    private Thread thread;

    protected List<ViewEventListener> eventListeners;

    private ViewStateEnum viewState;

    private boolean run;

    private final Lock runLock;
    private final Condition waitCondition;

    protected ViewDataStore dataStore;
    protected DateFormatter dateFormatter;

    @Autowired
    protected IntelligentSlotMachineUserInteractionInterface slotMachine;

    /**
     * Creates a new instance of this class.
     */
    public SimulationView() {
        eventListeners = new ArrayList<>();
        runLock = new ReentrantLock();
        dataStore = new ViewDataStore();
        dateFormatter = new DateFormatter(DATE_FORMAT);

        waitCondition = runLock.newCondition();

        viewState = ViewStateEnum.INIT;
    }

    /**
     * @see java.lang.Runnable#run()
     */
    @Override
    public final void run() {
        if (!run) {
            LOG.info("Running console simulation");
            run = true;

            // initialize
            init();

            while (run) {
                try {
                    runLock.lock();

                    switch (viewState) {
                    case ENTERING_PARKING_LOT:
                        executeActionsForStateEnteringParkingLotNumber();
                        break;
                    case DROPPING_IN_MONEY:
                        executeActionsForStateDroppingInMoney();
                        break;
                    case DISPLAY_ALL_INFORMATION:
                        executeActionsForStateViewingAllInformation();
                        break;
                    case DISPLAY_BOOKED_PARKINGLOTS:
                        executeActionsForStateDisplayBookedParkingLots();
                        break;
                    case EXIT:
                        run = false;
                        break;
                    case ENTERING_COIN_BOX_COIN_LEVEL:
                        executeActionsForStateEnteringCoinBoxLevels();
                        break;
                    case INIT:
                    default:
                        try {
                            waitCondition.await();
                        } catch (InterruptedException e) {
                            LOG.error("Wait condition failed", e);
                        }
                        break;
                    }
                } finally {
                    runLock.unlock();
                }
            }
        }
    }

    /* ********** External methods - Impl of Interface ********** */

    /*
     * (non-Javadoc)
     * 
     * @see
     * ch.zhaw.swengineering.view.SimulationViewInterface#startSimulationView()
     */
    @Override
    public void startSimulationView() {
        LOG.info("Trying to start simulation view");

        if (thread == null) {
            // Create and start thread
            thread = new Thread(this);
            thread.start();

            LOG.info("Simulation started");
        } else {
            LOG.error("The thread was already started!");
            throw new RuntimeException("The thread was already started!");
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see ch.zhaw.swengineering.view.SimulationViewInterface#shutdown()
     */
    @Override
    public void shutdown() {
        LOG.info("Shutting down the view...");
        run = false;
        setViewState(ViewStateEnum.EXIT);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * ch.zhaw.swengineering.view.SimulationViewInterface#addViewEventListener
     * (ch.zhaw.swengineering.event.ViewEventListener)
     */
    @Override
    public void addViewEventListener(final ViewEventListener aListener) {
        if (aListener == null) {
            throw new IllegalArgumentException(
                    "The parameter 'aListener' must not be null!");
        }

        if (!eventListeners.contains(aListener)) {
            eventListeners.add(aListener);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see ch.zhaw.swengineering.view.SimulationViewInterface#
     * removeViewEventListener(ch.zhaw.swengineering.event.ViewEventListener)
     */
    @Override
    public void removeViewEventListener(final ViewEventListener aListener) {
        if (aListener == null) {
            throw new IllegalArgumentException(
                    "The parameter 'aListener' must not be null!");
        }

        if (eventListeners.contains(aListener)) {
            eventListeners.remove(aListener);
        }
    }

    @Override
    public void displayParkingLotNumberAndParkingTime(
            final int aParkingLotNumber, final Date aPaidParkingTime) {

        String formattedDate = "-";
        if (aPaidParkingTime != null) {
            formattedDate = dateFormatter.print(aPaidParkingTime,
                    LocaleContextHolder.getLocale());
        }

        print("view.info.parkingTime", ViewOutputMode.INFO, aParkingLotNumber,
                formattedDate);
    }

    @Override
    public void displayErrorParkingLotNumberInvalid() {
        print("view.enter.parkinglotnumber.invalid", ViewOutputMode.ERROR);
    }

    @Override
    public void displayShutdownMessage() {
        print("application.bye", ViewOutputMode.INFO);
    }

    @Override
    public void displayCoinCountTooHigh(BigDecimal aCoinValue) {
        print("view.slot.machine.coin.box.level.too.high",
                ViewOutputMode.ERROR, aCoinValue);
    }

    @Override
    public void displayNotEnoughMoneyError() {
        print("view.booking.not.enough.money", ViewOutputMode.ERROR);
    }

    @Override
    public void displayMessageForDrawback() {
        StringBuilder sb = new StringBuilder();

        Map<BigDecimal, Integer> drawbackMap = slotMachine.getDrawback();

        List<BigDecimal> keyList = new ArrayList<>(drawbackMap.keySet());
        for (int i = 0; i < keyList.size(); i++) {
            BigDecimal key = keyList.get(i);

            sb.append(drawbackMap.get(key));
            sb.append(" x ");
            sb.append(key);

            if (i < (keyList.size() - 1)) {
                sb.append(", ");
            }
        }

        print("view.slot.machine.drawback", ViewOutputMode.INFO, sb.toString());
    }

    /* ********** Methods for prompt, executions and notification ********** */

    @Override
    public void promptForParkingLotNumber() {
        setViewState(ViewStateEnum.ENTERING_PARKING_LOT);
    }

    /**
     * Executes the action for the state 'EnteringParkingLotNumber'.
     */
    public final void executeActionsForStateEnteringParkingLotNumber() {
        print("view.enter.parkinglotnumber", ViewOutputMode.PROMPT);
        Integer enteredInteger = readInteger();

        if (enteredInteger != null) {
            setViewState(ViewStateEnum.INIT);
            notifyForParkingLotNumberEntered(enteredInteger);
        }
    }

    /**
     * Notifies all attached listeners about the entered parking lot.
     * 
     * @param parkingLotNumber
     *            The parking lot number.
     */
    protected final void notifyForParkingLotNumberEntered(
            final int parkingLotNumber) {
        ParkingLotEnteredEvent event = new ParkingLotEnteredEvent(this,
                parkingLotNumber);

        for (ViewEventListener listener : eventListeners) {
            listener.parkingLotEntered(event);
        }
    }

    @Override
    public void promptForMoney(final Integer aParkingLotNumber) {
        setViewState(ViewStateEnum.DROPPING_IN_MONEY);

        dataStore.setParkingLotNumber(aParkingLotNumber);
    }

    @Override
    public void promptForNewCoinBoxLevels(
            final List<CoinBoxLevel> someCurrentCoinBoxLevels) {
        dataStore.setCurrentCoinBoxLevels(someCurrentCoinBoxLevels);
        setViewState(ViewStateEnum.ENTERING_COIN_BOX_COIN_LEVEL);
    }

    /**
     * Executes the action for the state 'EnteringCoinBoxLevels'.
     */
    public final void executeActionsForStateEnteringCoinBoxLevels() {
        boolean failure = false;
        for (CoinBoxLevel cbl : dataStore.getCurrentCoinBoxLevels()) {

            // TODO: Not optimal -> creating three displays in gui -> three
            // output modes -> prompt, info, error
            // TODO: Evtl. auslagern.
            BigDecimal total = cbl.getCoinValue().multiply(
                    new BigDecimal(cbl.getCurrentCoinCount()));
            print("view.info.coin.box.content", ViewOutputMode.INFO,
                    cbl.getCoinValue(), cbl.getCurrentCoinCount(), total);

            print("view.info.coin.box.content.new", ViewOutputMode.PROMPT,
                    cbl.getCoinValue());

            Integer coinCount = readInteger();

            if (coinCount != null) {
                try {
                    if (coinCount.intValue() >= 1
                            && coinCount.intValue() <= 100) {
                        cbl.setCurrentCoinCount(coinCount);
                    } else {
                        LOG.info("Coin count for coin box not in range!");
                        failure = true;
                        print("view.info.coin.box.content.limit",
                                ViewOutputMode.ERROR);

                        // Early return for quick failure
                        return;
                    }
                } catch (NumberFormatException e) {
                    LOG.info("Coin count is not a valid number!");
                    failure = true;
                    print("view.slot.machine.format.invalid",
                            ViewOutputMode.ERROR);

                    // Early return for quick failure
                    return;
                }
            } else {
                failure = true;
                return;
            }
        }

        if (!failure) {
            setViewState(ViewStateEnum.INIT);
            notifyForCoinBoxLevelEntered(dataStore.getCurrentCoinBoxLevels());
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

    /* ********** Internal methods ********** */
    /**
     * @return the view state.
     */
    public final synchronized ViewStateEnum getViewState() {
        return viewState;
    }

    /**
     * Sets the view state. This method is synchronized to ensure integrity
     * trough the different threads.
     * 
     * @param aState
     *            the view state to set.
     */
    protected final synchronized void setViewState(final ViewStateEnum aState) {
        viewState = aState;
        try {
            waitCondition.signal();
        } catch (Exception e) {
            // Nothing to do here...
        }
    }

    /* ********** Definition for implementation classes ********** */

    /**
     * Reads an integer from the input.
     * 
     * @return an integer or null if another action was executed.
     */
    public abstract Integer readInteger();

    /**
     * Prints a message to the output.
     * 
     * @param aKey
     *            the key of the message.
     * @param aMode
     *            The mode to display the message.
     * @param arguments
     *            The arguments for the message.
     */
    protected abstract void print(final String aKey,
            final ViewOutputMode aMode, final Object... arguments);

    /**
     * Initializes the implementation class.
     */
    protected abstract void init();

    /**
     * Executes the action for the state 'DropppingInMoney'.
     */
    protected abstract void executeActionsForStateDroppingInMoney();

    /**
     * Executes the action for the state 'ViewingAllInformation'.
     */
    protected abstract void executeActionsForStateViewingAllInformation();

    /**
     * Executes the action for the state 'DisplayingBookedParkingLots'.
     */
    protected abstract void executeActionsForStateDisplayBookedParkingLots();

    /* ********** Impl of Slot Machine View ********* */

    @Override
    public final void handleCoinBoxFullException(
            final CoinBoxFullException anException) {
        LOG.error("Received exception "
                + "from slot machine: coin box is full!", anException);

        if (anException.isAllCoinBoxesFull()) {
            print("view.slot.machine.coin.box.full", ViewOutputMode.ERROR,
                    anException.getCoinValue());
        } else {
            print("view.slot.machine.coin.box.single.full",
                    ViewOutputMode.ERROR, anException.getCoinValue());
        }

        rolebackTransaction();
    }

    @Override
    public final void handleInvalidCoinException(
            final InvalidCoinException anException) {
        StringBuilder coinString = new StringBuilder();

        for (BigDecimal validCoin : anException.getValidCoins()) {
            coinString.append(validCoin);
            coinString.append(", ");
        }

        LOG.error("Received exception " + "from slot machine: invalid coin!",
                anException);
        print("view.slot.machine.coin.invalid", ViewOutputMode.ERROR,
                coinString);

        rolebackTransaction();
    }

    @Override
    public final void handleNoTransactionException(
            final NoTransactionException anException) {
        LOG.error("Received exception " + "from slot machine: no transaction!",
                anException);

        rolebackTransaction();
    }

    @Override
    public final void handleNumberFormatException(
            final NumberFormatException anException) {
        print("view.slot.machine.format.invalid", ViewOutputMode.ERROR);
    }

    @Override
    public final void rolebackTransaction() {
        Map<BigDecimal, Integer> drawbackMap = slotMachine
                .rolebackTransaction();

        boolean displayMsg = false;

        for (Integer count : drawbackMap.values()) {
            if (count.intValue() > 0) {
                displayMsg = true;
                break;
            }
        }

        if (displayMsg) {
            displayMessageForDrawback();
        }
    }
}
