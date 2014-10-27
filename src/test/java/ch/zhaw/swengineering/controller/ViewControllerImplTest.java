package ch.zhaw.swengineering.controller;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.any;
import static org.mockito.Matchers.eq;

import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import ch.zhaw.swengineering.event.ParkingLotEnteredEvent;
import ch.zhaw.swengineering.model.ParkingLot;
import ch.zhaw.swengineering.setup.ParkingMeterRunner;
import ch.zhaw.swengineering.view.console.ConsoleSimulationView;

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

    @InjectMocks
    private ViewControllerImpl controller;

    @Mock
    private ParkingMeterControllerImpl parkingMeterController;

    /**
     * Set up a test case.
     */
    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    /**
     * Method-Under-Test: start(...).
     * 
     * Expectation: All methods are invoked correctly.
     */
    @Test
    public void testStartOnCorrectMethodInvocation() {

        // Start
        controller.start();

        // Assert
        verify(view).addViewEventListener(controller);
        verify(view).startSimulationView();
        verify(view).promptForParkingLotNumber();
    }

    /**
     * Method-Under-Test: parkingLotEntered(...). Scenario: A valid parking lot
     * number is entered. Expectation: All methods are invoked correctly.
     */
    @Test
    public void testParkingLotEnteredEventWithValidParkingLotNumber() {
        int parkingLotNumber = 5;
        Date paidUntil = new Date();

        ParkingLot parkingLot = new ParkingLot(parkingLotNumber, paidUntil);
        ParkingLotEnteredEvent plEnteredEvent = new ParkingLotEnteredEvent(
                view, parkingLotNumber);

        // Mock
        when(parkingMeterController.getParkingLot(parkingLotNumber))
                .thenReturn(parkingLot);

        // Setup
        controller.start();

        // Execute Test
        controller.parkingLotEntered(plEnteredEvent);

        // Assert positive
        verify(parkingMeterController).getParkingLot(parkingLotNumber);
        verify(view).displayParkingLotNumberAndParkingTime(parkingLotNumber,
                paidUntil);
        verify(view).promptForMoney(parkingLotNumber);
        verify(view).promptForParkingLotNumber();
        
        //Assert negative
        verify(view, Mockito.times(0)).displayParkingLotNumberInvalid();
    }

    /**
     * Method-Under-Test: parkingLotEntered(...). Scenario: An invalid parking
     * lot number is entered. Expectation: All methods are invoked correctly.
     */
    @Test
    public void testParkingLotEnteredEventWithInvalidParkingLotNumber() {
        int parkingLotNumber = 5;

        ParkingLotEnteredEvent plEnteredEvent = new ParkingLotEnteredEvent(
                view, parkingLotNumber);

        // Mock
        when(parkingMeterController.getParkingLot(parkingLotNumber))
                .thenReturn(null);

        // Setup
        controller.start();

        // Execute Test
        controller.parkingLotEntered(plEnteredEvent);

        // Assert prositive
        verify(parkingMeterController).getParkingLot(parkingLotNumber);
        verify(view).displayParkingLotNumberInvalid();
        verify(view, Mockito.times(2)).promptForParkingLotNumber();

        // Assert negative
        verify(view, Mockito.times(0)).displayParkingLotNumberAndParkingTime(
                eq(parkingLotNumber), any(Date.class));
        verify(view, Mockito.times(0)).promptForMoney(parkingLotNumber);
    }
}
