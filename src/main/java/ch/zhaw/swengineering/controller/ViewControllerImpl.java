package ch.zhaw.swengineering.controller;

import java.math.BigDecimal;

import ch.zhaw.swengineering.business.ParkingMeter;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Controller;

import ch.zhaw.swengineering.event.ActionAbortedEvent;
import ch.zhaw.swengineering.event.CoinBoxLevelEnteredEvent;
import ch.zhaw.swengineering.event.MoneyInsertedEvent;
import ch.zhaw.swengineering.event.ParkingLotEnteredEvent;
import ch.zhaw.swengineering.event.ShowAllParkingCharge;
import ch.zhaw.swengineering.event.ShutdownEvent;
import ch.zhaw.swengineering.event.ViewEventListener;
import ch.zhaw.swengineering.model.ParkingLotBooking;
import ch.zhaw.swengineering.model.persistence.ParkingLot;
import ch.zhaw.swengineering.model.persistence.SecretActionEnum;
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
    private ParkingMeter parkingMeter;

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
                parkingMeter.callAllBookedParkingLots();
                // 0: Modell für Ausgabe erstellen.
                // 1: Controller aufrufen -> Parkplätze ermitteln und in Modell
                // konvertieren
                // 2: Rückgabe an View: view.display...
                break;
            case VIEW_ALL_INFORMATION:
                processed = true;
                view.displayAllInformation();
                break;
            case ENTER_NEW_LEVEL_FOR_COIN_BOXES:
                view.promptForNewCoinBoxLevels(slotMachine
                        .getCurrentCoinBoxLevel());
                break;
            }
        } catch (Exception e) {
            // Nothing to do here..
        }

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

        ParkingLotBooking booking = parkingMeter.calculateBookingForParkingLot(
                moneyInsertedEvent.getParkingLotNumber(), insertedMoney);

        if (booking.isNotEnoughMoney()) {
            view.displayNotEnoughMoneyError();
            view.promptForMoney(moneyInsertedEvent.getParkingLotNumber());
        } else {
            parkingMeter.persistBooking(booking);
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
    public void showAllParkingCharge(ShowAllParkingCharge showAllParkingCharge) {
        // TODO Auto-generated method stub

    }

    @Override
    public void coinBoxLevelEntered(
            CoinBoxLevelEnteredEvent coinBoxLevelEnteredEvent) {
        LOG.info("Coin box level entered...");
        slotMachine.updateCoinLevelInCoinBoxes(coinBoxLevelEnteredEvent
                .getCoinBoxLevels());
        //TODO: Ouput coin box levels (secret code method)
        view.promptForParkingLotNumber();
    }
}
