package ch.zhaw.swengineering.controller;

import java.math.BigDecimal;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Controller;

import ch.zhaw.swengineering.event.ActionAbortedEvent;
import ch.zhaw.swengineering.event.MoneyInsertedEvent;
import ch.zhaw.swengineering.event.ParkingLotEnteredEvent;
import ch.zhaw.swengineering.event.ShowAllParkingCharge;
import ch.zhaw.swengineering.event.ShutdownEvent;
import ch.zhaw.swengineering.event.ViewEventListener;
import ch.zhaw.swengineering.model.ParkingLot;
import ch.zhaw.swengineering.model.SecretActionEnum;
import ch.zhaw.swengineering.slotmachine.controller.IntelligentSlotMachineBackendInteractionInterface;
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
		LOG.debug("User entered parking lot number: " + parkingLotNumber);

		boolean processed = false;

		// Step One: Check if it is a parking lot number
		ParkingLot parkingLot = parkingMeterController
				.getParkingLot(parkingLotNumber);
		if (parkingLot != null) {
			processed = true;
			slotMachine.startTransaction();
			view.displayParkingLotNumberAndParkingTime(parkingLot.number,
					parkingLot.paidUntil);
			view.promptForMoney(parkingLot.number);
		}

		// Step Two: Check if it is a secret number
		SecretActionEnum secretAction = null;
		try {
			secretAction = parkingMeterController
					.getSecretAction(parkingLotNumber);
		} catch (Exception e) {
			// TODO sl: what to do when no secret action can be found? I would
			// just ignore the exception, which means no secret codes can be
			// handled.
		}
		if (secretAction != null) {
			handleSecretAction(secretAction);
		}

		// Step Three: Print error if nothing matched
		if (!processed) {
			view.displayParkingLotNumberInvalid();
			view.promptForParkingLotNumber();
		}
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
	public void actionAborted(final ActionAbortedEvent actionAbortedEvent) {
		slotMachine.finishTransaction(BigDecimal.ZERO);
		view.promptForParkingLotNumber();
	}

	@Override
	public void moneyInserted(MoneyInsertedEvent moneyInsertedEvent) {
		LOG.info("Received: MoneyInsertedEvent, InsertedMoney: "
				+ slotMachine.getAmountOfCurrentlyInsertedMoney());
		// TODO money insert
		// TODO: Calculate parking time...

		// TODO: Check if valid -> end transaction
		// TODO: Return money
		// TODO: Print console
		// TODO: Finish transaction.
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
	public void showAllParkingCharge(ShowAllParkingCharge showAllParkingCharge) {
		LOG.info("Received request for show all ParkingMeter Charge...");
		// TODO All TODO -

	}
}
