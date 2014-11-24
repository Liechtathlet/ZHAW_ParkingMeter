package ch.zhaw.swengineering.business;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.PostConstruct;

import ch.zhaw.swengineering.model.ParkingTimeTable;
import ch.zhaw.swengineering.model.ParkingTimeTableItem;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import ch.zhaw.swengineering.helper.AssertHelper;
import ch.zhaw.swengineering.helper.ConfigurationProvider;
import ch.zhaw.swengineering.helper.ConfigurationWriter;
import ch.zhaw.swengineering.model.ParkingLotBooking;
import ch.zhaw.swengineering.model.persistence.ParkingLot;
import ch.zhaw.swengineering.model.persistence.ParkingTimeDefinition;
import ch.zhaw.swengineering.model.persistence.ParkingTimeDefinitions;
import ch.zhaw.swengineering.model.persistence.SecretActionEnum;
import ch.zhaw.swengineering.model.persistence.SecretCodes;

/**
 * @author Daniel Brun Implementation of the {@link ParkingMeter}
 */
@Component
public class ParkingMeterImpl implements ParkingMeter {

    /**
     * The Logger.
     */
    private static final Logger LOG = LogManager
            .getLogger(ParkingMeterImpl.class);

    /**
     * ConfigurationProvider for the 'ParkingMeter'.
     */
    @Autowired
    @Qualifier("parkingMeter")
    private ConfigurationProvider parkingMeterProvider;

    @Autowired
    @Qualifier("secretCodes")
    private ConfigurationProvider secretCodesProvider;

    @Autowired
    @Qualifier("parkingTimeDef")
    private ConfigurationProvider parkingTimeDefinitionProvider;

    @Autowired
    @Qualifier("parkingMeter")
    private ConfigurationWriter parkingMeterWriter;

    /**
     * The ParkingMeter.
     */
    private ch.zhaw.swengineering.model.persistence.ParkingMeter parkingMeter;

    private SecretCodes secretCodes;

    private ParkingTimeDefinitions definitions;

    /**
     * Initializes the class after the properties have been injected.
     */
    @PostConstruct
    public final void init() {
        LOG.info("Initialize ParkingMeter Controller");

        LOG.info("Load ParkingLots");
        if (parkingMeterProvider != null && parkingMeterProvider.get() != null) {
            parkingMeter = (ch.zhaw.swengineering.model.persistence.ParkingMeter) parkingMeterProvider
                    .get();
        }

        LOG.info("Loading SecretCodes...");
        if (secretCodesProvider != null && secretCodesProvider.get() != null) {
            secretCodes = (SecretCodes) secretCodesProvider.get();
            validateSecretCodes();
        }

        LOG.info("Loading ParkingTimeDefinitions...");
        if (parkingTimeDefinitionProvider != null
                && parkingTimeDefinitionProvider.get() != null) {
            definitions = (ParkingTimeDefinitions) parkingTimeDefinitionProvider
                    .get();
            validateParkingTimeDefinitions();
            sortParkingTimeDefinitions();
        }
    }

    @Override
    public ParkingLot getParkingLot(final int aNumber) {
        ParkingLot parkingLot = null;

        for (ParkingLot pl : parkingMeter.parkingLots) {
            if (pl.getNumber() == aNumber) {
                parkingLot = pl;
                break;
            }
        }

        return parkingLot;
    }

    @Override
    public List<ParkingLot> getParkingLots() {
        return parkingMeter.parkingLots;

    }

    // @Override
    // public void callAllBookedParkingLots() {
    // ArrayList<ParkingLot> parkingLots;
    // parkingLots = (ArrayList<ParkingLot>) parkingMeter.parkingLots;
    //
    // AllParkingCharge allParkingCharge = new AllParkingCharge(parkingLots);
    //
    // }

    @Override
    public SecretActionEnum getSecretAction(int secretKey) throws Exception {

        if (secretCodes == null) {
            throw new Exception();
        }

        for (Entry<Integer, SecretActionEnum> secretCodeEntry : secretCodes
                .getCodeMapping().entrySet()) {
            if (secretCodeEntry.getKey().equals(secretKey)) {
                return secretCodeEntry.getValue();
            }
        }

        throw new IllegalArgumentException();
    }

    @Override
    public void persistBooking(ParkingLotBooking aBooking) {
        AssertHelper.isNotNull(aBooking, "aBooking");

        LOG.info("Persisting parking time for parking lot: '"
                + aBooking.getParkingLotNumber() + "', paid until:"
                + aBooking.getPaidTill());
        ParkingLot parkingLot = getParkingLot(aBooking.getParkingLotNumber());
        parkingLot.setPaidUntil(aBooking.getPaidTill());

        parkingMeterWriter.write(parkingMeter);
    }

    @Override
    public ParkingLotBooking calculateBookingForParkingLot(int aParkingLot,
            BigDecimal someInsertedMoney) {

        ParkingLotBooking booking = new ParkingLotBooking(aParkingLot,
                someInsertedMoney);

        BigDecimal leftoverMoney = someInsertedMoney;
        boolean minimumBooking = false;
        int bookingInMinutes = 0;

        // Loop over definitions...
        for (ParkingTimeDefinition def : definitions
                .getParkingTimeDefinitions()) {

            int periodCount = leftoverMoney
                    .divideToIntegralValue(def.getPricePerPeriod())
                    .toBigInteger().intValue();

            if (periodCount == 0) {
                booking.setDrawbackMoney(leftoverMoney);
                booking.setNotEnoughMoney(!minimumBooking);
                break;
            } else {
                // At least one period is covered.
                minimumBooking = true;

                int countOfSuccessivePeriods = def
                        .getCountOfSuccessivePeriods();

                // Limit period count
                if (countOfSuccessivePeriods != 0
                        && periodCount > countOfSuccessivePeriods) {
                    periodCount = countOfSuccessivePeriods;
                }

                // Calculate
                bookingInMinutes += def.getDurationOfPeriodInMinutes()
                        * periodCount;
                leftoverMoney = leftoverMoney.subtract(def.getPricePerPeriod()
                        .multiply(new BigDecimal(periodCount)));
            }
        }

        // Do final calculations.
        if (minimumBooking) {
            booking.setDrawbackMoney(leftoverMoney);
            booking.setChargedMoney(someInsertedMoney.subtract(leftoverMoney));
            booking.setBookingInMinutes(bookingInMinutes);

            Calendar cal = Calendar.getInstance();
            booking.setPaidFrom(cal.getTime());

            cal.add(Calendar.MINUTE, bookingInMinutes);

            booking.setPaidTill(cal.getTime());
        }

        return booking;
    }

    /**
     * Validates if multiple codes are stored for an action.
     */
    public final void validateSecretCodes() {
        Map<Integer, SecretActionEnum> mapping = secretCodes.getCodeMapping();

        Collection<SecretActionEnum> values = mapping.values();
        for (SecretActionEnum actionEnum : SecretActionEnum.values()) {
            int count = 0;
            for (SecretActionEnum actionEnumToCompare : values) {
                if (actionEnumToCompare.equals(actionEnum)) {
                    count++;
                }
            }

            if (count > 1) {
                throw new IllegalArgumentException(
                        "A secret action can only be mapped "
                                + "once to a secret code!");
            }
        }

    }

    /**
     * Validates the loaded parking time definitions.
     */
    public final void validateParkingTimeDefinitions() {
        if (definitions.getParkingTimeDefinitions() == null
                || definitions.getParkingTimeDefinitions().size() == 0) {
            throw new IllegalArgumentException(
                    "At least one parking time definition must be configured.");
        }
    }

    /**
     * Executes a sort on the parking time definitions.
     */
    private void sortParkingTimeDefinitions() {
        Collections.sort(definitions.getParkingTimeDefinitions());
    }

    @Override
    public List<ParkingTimeDefinition> getParkingTimeDefinitions() {
        return definitions.getParkingTimeDefinitions();
    }

    @Override
    public ParkingTimeTable getParkingTimeTable() {
        int number = getParkingLots().get(0).getNumber();
        BigDecimal lastAmount = BigDecimal.ZERO;

        List<ParkingTimeTableItem> items = new ArrayList<>();

        for (ParkingTimeDefinition def : definitions
                .getParkingTimeDefinitions()) {
            for (int i = 0; i < def.getCountOfSuccessivePeriods(); i ++) {
                ParkingLotBooking booking =
                        calculateBookingForParkingLot(number, lastAmount);
                items.add(new ParkingTimeTableItem(
                        lastAmount, booking.getBookingInMinutes()));
                lastAmount = lastAmount.add(def.getPricePerPeriod());
            }
        }

        int maxBookingTime = getMaxBookingTime();
        BigDecimal maxPrice = getMaxPrice();
        items.add(new ParkingTimeTableItem(maxPrice, maxBookingTime));

        return new ParkingTimeTable(items, maxBookingTime,
                maxPrice);
    }

    private BigDecimal getMaxPrice() {
        BigDecimal maxPrice = BigDecimal.ZERO;
        for (ParkingTimeDefinition def : definitions
                .getParkingTimeDefinitions()) {
            BigDecimal periodCount =
                    BigDecimal.valueOf(def.getCountOfSuccessivePeriods());
            BigDecimal priceForPeriods = def.getPricePerPeriod().multiply(
                    periodCount);
            maxPrice = maxPrice.add(priceForPeriods);
        }

        return maxPrice;
    }

    private int getMaxBookingTime() {
        int maxTime = 0;
        for (ParkingTimeDefinition def : definitions
                .getParkingTimeDefinitions()) {
            maxTime = maxTime +
                    def.getDurationOfPeriodInMinutes()
                    * def.getCountOfSuccessivePeriods();
        }

        return maxTime;
    }
}
