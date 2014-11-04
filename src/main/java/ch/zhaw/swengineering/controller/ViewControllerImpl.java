package ch.zhaw.swengineering.controller;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Controller;

import ch.zhaw.swengineering.event.ActionAbortedEvent;
import ch.zhaw.swengineering.event.MoneyInsertedEvent;
import ch.zhaw.swengineering.event.ParkingLotEnteredEvent;
import ch.zhaw.swengineering.event.SecretCodeEnteredEvent;
import ch.zhaw.swengineering.event.ShutdownEvent;
import ch.zhaw.swengineering.event.ViewEventListener;
import ch.zhaw.swengineering.model.ParkingLot;
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
    private ConfigurableApplicationContext appContext;

    @Override
    public final void start() {
        LOG.info("Starting controller...");

        // Register event listener.
        view.addViewEventListener(this);

        // Start simulation view.
        view.startSimulationView();

        // Start process
        view.promptForParkingLotNumber();
        view.promptForMoneyEntered();
    }
		// Start simulation view.
		view.startSimulationView();

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

    @Override
    public void actionAborted(final ActionAbortedEvent actionAbortedEvent) {
        // TODO Action aborted
        view.promptForParkingLotNumber();
    }

    @Override
    public void moneyInserted(MoneyInsertedEvent moneyInsertedEvent) {
        // TODO money insert
        view.promptForMoneyEntered();
    }
	private void handleSecretAction(SecretActionEnum secretAction) {
		switch (secretAction) {
		case VIEW_ALL_INFORMATION:
			view.displayAllInformation();
			break;
		default:
			throw new IllegalArgumentException();
		}
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

	@Override
	public void secretCodeEntered(SecretCodeEnteredEvent SecretCodeEnteredEvent) {
		// TODO Auto-generated method stub
		
	}
}
