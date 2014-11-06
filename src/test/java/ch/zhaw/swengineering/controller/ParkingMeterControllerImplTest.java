package ch.zhaw.swengineering.controller;

import ch.zhaw.swengineering.helper.ConfigurationProvider;
import ch.zhaw.swengineering.model.persistence.ParkingLot;
import ch.zhaw.swengineering.model.persistence.ParkingMeter;
import ch.zhaw.swengineering.model.persistence.SecretActionEnum;
import ch.zhaw.swengineering.model.persistence.SecretCodes;
import ch.zhaw.swengineering.setup.ParkingMeterRunner;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import java.util.Calendar;
import java.util.Hashtable;

import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = ParkingMeterRunner.class, loader = AnnotationConfigContextLoader.class)
public class ParkingMeterControllerImplTest {

    @Mock(name = "parkingMeterProvider")
    private ConfigurationProvider configProviderParkingMeter;

    @Mock(name = "secretCodesProvider")
    private ConfigurationProvider configProviderSecretCode;

    @InjectMocks
    private ParkingMeterControllerImpl controller;

    private ParkingMeter parkingMeter;

    /**
     * Sets up a test case.
     */
    @Before
    public final void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    /**
     * Performs some standard initialization.
     */
    private void initStandard() {
        parkingMeter = getParkingMeterMock();

        when(configProviderParkingMeter.get()).thenReturn(parkingMeter);

        controller.init();
    }

    /**
     * Argument: Invalid parking lot number. Method: 'getParkingLot' Expected
     * result: Null
     */
    @Test
    public final void getParkingLotLotWithInvalidLotNumber() {
        // Setup
        initStandard();

        // Assert
        Assert.assertNull(controller.getParkingLot(100));
        Assert.assertNull(controller.getParkingLot(0));
        Assert.assertNull(controller.getParkingLot(150));
        Assert.assertNull(controller.getParkingLot(20));
    }

    /**
     * Argument: Valid parking lot number. Method: 'getParkingLot' Expected
     * result: Correct parking lot object
     */
    @Test
    public final void getParkingLotWithValidLotNumber() {
        // Setup
        initStandard();

        // Assert
        for (ParkingLot pl : parkingMeter.parkingLots) {
            ParkingLot reqParkingLot = controller.getParkingLot(pl.number);

            Assert.assertNotNull(reqParkingLot);
            Assert.assertEquals(pl.number, reqParkingLot.number);
            Assert.assertEquals(pl.paidUntil, reqParkingLot.paidUntil);
        }
    }

    // ************** Tests secret code validation ***************

    /**
     * Method-Under-Test: getSecretAction(...).
     * 
     * Scenario: The secret code mapping is valid.
     * 
     * Expectation: The validation is successful.
     */
    @Test
    public final void secretCodesMappingIsValid() throws Exception {

        int secretKey = 12345;
        SecretActionEnum secretAction = SecretActionEnum.VIEW_ALL_INFORMATION;

        // Setup
        Hashtable<Integer, SecretActionEnum> mappingTable = new Hashtable<>();

        mappingTable.put(secretKey, secretAction);

        SecretCodes secretCodes = new SecretCodes(mappingTable);

        when(configProviderSecretCode.get()).thenReturn(secretCodes);

        controller.init();

        // Assert
        Assert.assertEquals(secretAction, controller.getSecretAction(secretKey));
    }

    /**
     * Method-Under-Test: validateSecretCodes(...).
     * 
     * Scenario: The secret code mapping is invalid.
     * 
     * Expectation: The validation is not successful and an exception is thrown.
     */
    @Test(expected = Exception.class)
    public final void exceptionShouldBeThrownWhenNoSecretActionsLoaded() throws Exception {

        int secretKey = 999;

        // Assert
        controller.getSecretAction(secretKey);
    }

    /**
     * Method-Under-Test: validateSecretCodes(...).
     *
     * Scenario: The secret code mapping is invalid.
     *
     * Expectation: The validation is not successful and an exception is thrown.
     */
    @Test(expected = IllegalArgumentException.class)
    public final void illegalArgumentexceptionShouldBeThrownWhenNoSecretActionCanBefound() throws Exception {

        int secretKey = 999;
        SecretActionEnum secretAction = SecretActionEnum.VIEW_ALL_INFORMATION;

        // Setup
        Hashtable<Integer, SecretActionEnum> mappingTable = new Hashtable<>();

        mappingTable.put(111, secretAction);

        SecretCodes secretCodes = new SecretCodes(mappingTable);

        when(configProviderSecretCode.get()).thenReturn(secretCodes);

        controller.init();

        // Assert
        controller.getSecretAction(secretKey);
    }

    /**
     * Creates a ParkingMeter mock object.
     * 
     * @return the mock
     */
    private ParkingMeter getParkingMeterMock() {
        ParkingMeter parkingMeter = new ParkingMeter();

        Calendar parkingTime = Calendar.getInstance();

        // Preset values of calendar
        parkingTime.set(Calendar.DAY_OF_MONTH, 5);
        parkingTime.set(Calendar.SECOND, 0);
        parkingTime.set(Calendar.MILLISECOND, 0);

        // ParkingLot Nr. 1
        parkingTime.set(Calendar.HOUR, 11);
        parkingTime.set(Calendar.MINUTE, 0);
        parkingMeter.parkingLots.add(new ParkingLot(1, parkingTime.getTime()));

        // ParkingLot Nr. 2
        parkingTime.set(Calendar.HOUR, 10);
        parkingTime.set(Calendar.MINUTE, 0);
        parkingMeter.parkingLots.add(new ParkingLot(2, parkingTime.getTime()));

        // ParkingLot Nr. 5
        parkingTime.set(Calendar.HOUR, 15);
        parkingTime.set(Calendar.MINUTE, 30);
        parkingMeter.parkingLots.add(new ParkingLot(5, parkingTime.getTime()));

        // ParkingLot Nr. 10
        parkingTime.set(Calendar.HOUR, 13);
        parkingTime.set(Calendar.MINUTE, 20);
        parkingMeter.parkingLots.add(new ParkingLot(10, parkingTime.getTime()));
        return parkingMeter;
    }
}
