package ch.zhaw.swengineering.view;

import java.math.BigDecimal;
import java.text.DecimalFormat;
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
import ch.zhaw.swengineering.event.NumberOfTransactionLogEntriesToShowEvent;
import ch.zhaw.swengineering.event.ParkingLotEnteredEvent;
import ch.zhaw.swengineering.event.ShutdownEvent;
import ch.zhaw.swengineering.event.ViewEventListener;
import ch.zhaw.swengineering.helper.DateHelper;
import ch.zhaw.swengineering.helper.MessageProvider;
import ch.zhaw.swengineering.helper.TransactionLogHandler;
import ch.zhaw.swengineering.model.CoinBoxLevel;
import ch.zhaw.swengineering.model.ParkingTimeTable;
import ch.zhaw.swengineering.model.ParkingTimeTableItem;
import ch.zhaw.swengineering.model.persistence.ParkingLot;
import ch.zhaw.swengineering.model.persistence.ParkingTimeDefinition;
import ch.zhaw.swengineering.model.persistence.TransactionLogEntry;
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
    protected static final DateFormatter dateFormatter = new DateFormatter(
            DATE_FORMAT);

    private Thread thread;

    protected List<ViewEventListener> eventListeners;

    private ViewStateEnum viewState;

    private boolean run;
    private final Lock runLock;

    private final Condition waitCondition;
    protected ViewDataStore dataStore;

    @Autowired
    protected IntelligentSlotMachineUserInteractionInterface slotMachine;

    @Autowired
    protected MessageProvider messageProvider;

    @Autowired
    protected TransactionLogHandler transactionLogHandler;

    /**
     * Creates a new instance of this class.
     */
    public SimulationView() {
        eventListeners = new ArrayList<>();
        runLock = new ReentrantLock();
        dataStore = new ViewDataStore();

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
                    switch (getViewState()) {
                    case ENTERING_PARKING_LOT:
                        executeActionsForStateEnteringParkingLotNumber();
                        break;
                    case DROPPING_IN_MONEY:
                        executeActionsForStateDroppingInMoney();
                        break;
                    case EXIT:
                        run = false;
                        break;
                    case ENTERING_COIN_BOX_COIN_LEVEL:
                        executeActionsForStateEnteringCoinBoxLevels();
                        break;
                    case ENTERING_TRANSACTION_LOG_ENTRIES_TO_SHOW:
                        executeActionsForStateEnteringTransactionLogEntriesToShow();
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

    @Override
    public void displayParkingLotNumberAndParkingTime(
            final int aParkingLotNumber, final Date aPaidParkingTime) {

        String formattedDate = "-";
        if (aPaidParkingTime != null) {
            formattedDate = dateFormatter.print(aPaidParkingTime,
                    LocaleContextHolder.getLocale());

            String timeDifference = DateHelper
                    .calculateFormattedTimeDifference(aPaidParkingTime,
                            new Date());

            if (timeDifference.startsWith("+")) {
                formattedDate += " (" + timeDifference + ")";
            }
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
        if (slotMachine.hasDrawback()) {

            String output = formatCoinMaptoString(slotMachine.getDrawback(),
                    false);

            print("view.slot.machine.drawback", ViewOutputMode.INFO, output);
        }
    }

    @Override
    public void displayBookedParkingLots(List<ParkingLot> parkingLots) {
        Date currentDate = new Date();

        print("view.booked.parkinglots.title.template",
                ViewOutputMode.LARGE_INFO,
                dateFormatter.print(currentDate,
                        LocaleContextHolder.getLocale()));

        displayBookedParkingLotsCore(parkingLots, currentDate);

    }

    private void displayBookedParkingLotsCore(List<ParkingLot> parkingLots,
            Date currentDate) {
        for (ParkingLot parkingLot : parkingLots) {
            String paidUntilString = "--";
            String timeDifferenceString = "--";
            String parkingTimeExceededString = messageProvider
                    .get("parking.time.exceeded");

            if (parkingLot.getPaidUntil() != null) {
                paidUntilString = dateFormatter.print(
                        parkingLot.getPaidUntil(),
                        LocaleContextHolder.getLocale());

                timeDifferenceString = DateHelper
                        .calculateFormattedTimeDifference(currentDate,
                                parkingLot.getPaidUntil());

                if (parkingLot.getPaidUntil().before(currentDate)) {
                    parkingTimeExceededString = messageProvider
                            .get("parking.time.exceeded");
                } else {
                    parkingTimeExceededString = "";
                }
            }

            print("view.booked.parkinglot", ViewOutputMode.LARGE_INFO,
                    parkingLot.getNumber(), paidUntilString,
                    timeDifferenceString, parkingTimeExceededString);
        }
    }

    public void displayParkingMeterInfo(Date currentDate) {
        print("view.parking.time.info",
                ViewOutputMode.LARGE_INFO,
                dateFormatter.print(currentDate,
                        LocaleContextHolder.getLocale()));
    }

    @Override
    public void displayParkingLotPayment(int aParkingLot,
            Map<BigDecimal, Integer> somePaymentMap) {
        print("view.info.inserted.coins", ViewOutputMode.INFO, aParkingLot,
                formatCoinMaptoString(somePaymentMap, true));
    }

    public void displayParkingTimeDefinitions(
            List<ParkingTimeDefinition> someParkingTimeDefinitions) {
        int i = 1;
        int lastMinuteCount = 0;
        BigDecimal lastPrice = BigDecimal.ZERO;
        for (ParkingTimeDefinition parkingTimeDef : someParkingTimeDefinitions) {
            for (int j = 0; j < parkingTimeDef.getCountOfSuccessivePeriods(); j++) {
                BigDecimal newPrice = lastPrice.add(parkingTimeDef
                        .getPricePerPeriod());

                int newMinuteCount = lastMinuteCount
                        + parkingTimeDef.getDurationOfPeriodInMinutes();

                print("view.all.information.parking.time",
                        ViewOutputMode.LARGE_INFO, i, formatPrice(lastPrice),
                        formatPrice(newPrice), newMinuteCount);

                lastPrice = newPrice;
                lastMinuteCount = newMinuteCount;
                i++;
            }
        }

    }

    @Override
    public void displayContentOfCoinBoxes(
            final List<CoinBoxLevel> someCoinBoxLevels) {
        BigDecimal totalCoinBoxes = BigDecimal.ZERO;
        totalCoinBoxes = totalCoinBoxes.setScale(2);

        for (CoinBoxLevel level : someCoinBoxLevels) {
            totalCoinBoxes = totalCoinBoxes.add(level.getCoinValue().multiply(
                    new BigDecimal(level.getCurrentCoinCount())));
        }

        print("view.info.coin.box.content.total", ViewOutputMode.LARGE_INFO,
                totalCoinBoxes);

        displayContentOfCoinBoxesCore(someCoinBoxLevels);
    }

    private void displayContentOfCoinBoxesCore(
            List<CoinBoxLevel> someCoinBoxLevels) {
        for (CoinBoxLevel level : someCoinBoxLevels) {
            print("view.info.coin.box.content",
                    ViewOutputMode.LARGE_INFO,
                    level.getCoinValue(),
                    level.getCurrentCoinCount(),
                    level.getCoinValue().multiply(
                            new BigDecimal(level.getCurrentCoinCount())));
        }
    }

    public void displayAllTransactionLogs() {
        List<TransactionLogEntry> entries = transactionLogHandler.getAll();
        for (TransactionLogEntry entry : entries) {
            print("view.transaction.log.entry", ViewOutputMode.LARGE_INFO,
                    entry.creationTime, entry.text);
        }
    }

    public void displayLast24HoursOfTransactionLog() {
        List<TransactionLogEntry> entries = transactionLogHandler
                .getLast24Hours();
        for (TransactionLogEntry entry : entries) {
            print("view.transaction.log.entry", ViewOutputMode.LARGE_INFO,
                    entry.creationTime, entry.text);
        }
    }

    @Override
    public void displayNTransactionLogEntries(
            int numberOfTransactionLogEntriesToShow) {
        List<TransactionLogEntry> entries = transactionLogHandler
                .get(numberOfTransactionLogEntriesToShow);
        for (TransactionLogEntry entry : entries) {
            print("view.transaction.log.entry", ViewOutputMode.LARGE_INFO,
                    entry.creationTime, entry.text);
        }
    }

    @Override
    public void displayAllInformation(List<CoinBoxLevel> coinBoxLevels,
            List<ParkingTimeDefinition> parkingTimeDefinitions,
            Date currentDate, List<ParkingLot> parkingLots,
            ParkingTimeTable parkingTimeTable) {
        String formattedDate = dateFormatter.print(currentDate,
                LocaleContextHolder.getLocale());

        // Title
        displayParkingMeterInfo(currentDate);

        // Coin boxes
        BigDecimal totalCoinBoxes = BigDecimal.ZERO;
        totalCoinBoxes = totalCoinBoxes.setScale(2);
        for (CoinBoxLevel level : coinBoxLevels) {
            BigDecimal currentCoinCount = new BigDecimal(
                    level.getCurrentCoinCount());
            BigDecimal coinBoxAmount = level.getCoinValue().multiply(
                    currentCoinCount);
            totalCoinBoxes = totalCoinBoxes.add(coinBoxAmount);
        }
        print("view.all.information.coin.box.content.total",
                ViewOutputMode.LARGE_INFO, totalCoinBoxes);
        displayContentOfCoinBoxesCore(coinBoxLevels);

        // Parking time definitions
        print("view.all.information.parking.time.def",
                ViewOutputMode.LARGE_INFO);
        displayParkingTimeDefinitions(parkingTimeDefinitions);

        // Parking lots
        print("view.all.information.parking.lots", ViewOutputMode.LARGE_INFO,
                formattedDate);
        displayBookedParkingLotsCore(parkingLots, currentDate);

        // Parking time calculation
        print("view.all.information.compute.parking.time",
                ViewOutputMode.LARGE_INFO);
        displayParkingTimeCalculationTable(parkingTimeTable);

        // Transaction log
        print("view.all.information.transaction.log",
                ViewOutputMode.LARGE_INFO, formattedDate);
        displayLast24HoursOfTransactionLog();
    }

    private void displayParkingTimeCalculationTable(ParkingTimeTable table) {

        int maxMinuteLength = Long.toString(
                table.getItems().get(table.getItems().size() - 1).getMinutes())
                .length();
        for (ParkingTimeTableItem item : table.getItems()) {
            print("view.parking.time.table", ViewOutputMode.LARGE_INFO,
                    formatPrice(item.getAmount()),
                    formatMinutes(item.getMinutes(), maxMinuteLength));
        }

        print("view.parking.maximal.time", ViewOutputMode.LARGE_INFO,
                table.getMaxBookingTime());

        print("view.parking.maximal.amount", ViewOutputMode.LARGE_INFO,
                formatPrice(table.getMaxPrice()));
    }

    /* ********** Methods for prompt, executions and notification ********** */

    @Override
    public void promptForParkingLotNumber() {
        setViewState(ViewStateEnum.ENTERING_PARKING_LOT);
    }

    @Override
    public void promptForNumberOfTransactionLogEntriesToShow() {
        setViewState(ViewStateEnum.ENTERING_TRANSACTION_LOG_ENTRIES_TO_SHOW);
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
     * Executes the action for the state 'EnteringTransactionLogEntriesToShow".
     */
    public final void executeActionsForStateEnteringTransactionLogEntriesToShow() {
        print("view.n.transaction.log.entries", ViewOutputMode.PROMPT);
        Integer numberOfTransactionLogEntriesToShow = readInteger();

        if (numberOfTransactionLogEntriesToShow != null) {
            notifyNumberOfTransactionLogEntriesToShowInserted(numberOfTransactionLogEntriesToShow);
        } else {
            print("view.invalid.number.of.transaction.log.entries.to.show",
                    ViewOutputMode.ERROR);
        }
    }

    /**
     * Executes the action for the state 'EnteringCoinBoxLevels'.
     */
    public final void executeActionsForStateEnteringCoinBoxLevels() {
        boolean failure = false;
        for (CoinBoxLevel cbl : dataStore.getCurrentCoinBoxLevels()) {

            BigDecimal total = cbl.getCoinValue().multiply(
                    new BigDecimal(cbl.getCurrentCoinCount()));
            print("view.info.coin.box.content", ViewOutputMode.INFO,
                    cbl.getCoinValue(), cbl.getCurrentCoinCount(), total);

            print("view.info.coin.box.content.new", ViewOutputMode.PROMPT,
                    cbl.getCoinValue());

            Integer coinCount = readInteger();

            if (coinCount != null) {
                try {
                    if (coinCount >= 1 && coinCount <= 100) {
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
     * Notifies attached listeners about the number of transaction log tnries to
     * show
     * 
     * @param number
     *            Number of entries to show.
     */
    protected void notifyNumberOfTransactionLogEntriesToShowInserted(
            final int number) {
        NumberOfTransactionLogEntriesToShowEvent event = new NumberOfTransactionLogEntriesToShowEvent(
                this, number);

        for (ViewEventListener listener : eventListeners) {
            listener.numberOfTransactionLogEntriesToShowEntered(event);
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
     * Formats a given price.
     * 
     * @param price
     *            Price to format.
     * @return Formatted price.
     */
    private String formatPrice(BigDecimal price) {
        price = price.setScale(2, BigDecimal.ROUND_DOWN);
        DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(2);
        df.setMinimumFractionDigits(2);
        df.setGroupingUsed(false);
        return df.format(price);
    }

    private String formatMinutes(long minutes, int maxMinuteLength) {

        String minutesString = Long.toString(minutes);
        for (int i = minutesString.length(); i < maxMinuteLength; i++) {
            minutesString = "0" + minutesString;
        }
        return minutesString;
    }

    /**
     * Formats the given coin map to a string.
     * 
     * @param someCoins
     *            the coins and their count to format.
     * @param isWithTotal
     *            true if the total should be appended at the front.
     * @return The formatted string.
     */
    protected String formatCoinMaptoString(Map<BigDecimal, Integer> someCoins,
            boolean isWithTotal) {
        StringBuilder sb = new StringBuilder();

        BigDecimal total = BigDecimal.ZERO;

        if (someCoins != null) {
            List<BigDecimal> keyList = new ArrayList<>(someCoins.keySet());
            for (int i = 0; i < keyList.size(); i++) {
                BigDecimal key = keyList.get(i);

                Integer count = someCoins.get(key);

                sb.append(count);
                sb.append(" x ");
                sb.append(key);

                if (i < (keyList.size() - 1)) {
                    sb.append(", ");
                }

                total = total
                        .add(key.multiply(new BigDecimal(count.intValue())));
            }

            if (isWithTotal) {
                sb.insert(0, total + " = ");
            }
        }
        return sb.toString();
    }

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
            LOG.warn("Synchronized signal for wait failed...", e);
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

    /* ********** Impl of Slot Machine View ********* */

    @Override
    public final void handleCoinBoxFullException(
            final CoinBoxFullException anException) {
        LOG.error("Received exception "
                + "from slot machine: coin box is full!", anException);

        if (anException.isAllCoinBoxesFull()) {
            print("view.slot.machine.coin.box.full", ViewOutputMode.ERROR,
                    anException.getCoinValue());

            print("view.slot.machine.drawback", ViewOutputMode.INFO, "1 x "
                    + anException.getCoinValue());
        } else {
            print("view.slot.machine.coin.box.single.full",
                    ViewOutputMode.ERROR, anException.getCoinValue());

            print("view.slot.machine.drawback", ViewOutputMode.INFO, "1 x "
                    + anException.getCoinValue());
        }
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
        slotMachine.rolebackTransaction();
        displayMessageForDrawback();
    }
}
