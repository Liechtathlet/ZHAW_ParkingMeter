package ch.zhaw.swengineering.controller;

import ch.zhaw.swengineering.business.ParkingMeterImpl;
import ch.zhaw.swengineering.event.*;
import ch.zhaw.swengineering.helper.TransactionLogHandler;
import ch.zhaw.swengineering.model.CoinBoxLevel;
import ch.zhaw.swengineering.model.ParkingLotBooking;
import ch.zhaw.swengineering.model.ParkingTimeTable;
import ch.zhaw.swengineering.model.persistence.ParkingLot;
import ch.zhaw.swengineering.model.persistence.SecretActionEnum;
import ch.zhaw.swengineering.setup.ParkingMeterRunner;
import ch.zhaw.swengineering.slotmachine.controller.IntelligentSlotMachineBackendInteractionInterface;
import ch.zhaw.swengineering.slotmachine.exception.CoinBoxFullException;
import ch.zhaw.swengineering.view.SimulationView;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

/**
 * @author Daniel Brun
 * 
 *         Tests the Class: 'ViewControllerImpl'.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = ParkingMeterRunner.class, loader = AnnotationConfigContextLoader.class)
public class ViewControllerImplTest {

    @Mock
    private SimulationView view;

    @Mock
    private ConfigurableApplicationContext appContext;

    @InjectMocks
    private ViewControllerImpl controller;

    @Mock
    private ParkingMeterImpl parkingMeter;

    @Mock
    private IntelligentSlotMachineBackendInteractionInterface slotMachine;

    @Mock
    private TransactionLogHandler transactionLog;

    /**
     * Set up a test case.
     */
    @Before
    public final void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    /**
     * Method-Under-Test: start(...).
     * 
     * Expectation: All methods are invoked correctly.
     */
    @Test
    public final void testStartOnCorrectMethodInvocation() {

        // Start
        controller.start();

        // Assert
        verify(view).addViewEventListener(controller);
        verify(view).startSimulationView();
        verify(view).promptForParkingLotNumber();
    }

    /**
     * Method-Under-Test: parkingLotEntered(...).
     * 
     * Scenario: A valid parking lot number is entered.
     * 
     * Expectation: All methods are invoked correctly.
     */
    @Test
    public final void testParkingLotEnteredEventWithValidParkingLotNumber() {
        int parkingLotNumber = 5;
        Date paidUntil = new Date();

        ParkingLot parkingLot = new ParkingLot(parkingLotNumber, paidUntil);
        ParkingLotEnteredEvent plEnteredEvent = new ParkingLotEnteredEvent(
                view, parkingLotNumber);

        // Mock
        when(parkingMeter.getParkingLot(parkingLotNumber)).thenReturn(
                parkingLot);

        // Run
        controller.parkingLotEntered(plEnteredEvent);

        // Assert positive
        verify(parkingMeter).getParkingLot(parkingLotNumber);
        verify(view).displayParkingLotNumberAndParkingTime(parkingLotNumber,
                paidUntil);
        verify(view).promptForMoney(parkingLotNumber);

        // Assert negative
        verify(view, times(0)).displayErrorParkingLotNumberInvalid();
        verify(view, times(0)).promptForParkingLotNumber();
    }

    /**
     * Method-Under-Test: parkingLotEntered(...).
     * 
     * Scenario: An invalid parking lot number is entered.
     * 
     * Expectation: All methods are invoked correctly.
     */
    @Test
    public final void testParkingLotEnteredEventWithInvalidParkingLotNumber() {
        int parkingLotNumber = 5;

        ParkingLotEnteredEvent plEnteredEvent = new ParkingLotEnteredEvent(
                view, parkingLotNumber);

        // Mock
        when(parkingMeter.getParkingLot(parkingLotNumber)).thenReturn(null);

        // Run
        controller.parkingLotEntered(plEnteredEvent);

        // Assert positive
        verify(parkingMeter).getParkingLot(parkingLotNumber);
        verify(view).displayErrorParkingLotNumberInvalid();
        verify(view).promptForParkingLotNumber();

        // Assert negative
        verify(view, times(0)).displayParkingLotNumberAndParkingTime(
                eq(parkingLotNumber), any(Date.class));
        verify(view, times(0)).promptForMoney(parkingLotNumber);
    }

    /**
     * Method-Under-Test: parkingLotEntered(...).
     * 
     * Scenario: Valid secret code for viewing all information has been entered.
     * 
     * Expectation: All methods are invoked correctly.
     */
    @Test
    public final void testParkingLotEnteredEventWithValidViewAllInformationSecretNumber()
            throws Exception {
        int parkingLotNumber = 123456;

        // Mock
        ParkingLotEnteredEvent plEnteredEvent = new ParkingLotEnteredEvent(
                view, parkingLotNumber);
        when(parkingMeter.getSecretAction(parkingLotNumber)).thenReturn(
                SecretActionEnum.VIEW_ALL_INFORMATION);

        // Run
        controller.parkingLotEntered(plEnteredEvent);

        // Assert positive
        verify(parkingMeter).getSecretAction(parkingLotNumber);
        verify(view).displayAllInformation(
                any(ArrayList.class),
                any(ArrayList.class),
                any(Date.class),
                any(ArrayList.class),
                any(ParkingTimeTable.class));
    }

    /**
     * Method-Under-Test: parkingLotEntered(...).
     *
     * Scenario: Valid secret code for viewing all transaction logs has been entered.
     *
     * Expectation: All methods are invoked correctly.
     */
    @Test
    public final void testParkingLotEnteredEventWithValidViewAllTransactionLogSecretNumber()
            throws Exception {
        int parkingLotNumber = 123456;

        // Mock
        ParkingLotEnteredEvent plEnteredEvent = new ParkingLotEnteredEvent(
                view, parkingLotNumber);
        when(parkingMeter.getSecretAction(parkingLotNumber)).thenReturn(
                SecretActionEnum.VIEW_ALL_TRANSACTION_LOGS);

        // Run
        controller.parkingLotEntered(plEnteredEvent);

        // Assert positive
        verify(parkingMeter).getSecretAction(parkingLotNumber);
        verify(view).displayAllTransactionLogs();
        verify(view).promptForParkingLotNumber();
    }

    /**
     * Method-Under-Test: parkingLotEntered(...).
     *
     * Scenario: Valid secret code for viewing transaction logs of the last 24 hours has been entered.
     *
     * Expectation: All methods are invoked correctly.
     */
    @Test
    public final void testParkingLotEnteredEventWithValidViewLast24HoursOfTransactionLogSecretNumber()
            throws Exception {
        int parkingLotNumber = 123456;

        // Mock
        ParkingLotEnteredEvent plEnteredEvent = new ParkingLotEnteredEvent(
                view, parkingLotNumber);
        when(parkingMeter.getSecretAction(parkingLotNumber)).thenReturn(
                SecretActionEnum.VIEW_LAST_24_HOURS_OF_TRANSACTION_LOG);

        // Run
        controller.parkingLotEntered(plEnteredEvent);

        // Assert positive
        verify(parkingMeter).getSecretAction(parkingLotNumber);
        verify(view).displayLast24HoursOfTransactionLog();
        verify(view).promptForParkingLotNumber();
    }

    /**
     * Method-Under-Test: parkingLotEntered(...).
     *
     * Scenario: Valid secret code for viewing n transaction logs entries has been entered.
     *
     * Expectation: All methods are invoked correctly.
     */
    @Test
    public final void testParkingLotEnteredEventWithValidViewNTransactionLogEntriesSecretNumber()
            throws Exception {
        int parkingLotNumber = 123456;

        // Mock
        ParkingLotEnteredEvent plEnteredEvent = new ParkingLotEnteredEvent(
                view, parkingLotNumber);
        when(parkingMeter.getSecretAction(parkingLotNumber)).thenReturn(
                SecretActionEnum.VIEW_N_TRANSACTION_LOG_ENTRIES);

        // Run
        controller.parkingLotEntered(plEnteredEvent);

        // Assert positive
        verify(parkingMeter).getSecretAction(parkingLotNumber);
        verify(view).promptForNumberOfTransactionLogEntriesToShow();
    }

    /**
     * Method-Under-Test: numberOfTransactionLogEntriesToShowEntered(...).
     *
     * Scenario: Valid amount of transaction log entries to show has been entered..
     *
     * Expectation: All methods are invoked correctly.
     */
    @Test
    public final void testNumberOfTransactionLogEntriesToShowEnteredCallsCorrectViewMethod()
            throws Exception {
        int n = 2;
        NumberOfTransactionLogEntriesToShowEvent event = new NumberOfTransactionLogEntriesToShowEvent(
                view, n);

        // Run
        controller.numberOfTransactionLogEntriesToShowEntered(event);

        // Assert positive
        verify(view).displayNTransactionLogEntries(n);
    }

    /**
     * Method-Under-Test: parkingLotEntered(...).
     * 
     * Scenario: A valid secret code is entered.
     * 
     * Expectation: All methods are invoked correctly.
     */
    @Test
    public final void testParkingLotEnteredEventWithSCEnteringCoinBoxLevel()
            throws Exception {
        int parkingLotNumber = 123456;

        // Mock
        ParkingLotEnteredEvent plEnteredEvent = new ParkingLotEnteredEvent(
                view, parkingLotNumber);
        when(parkingMeter.getSecretAction(parkingLotNumber)).thenReturn(
                SecretActionEnum.ENTER_NEW_LEVEL_FOR_COIN_BOXES);

        // Run
        controller.parkingLotEntered(plEnteredEvent);

        // Assert positive
        verify(parkingMeter).getSecretAction(parkingLotNumber);
        // verify(view).displayAllInformation();
    }

    /**
     * Method-Under-Test: parkingLotEntered(...).
     *
     * Scenario: User entered secret code for viewing all parking charges.
     *
     * Expectation: All methods are invoked correctly.
     */
    @Test
    public final void testParkingLotEnteredEventWithValidViewAllParkingChargeSecretCode()
            throws Exception {
        int parkingLotNumber = 123456;

        // Mock
        ParkingLotEnteredEvent plEnteredEvent = new ParkingLotEnteredEvent(
                view, parkingLotNumber);
        when(parkingMeter.getSecretAction(parkingLotNumber)).thenReturn(
                SecretActionEnum.VIEW_ALL_PARKING_CHARGE);

        // Run
        controller.parkingLotEntered(plEnteredEvent);

        // Assert positive
        verify(view).displayBookedParkingLots(any(ArrayList.class));
        verify(view).promptForParkingLotNumber();
    }

    /**
     * Method-Under-Test: parkingLotEntered(...).
     *
     * Scenario: User entered secret code for viewing content of coin boxes.
     *
     * Expectation: All methods are invoked correctly.
     */
    @Test
    public final void testParkingLotEnteredEventWithValidViewContentOfCoinBoxesSecretCode()
            throws Exception {
        int parkingLotNumber = 123456;

        // Mock
        ParkingLotEnteredEvent plEnteredEvent = new ParkingLotEnteredEvent(
                view, parkingLotNumber);
        when(parkingMeter.getSecretAction(parkingLotNumber)).thenReturn(
                SecretActionEnum.VIEW_CONTENT_OF_COIN_BOXES);

        // Run
        controller.parkingLotEntered(plEnteredEvent);

        // Assert positive
        verify(view).displayContentOfCoinBoxes(any(ArrayList.class));
        verify(view).promptForParkingLotNumber();
    }

    /**
     * Method-Under-Test: moneyInserted(...).
     * 
     * Scenario: All data is valid and enough money was inserted.
     * 
     * Expectation: All methods are invoked correctly and deliver the correct
     * result.
     */
    @Test
    public final void testMoneyInsertedEventWithEnoughMoney() {
        int parkingLotNumber = 5;
        MoneyInsertedEvent mInsertedEvent = new MoneyInsertedEvent(view, 5);

        BigDecimal insertedMoney = new BigDecimal(2.0);
        BigDecimal drawback = new BigDecimal(1.00);

        Calendar cal = Calendar.getInstance();
        Date start = cal.getTime();

        cal.add(Calendar.MINUTE, 10);

        Date end = cal.getTime();

        ParkingLotBooking booking = new ParkingLotBooking(false, 5, start, end,
                insertedMoney, new BigDecimal(1.00), drawback);

        // Mock
        when(slotMachine.getAmountOfCurrentlyInsertedMoney()).thenReturn(
                insertedMoney);
        when(parkingMeter.getParkingLot(parkingLotNumber)).thenReturn(null);
        when(parkingMeter.calculateBookingForParkingLot(5, insertedMoney))
                .thenReturn(booking);
        when(slotMachine.hasDrawback()).thenReturn(true);

        // Run
        controller.moneyInserted(mInsertedEvent);

        // Assert positive
        verify(parkingMeter).calculateBookingForParkingLot(5, insertedMoney);
        verify(slotMachine).finishTransaction(drawback);
        verify(view).promptForParkingLotNumber();
        verify(view).displayParkingLotNumberAndParkingTime(5, end);
        verify(view).displayMessageForDrawback();

        // Assert negative
        verify(view, times(0)).displayNotEnoughMoneyError();
        verify(view, times(0)).promptForMoney(5);
    }

    /**
     * Method-Under-Test: moneyInserted(...).
     * 
     * Scenario: All data is valid, but not enough money was inserted.
     * 
     * Expectation: All methods are invoked correctly and deliver the correct
     * result.
     */
    @Test
    public final void testMoneyInsertedEventWithNotEnoughMoney() {
        int parkingLotNumber = 5;

        MoneyInsertedEvent mInsertedEvent = new MoneyInsertedEvent(view, 5);

        BigDecimal insertedMoney = new BigDecimal(2.0);
        BigDecimal drawback = new BigDecimal(1.00);

        Calendar cal = Calendar.getInstance();
        Date start = cal.getTime();

        cal.add(Calendar.MINUTE, 10);

        Date end = cal.getTime();

        ParkingLotBooking booking = new ParkingLotBooking(true, 5, start, end,
                insertedMoney, new BigDecimal(1.00), drawback);

        // Mock
        when(slotMachine.getAmountOfCurrentlyInsertedMoney()).thenReturn(
                insertedMoney);
        when(parkingMeter.getParkingLot(parkingLotNumber)).thenReturn(null);
        when(parkingMeter.calculateBookingForParkingLot(5, insertedMoney))
                .thenReturn(booking);

        // Run
        controller.moneyInserted(mInsertedEvent);

        // Assert positive
        verify(view).displayNotEnoughMoneyError();
        verify(view).promptForMoney(5);
        verify(parkingMeter).calculateBookingForParkingLot(5, insertedMoney);


        // Assert negative
        verify(slotMachine, times(0)).finishTransaction(drawback);
        verify(view, times(0)).displayParkingLotNumberAndParkingTime(5,
                end);
        verify(view, times(0)).promptForParkingLotNumber();
    }

    /**
     * Method-Under-Test: coinBoxLevelEntered(...).
     * 
     * Scenario: All data is valid.
     * 
     * Expectation: The coin box level is set correctly.
     */
    @Test
    public final void testCoinBoxLevelEntered() {
        List<CoinBoxLevel> cbLevels = new ArrayList<>();

        cbLevels.add(new CoinBoxLevel(new BigDecimal(5), 4, 10));

        CoinBoxLevelEnteredEvent event = new CoinBoxLevelEnteredEvent(view,
                cbLevels);

        controller.coinBoxLevelEntered(event);

        // Assert positive
        verify(slotMachine).updateCoinLevelInCoinBoxes(cbLevels);
    }

    /**
     * Method-Under-Test: coinBoxLevelEntered(...).
     * 
     * Scenario: The entered coin count is too high.
     * 
     * Expectation: An error message is displayed and the prompt displayed
     * again.
     */
    @Test
    public final void testCoinBoxLevelEnteredWithLimitReached() {
        List<CoinBoxLevel> cbLevels = new ArrayList<>();

        BigDecimal coin = new BigDecimal(5);
        cbLevels.add(new CoinBoxLevel(coin, 50, 10));

        CoinBoxLevelEnteredEvent event = new CoinBoxLevelEnteredEvent(view,
                cbLevels);

        doThrow(new CoinBoxFullException("coinboxfull", coin))
                .when(slotMachine).updateCoinLevelInCoinBoxes(cbLevels);

        controller.coinBoxLevelEntered(event);

        // Assert positive
        verify(slotMachine).updateCoinLevelInCoinBoxes(cbLevels);
        verify(view).displayCoinCountTooHigh(coin);
    }

    /**
     * Method-Under-Test: shutdownRequested(...).
     * 
     * Scenario: A shutdown request is being sent and the data correctly
     * persisted.
     * 
     * Expectation: All methods are invoked correctly and a shutdown exception
     * is thrown.
     */
    @Test
    public final void testShutdownEventPositive() {
        ShutdownEvent shutdownEvent = new ShutdownEvent(view);

        // Setup
        controller.start();

        // Run
        controller.shutdownRequested(shutdownEvent);

        // Assert positive
        verify(view).displayShutdownMessage();
        verify(view).shutdown();
        verify(appContext).close();
    }

    /**
     * Method-Under-Test: actionAborted(...).
     *
     * Scenario: User aborted the the current action.
     *
     * Expectation: All methods are invoked correctly
     */
    @Test
    public final void testActionAbortedCallsCorrectMethods() {

        // Run
        controller.actionAborted(new ActionAbortedEvent(this));

        // Assert
        verify(slotMachine, times(2)).finishTransaction(any(BigDecimal.class));
        verify(view).displayMessageForDrawback();
        verify(view).promptForParkingLotNumber();
    }
}
