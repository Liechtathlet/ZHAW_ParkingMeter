package ch.zhaw.swengineering.controller;

import java.math.BigDecimal;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Controller;

import ch.zhaw.swengineering.business.ParkingMeter;
import ch.zhaw.swengineering.event.ActionAbortedEvent;
import ch.zhaw.swengineering.event.CoinBoxLevelEnteredEvent;
import ch.zhaw.swengineering.event.MoneyInsertedEvent;
import ch.zhaw.swengineering.event.ParkingLotEnteredEvent;
import ch.zhaw.swengineering.event.ShutdownEvent;
import ch.zhaw.swengineering.event.ViewEventListener;
import ch.zhaw.swengineering.helper.TransactionLogHandler;
import ch.zhaw.swengineering.model.ParkingLotBooking;
import ch.zhaw.swengineering.model.persistence.ParkingLot;
import ch.zhaw.swengineering.model.persistence.SecretActionEnum;
import ch.zhaw.swengineering.slotmachine.controller.IntelligentSlotMachineBackendInteractionInterface;
import ch.zhaw.swengineering.slotmachine.exception.CoinBoxFullException;
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
    private ParkingMeter parkingMeter;

    @Autowired
    private ConfigurableApplicationContext appContext;

    @Autowired
    private IntelligentSlotMachineBackendInteractionInterface slotMachine;

    @Autowired()
    private TransactionLogHandler transactionLog;

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
        ParkingLot parkingLot = parkingMeter
                .getParkingLot(parkingLotEnteredEvent.getParkingLotNumber());

        // Step One: Check if it is a parking lot number
        if (parkingLot != null) {
            processed = true;
            slotMachine.startTransaction();
            view.displayParkingLotNumberAndParkingTime(parkingLot.getNumber(),
                    parkingLot.getPaidUntil());
            view.promptForMoney(parkingLot.getNumber());
        }

        // Step Two: Check if it is a secret number
        try {
            SecretActionEnum actionEnum = parkingMeter
                    .getSecretAction(parkingLotEnteredEvent
                            .getParkingLotNumber());

            switch (actionEnum) {
            case VIEW_ALL_PARKING_CHARGE:
                processed = true;
                view.displayBookedParkingLots(parkingMeter.getParkingLots());
                view.promptForParkingLotNumber();
                break;
            case VIEW_ALL_INFORMATION:
                processed = true;

                view.displayParkingMeterInfo();
                view.displayContentOfCoinBoxes(slotMachine
                        .getCurrentCoinBoxLevel());
                view.displayParkingTimeDefinitions(parkingMeter
                        .getParkingTimeDefinitions());

                // Output: test für Zeitberechnung
                // Output: Display Transaction log

                view.promptForParkingLotNumber();
                break;
            case VIEW_CONTENT_OF_COIN_BOXES:
                processed = true;
                view.displayContentOfCoinBoxes(slotMachine
                        .getCurrentCoinBoxLevel());
                view.promptForParkingLotNumber();
                break;
            case ENTER_NEW_LEVEL_FOR_COIN_BOXES:
                processed = true;
                view.promptForNewCoinBoxLevels(slotMachine
                        .getCurrentCoinBoxLevel());
                break;
            }
        } catch (Exception e) {
            // Nothing to do here..
        }

        // Step Three: Print error if nothing matched
        if (!processed) {
            view.displayErrorParkingLotNumberInvalid();
            view.promptForParkingLotNumber();
        }
    }

    @Override
    public void actionAborted(final ActionAbortedEvent actionAbortedEvent) {
        slotMachine.finishTransaction(slotMachine
                .getAmountOfCurrentlyInsertedMoney());
        view.displayMessageForDrawback();
        view.promptForParkingLotNumber();
    }

    @Override
    public void moneyInserted(MoneyInsertedEvent moneyInsertedEvent) {
        BigDecimal insertedMoney = slotMachine
                .getAmountOfCurrentlyInsertedMoney();
        LOG.info("Received: MoneyInsertedEvent, InsertedMoney: "
                + insertedMoney);

        ParkingLotBooking booking = parkingMeter.calculateBookingForParkingLot(
                moneyInsertedEvent.getParkingLotNumber(), insertedMoney);

        if (booking.isNotEnoughMoney()) {
            view.displayNotEnoughMoneyError();
            view.promptForMoney(moneyInsertedEvent.getParkingLotNumber());
        } else {
            parkingMeter.persistBooking(booking);
            view.increaseInfoBufferSizeTemporarily(2);

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

        LOG.info("Proceeding with shutdown...");
        view.displayShutdownMessage();
        view.shutdown();
        LOG.info("Shutdown complete...exit");
        appContext.close();
    }

    @Override
    public void coinBoxLevelEntered(
            final CoinBoxLevelEnteredEvent coinBoxLevelEnteredEvent) {
        LOG.info("Coin box level entered...");
        try {
            slotMachine.updateCoinLevelInCoinBoxes(coinBoxLevelEnteredEvent
                    .getCoinBoxLevels());
            view.displayContentOfCoinBoxes(slotMachine.getCurrentCoinBoxLevel());
            view.promptForParkingLotNumber();
        } catch (CoinBoxFullException e) {
            view.displayCoinCountTooHigh(e.getCoinValue());
        }
    }
}
