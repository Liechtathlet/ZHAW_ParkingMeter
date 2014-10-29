package ch.zhaw.swengineering.controller;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;

import ch.zhaw.swengineering.event.ActionAbortedEvent;
import ch.zhaw.swengineering.event.MoneyInsertedEvent;
import ch.zhaw.swengineering.event.ParkingLotEnteredEvent;
import ch.zhaw.swengineering.event.ViewEventListener;
import ch.zhaw.swengineering.helper.ConfigurationProvider;
import ch.zhaw.swengineering.model.CoinBoxes;
import ch.zhaw.swengineering.model.ParkingLot;
import ch.zhaw.swengineering.model.SecretCodes;
import ch.zhaw.swengineering.view.SimulationView;

/**
 * @author Daniel Brun Controller for the view.
 */
@Controller
public class ViewControllerImpl implements ViewController, ViewEventListener {

    /**
     * The Logger.
     */
    private static final Logger LOG = LogManager
            .getLogger(ViewControllerImpl.class);

    @Autowired
    private SimulationView view;

    @Autowired
    private ParkingMeterController parkingMeterController;

    @Autowired
    @Qualifier("coinBoxes")
    private ConfigurationProvider coinBoxesProvider;
    private CoinBoxes coinBoxes;

    @Autowired
    @Qualifier("secretCodes")
    private ConfigurationProvider secretCodesProvider;
    private SecretCodes secretCodes;

    @Override
    public final void start() {
        LOG.info("Starting controller...");

        // Register event listener.
        view.addViewEventListener(this);

        loadData();

        // Start simulation view.
        view.startSimulationView();

        // Start process
        view.promptForParkingLotNumber();
    }

    /**
     * Loads the data from the configuration providers.
     */
    private void loadData() {
        LOG.info("Loading data...");

        LOG.info("Loading CoinBoxes...");
        if (coinBoxesProvider != null && coinBoxesProvider.get() != null) {
            coinBoxes = (CoinBoxes) coinBoxesProvider.get();
        }

        LOG.info("Loading SecretCodes...");
        if (secretCodesProvider != null && secretCodesProvider.get() != null) {
            secretCodes = (SecretCodes) secretCodesProvider.get();
        }
    }
    
    @Override
    public final void parkingLotEntered(
            final ParkingLotEnteredEvent parkingLotEnteredEvent) {
        LOG.debug("User entered parking lot number: "
                + parkingLotEnteredEvent.getParkingLotNumber());

        boolean processed = false;
        ParkingLot parkingLot = parkingMeterController
                .getParkingLot(parkingLotEnteredEvent.getParkingLotNumber());

        // Step One: Check if it is a parking lot number
        if (parkingLot != null) {
            processed = true;
            view.displayParkingLotNumberAndParkingTime(parkingLot.number,
                    parkingLot.paidUntil);
            view.promptForMoney(parkingLot.number);
        }

        // Step Two: Check if it is a secret number

        // Step Three: Print error if nothing matched
        if (!processed) {
            view.displayParkingLotNumberInvalid();
            view.promptForParkingLotNumber();
        }
    }

    public void actionAborted(final ActionAbortedEvent actionAbortedEvent) {
        // TODO Action aborted
        view.promptForParkingLotNumber();
    }

    @Override
    public void moneyInserted(MoneyInsertedEvent moneyInsertedEvent) {
        // TODO Auto-generated method stub
    }
}
