package ch.zhaw.swengineering.controller;

import ch.zhaw.swengineering.business.ParkingMeterImpl;
import ch.zhaw.swengineering.event.CoinBoxLevelEnteredEvent;
import ch.zhaw.swengineering.event.MoneyInsertedEvent;
import ch.zhaw.swengineering.event.ParkingLotEnteredEvent;
import ch.zhaw.swengineering.event.ShutdownEvent;
import ch.zhaw.swengineering.model.CoinBoxLevel;
import ch.zhaw.swengineering.model.ParkingLotBooking;
import ch.zhaw.swengineering.model.persistence.ParkingLot;
import ch.zhaw.swengineering.model.persistence.SecretActionEnum;
import ch.zhaw.swengineering.setup.ParkingMeterRunner;
import ch.zhaw.swengineering.slotmachine.controller.IntelligentSlotMachineBackendInteractionInterface;
import ch.zhaw.swengineering.slotmachine.exception.CoinBoxFullException;
import ch.zhaw.swengineering.view.console.ConsoleSimulationView;
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
    private ConsoleSimulationView view;

    @Mock
    private ConfigurableApplicationContext appContext;

    @InjectMocks
    private ViewControllerImpl controller;

    @Mock
    private ParkingMeterImpl parkingMeter;

    @Mock
    private IntelligentSlotMachineBackendInteractionInterface slotMachine;

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

        // Setup
        controller.start();

        // Execute Test
        controller.parkingLotEntered(plEnteredEvent);

        // Assert positive
        verify(parkingMeter).getParkingLot(parkingLotNumber);
        verify(view).displayParkingLotNumberAndParkingTime(parkingLotNumber,
                paidUntil);
        verify(view).promptForMoney(parkingLotNumber);
        verify(view).promptForParkingLotNumber();

        // Assert negative
        verify(view, times(0)).displayErrorParkingLotNumberInvalid();
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

        // Setup
        controller.start();

        // Execute Test
        controller.parkingLotEntered(plEnteredEvent);

        // Assert positive
        verify(parkingMeter).getParkingLot(parkingLotNumber);
        verify(view).displayErrorParkingLotNumberInvalid();
        verify(view, times(2)).promptForParkingLotNumber();

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

        // Setup
        controller.start();

        // Execute Test
        controller.parkingLotEntered(plEnteredEvent);

        // Assert positive
        verify(parkingMeter).getSecretAction(parkingLotNumber);
        verify(view).displayParkingMeterInfo();
        verify(view).displayContentOfCoinBoxes(
                slotMachine.getCurrentCoinBoxLevel());
        verify(view).displayParkingTimeDefinitions(
                parkingMeter.getParkingTimeDefinitions());
        verify(view, times(2)).promptForParkingLotNumber();
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

        // Setup
        controller.start();

        // Execute Test
        controller.parkingLotEntered(plEnteredEvent);

        // Assert positive
        verify(parkingMeter).getSecretAction(parkingLotNumber);
        verify(view).displayAllTransactionLogs();
        verify(view, times(2)).promptForParkingLotNumber();
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

        // Setup
        controller.start();

        // Execute Test
        controller.parkingLotEntered(plEnteredEvent);

        // Assert positive
        verify(parkingMeter).getSecretAction(parkingLotNumber);
        verify(view).displayLast24HoursOfTransactionLog();
        verify(view, times(2)).promptForParkingLotNumber();
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

        // Setup
        controller.start();

        // Execute Test
        controller.parkingLotEntered(plEnteredEvent);

        // Assert positive
        verify(parkingMeter).getSecretAction(parkingLotNumber);
        // verify(view).displayAllInformation();

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
    public final void testMoneyEnsertedEventWithEnoughMoney() {
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

        // Setup
        controller.start();

        // Run
        controller.moneyInserted(mInsertedEvent);

        // Assert positive
        verify(parkingMeter).calculateBookingForParkingLot(5, insertedMoney);
        verify(slotMachine).finishTransaction(drawback);
        verify(view, times(2)).promptForParkingLotNumber();
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
    public final void testMoneyEnsertedEventWithNotEnoughMoney() {
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

        // Setup
        controller.start();

        // Run
        controller.moneyInserted(mInsertedEvent);

        // Assert positive
        verify(view).displayNotEnoughMoneyError();
        verify(view).promptForMoney(5);
        verify(parkingMeter).calculateBookingForParkingLot(5, insertedMoney);

        verify(view).promptForParkingLotNumber();

        // Assert negative
        verify(slotMachine, times(0)).finishTransaction(drawback);
        verify(view, times(0)).displayParkingLotNumberAndParkingTime(5,
                end);
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

        // Execute Test
        controller.shutdownRequested(shutdownEvent);

        // Assert positive
        verify(view).displayShutdownMessage();
        verify(view).shutdown();
        verify(appContext).close();
    }
}
