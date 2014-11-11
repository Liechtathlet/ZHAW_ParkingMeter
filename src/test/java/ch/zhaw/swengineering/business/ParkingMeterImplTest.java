package ch.zhaw.swengineering.business;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;

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

import ch.zhaw.swengineering.helper.ConfigurationProvider;
import ch.zhaw.swengineering.helper.ConfigurationWriter;
import ch.zhaw.swengineering.model.ParkingLotBooking;
import ch.zhaw.swengineering.model.persistence.ParkingLot;
import ch.zhaw.swengineering.model.persistence.ParkingMeter;
import ch.zhaw.swengineering.model.persistence.ParkingTimeDefinition;
import ch.zhaw.swengineering.model.persistence.ParkingTimeDefinitions;
import ch.zhaw.swengineering.model.persistence.SecretActionEnum;
import ch.zhaw.swengineering.model.persistence.SecretCodes;
import ch.zhaw.swengineering.setup.ParkingMeterRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = ParkingMeterRunner.class, loader = AnnotationConfigContextLoader.class)
public class ParkingMeterImplTest {

    @Mock(name = "parkingMeterProvider")
    private ConfigurationProvider configProviderParkingMeter;

    @Mock(name = "secretCodesProvider")
    private ConfigurationProvider configProviderSecretCode;

    @Mock(name = "parkingTimeDefinitionProvider")
    private ConfigurationProvider configProviderTimeDef;

    @Mock(name = "parkingMeterWriter")
    private ConfigurationWriter configWriterParkingMeter;

    @InjectMocks
    private ParkingMeterImpl controller;

    private ParkingMeter parkingMeter;

    private ParkingTimeDefinitions parkingTimeDefinitions;

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
        parkingTimeDefinitions = getParkingTimeDefinitionMock();

        when(configProviderParkingMeter.get()).thenReturn(parkingMeter);
        when(configProviderTimeDef.get()).thenReturn(parkingTimeDefinitions);

        controller.init();
    }

    /**
     * Argument: Invalid parking lot number.
     * 
     * Method: 'getParkingLot'
     * 
     * Expected: Null
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
     * Argument: Valid parking lot number.
     * 
     * Method: 'getParkingLot'
     * 
     * Expected: Correct parking lot object
     */
    @Test
    public final void getParkingLotWithValidLotNumber() {
        // Setup
        initStandard();

        // Assert
        for (ParkingLot pl : parkingMeter.parkingLots) {
            ParkingLot reqParkingLot = controller.getParkingLot(pl.getNumber());

            Assert.assertNotNull(reqParkingLot);
            assertEquals(pl.getNumber(), reqParkingLot.getNumber());
            assertEquals(pl.getPaidUntil(), reqParkingLot.getPaidUntil());
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
        assertEquals(secretAction, controller.getSecretAction(secretKey));
    }

    /**
     * Method-Under-Test: validateSecretCodes(...).
     * 
     * Scenario: The secret code mapping is invalid.
     * 
     * Expectation: The validation is not successful and an exception is thrown.
     */
    @Test(expected = Exception.class)
    public final void exceptionShouldBeThrownWhenNoSecretActionsLoaded()
            throws Exception {

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
    public final void illegalArgumentExceptionShouldBeThrownWhenNoSecretActionCanBefound()
            throws Exception {

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

    // ************** Tests booking calculation ***************

    /**
     * Argument: Valid data with enough money.
     * 
     * Method: 'calculateBookingForParkingLot'
     * 
     * Expected: All results are correct.
     */
    @Test
    public final void testCalculateBookingForParkingLotWithEnoughMoney() {
        // Setup
        initStandard();

        ParkingLotBooking booking = controller.calculateBookingForParkingLot(5,
                new BigDecimal(5.0));

        // 5.0 ->
        Assert.assertNotNull(booking);
        assertEquals(new BigDecimal(4), booking.getChargedMoney());
        assertEquals(new BigDecimal(1), booking.getDrawbackMoney());
        assertEquals(new BigDecimal(5), booking.getInsertedMoney());
        assertEquals(booking.isNotEnoughMoney(), false);

        int diff = (int) (booking.getPaidTill().getTime() - booking
                .getPaidFrom().getTime());

        int minutes = diff / 1000 / 60;

        assertEquals(90, minutes);
    }

    /**
     * Argument: Valid data with not enough money.
     * 
     * Method: 'calculateBookingForParkingLot'
     * 
     * Expected: All results are correct.
     */
    @Test
    public final void testCalculateBookingForParkingLotWithNotEnoughMoney() {
        // Setup
        initStandard();

        ParkingLotBooking booking = controller.calculateBookingForParkingLot(5,
                new BigDecimal(0.5));

        // 5.0 ->
        Assert.assertNotNull(booking);
        assertEquals(booking.isNotEnoughMoney(), true);
        Assert.assertNull(booking.getChargedMoney());
        assertEquals(booking.getDrawbackMoney(), new BigDecimal(0.5));
        assertEquals(new BigDecimal(0.5), booking.getInsertedMoney());
    }

    /**
     * Argument: A valid booking.
     * 
     * Method: 'persistBooking'
     * 
     * Expected: All results are correct.
     */
    @Test
    public final void testPersistBookingWithValidBooking() {
        // Setup
        initStandard();

        Date paidUntil = new Date();
        ParkingLotBooking booking = new ParkingLotBooking(5,
                new BigDecimal(5.0));
        booking.setPaidTill(paidUntil);

        controller.persistBooking(booking);
        ParkingLot lot = controller.getParkingLot(5);

        assertEquals(paidUntil, lot.getPaidUntil());
        verify(configWriterParkingMeter).write(any(ParkingMeter.class));

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

    /**
     * Creates a new parking time definition mock.
     * 
     * @return The mock.
     */
    private ParkingTimeDefinitions getParkingTimeDefinitionMock() {
        ParkingTimeDefinitions definitions = new ParkingTimeDefinitions();

        List<ParkingTimeDefinition> defList = new ArrayList<>();

        defList.add(new ParkingTimeDefinition(new BigDecimal(2.0), 30, 2, 2));
        defList.add(new ParkingTimeDefinition(new BigDecimal(1.0), 30, 2, 1));

        definitions.setParkingTimeDefinitions(defList);

        return definitions;
    }
}
