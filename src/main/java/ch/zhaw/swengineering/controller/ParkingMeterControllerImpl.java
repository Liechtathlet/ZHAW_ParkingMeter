package ch.zhaw.swengineering.controller;

import ch.zhaw.swengineering.helper.ConfigurationProvider;
import ch.zhaw.swengineering.model.ParkingLot;
import ch.zhaw.swengineering.model.ParkingMeter;
import ch.zhaw.swengineering.model.SecretActionEnum;
import ch.zhaw.swengineering.model.SecretCodes;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;

import javax.annotation.PostConstruct;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

/**
 * @author Daniel Brun Implementation of the {@link ParkingMeterController}
 */
@Controller
public class ParkingMeterControllerImpl implements ParkingMeterController {

    /**
     * The Logger.
     */
    private static final Logger LOG = LogManager
            .getLogger(ParkingMeterControllerImpl.class);

    /**
     * ConfigurationProvider for the 'ParkingMeter'.
     */
    @Autowired
    @Qualifier("parkingMeter")
    private ConfigurationProvider parkingMeterProvider;

    @Autowired
    @Qualifier("secretCodes")
    private ConfigurationProvider secretCodesProvider;
    
    /**
     * The ParkingMeter.
     */
    private ParkingMeter parkingMeter;

    private SecretCodes secretCodes;
    
    /**
     * Initializes the class after the properties have been injected.
     */
    @PostConstruct
    public final void init() {
        LOG.info("Initialize ParkingMeter Controller");

        LOG.info("Load ParkingLots");
        if (parkingMeterProvider != null && parkingMeterProvider.get() != null) {
            parkingMeter = (ParkingMeter) parkingMeterProvider.get();
        }

        LOG.info("Loading SecretCodes...");
        if (secretCodesProvider != null && secretCodesProvider.get() != null) {
            secretCodes = (SecretCodes) secretCodesProvider.get();
            validateSecretCodes();
        }
    }

    @Override
    public ParkingLot getParkingLot(final int aNumber) {
        ParkingLot parkingLot = null;

        for (ParkingLot pl : parkingMeter.parkingLots) {
            if (pl.number == aNumber) {
                parkingLot = pl;
                break;
            }
        }

        return parkingLot;
    }

    @Override
    public SecretActionEnum getSecretAction(int secretKey) throws Exception {

        if (secretCodes == null) {
            throw new Exception();
        }

        for (Object o : secretCodes.getCodeMapping().entrySet()) {
            Map.Entry secretCodeEntry = (Map.Entry) o;
            if (secretCodeEntry.getKey().equals(secretKey)) {
                return (SecretActionEnum) secretCodeEntry.getValue();
            }
        }

        throw new IllegalArgumentException();
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
}
