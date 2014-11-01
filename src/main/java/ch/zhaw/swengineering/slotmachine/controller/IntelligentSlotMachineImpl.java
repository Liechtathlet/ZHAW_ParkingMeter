package ch.zhaw.swengineering.slotmachine.controller;

import javax.annotation.PostConstruct;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;

import ch.zhaw.swengineering.controller.ParkingMeterControllerImpl;
import ch.zhaw.swengineering.helper.ConfigurationProvider;
import ch.zhaw.swengineering.model.CoinBoxes;
import ch.zhaw.swengineering.model.SecretCodes;

/**
 * @author Daniel Brun
 * 
 *         Implementation of the {@link IntelligentSlotMachine}.
 */
@Controller
public class IntelligentSlotMachineImpl implements IntelligentSlotMachine {

    /**
     * The Logger.
     */
    private static final Logger LOG = LogManager
            .getLogger(IntelligentSlotMachineImpl.class);
    
    @Autowired
    @Qualifier("coinBoxes")
    private ConfigurationProvider coinBoxesProvider;
    private CoinBoxes coinBoxes;

   
    /**
     * Loads the data from the configuration providers.
     */
    @PostConstruct
    private void init() {
        LOG.info("Loading data...");

        LOG.info("Loading CoinBoxes...");
        if (coinBoxesProvider != null && coinBoxesProvider.get() != null) {
            coinBoxes = (CoinBoxes) coinBoxesProvider.get();
        }
    }

    @Override
    public double getAmountOfCurrentlyInsertedMoney() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void finishTransaction() {
        // TODO Auto-generated method stub

    }

    @Override
    public void insertCoin(int aCoinValue) {
        // TODO Auto-generated method stub

    }
}
