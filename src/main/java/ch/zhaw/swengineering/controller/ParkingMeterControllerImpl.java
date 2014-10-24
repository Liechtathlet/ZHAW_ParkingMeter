package ch.zhaw.swengineering.controller;

import javax.annotation.PostConstruct;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;

import ch.zhaw.swengineering.helper.ConfigurationProvider;
import ch.zhaw.swengineering.model.ParkingLot;
import ch.zhaw.swengineering.model.ParkingMeter;

/**
 * @author Daniel Brun
 * 
 *         Implementation of the {@link ParkingMeterController}
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
    
    /**
     * The ParkingMeter.
     */
    private ParkingMeter parkingMeter;

    /**
     * Initializes the class after the properties have been injected.
     */
    @PostConstruct
    public final void init() {
        LOG.info("Initialize ParkingMeter Controller");

        LOG.info("Load ParkingLots");
        if (parkingMeterProvider != null 
                && parkingMeterProvider.get() != null) {
            parkingMeter = (ParkingMeter) parkingMeterProvider.get();
        }
    }

    @Override
    public final boolean isParkingLot(final int aNumber) {
        boolean result = false;

        for (ParkingLot parkingLot : parkingMeter.parkingLots) {
            if (parkingLot.number == aNumber) {
                result = true;
                break;
            }
        }

        return result;
    }
}
