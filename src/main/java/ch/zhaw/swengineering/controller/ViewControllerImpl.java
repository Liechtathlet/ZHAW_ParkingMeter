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
import ch.zhaw.swengineering.model.ParkingLotBooking;
import ch.zhaw.swengineering.model.persistence.ParkingLot;
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
        LOG.debug("User entered parking lot number: "
                + parkingLotEnteredEvent.getParkingLotNumber());

        boolean processed = false;
        ParkingLot parkingLot = parkingMeterController
                .getParkingLot(parkingLotEnteredEvent.getParkingLotNumber());

        // Step One: Check if it is a parking lot number
        if (parkingLot != null) {
            processed = true;
            slotMachine.startTransaction();
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
        slotMachine.finishTransaction(BigDecimal.ZERO);
        view.promptForParkingLotNumber();
    }

    @Override
    public void moneyInserted(MoneyInsertedEvent moneyInsertedEvent) {
        BigDecimal insertedMoney = slotMachine
                .getAmountOfCurrentlyInsertedMoney();
        LOG.info("Received: MoneyInsertedEvent, InsertedMoney: "
                + insertedMoney);

        ParkingLotBooking booking = parkingMeterController
                .calculateBookingForParkingLot(
                        moneyInsertedEvent.getParkingLotNumber(), insertedMoney);

        if (booking.isNotEnoughMoney()) {
            view.displayNotEnoughMoneyError();
            view.promptForMoney(moneyInsertedEvent.getParkingLotNumber());
        } else {
            // TODO: Persist booking
            view.displayParkingLotNumberAndParkingTime(
                    moneyInsertedEvent.getParkingLotNumber(),
                    booking.getPaidTill());
            slotMachine.finishTransaction(booking.getDrawbackMoney());
            view.displayMessageForDrawback();
            view.promptForParkingLotNumber();
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
    public void showAllParkingCharge(ShowAllParkingCharge showAllParkingCharge) {
        // TODO Auto-generated method stub

    }
}
