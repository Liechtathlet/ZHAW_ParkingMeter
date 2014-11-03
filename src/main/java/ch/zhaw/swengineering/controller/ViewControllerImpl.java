package ch.zhaw.swengineering.controller;

import ch.zhaw.swengineering.event.*;
import ch.zhaw.swengineering.model.ParkingLot;
import ch.zhaw.swengineering.slotmachine.controller.IntelligentSlotMachineBackendInteractionInterface;
import ch.zhaw.swengineering.view.SimulationView;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Controller;

import java.math.BigDecimal;

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
    private ConfigurableApplicationContext appContext;
    
    @Autowired
    private IntelligentSlotMachineBackendInteractionInterface slotMachine;

    @Override
    public final void start() {
        LOG.info("Starting controller...");

        // Register event listener.
        view.addViewEventListener(this);

        // Start simulation view.
        view.startSimulationView();

        // Start process
        view.promptForParkingLotNumber();
    }

    @Override
    public final void parkingLotEntered(
            final ParkingLotEnteredEvent parkingLotEnteredEvent) {
        int parkingLotNumber = parkingLotEnteredEvent.getParkingLotNumber();
        LOG.debug("User entered parking lot number: "
                + parkingLotNumber);

        boolean processed = false;

        // Step One: Check if it is a parking lot number
        ParkingLot parkingLot = parkingMeterController.getParkingLot(parkingLotNumber);
        if (parkingLot != null) {
            processed = true;
            slotMachine.startTransaction();
            view.displayParkingLotNumberAndParkingTime(parkingLot.number,
                    parkingLot.paidUntil);
            view.promptForMoney(parkingLot.number);
        }

        // Step Two: Check if it is a secret number
        //SecretActionEnum secretAction = parkingMeterController.getSecretAction(parkingLotNumber);
        //if (parkingMeterController.isValid)

        // Step Three: Print error if nothing matched
        if (!processed) {
            view.displayParkingLotNumberInvalid();
            view.promptForParkingLotNumber();
        }
    }

    @Override
    public void actionAborted(final ActionAbortedEvent actionAbortedEvent) {
        slotMachine.finishTransaction(BigDecimal.ZERO);
        view.promptForParkingLotNumber();
    }

    @Override
    public void moneyInserted(MoneyInsertedEvent moneyInsertedEvent) {
        LOG.info("Received: MoneyInsertedEvent, InsertedMoney: " + slotMachine.getAmountOfCurrentlyInsertedMoney());
        // TODO money insert
        //TODO: Calculate parking time...
        
        //TODO: Check if valid -> end transaction
        //TODO: Return money
        //TODO: Print console
        //TODO: Finish transaction.
    }

    @Override
    public void shutdownRequested(final ShutdownEvent shutdownEvent) {
        LOG.info("Received request for shutdown...");
        // TODO: Persist current coin level
        // TODO: Persist parking lot allocation

        // TODO: If all ok
        LOG.info("Proceeding with shutdown...");
        view.displayShutdownMessage();
        view.shutdown();
        LOG.info("Shutdown complete...exit");
        appContext.close();
    }
}
