package ch.zhaw.swengineering.controller;

import ch.zhaw.swengineering.business.ParkingMeter;
import ch.zhaw.swengineering.event.*;
import ch.zhaw.swengineering.helper.TransactionLogHandler;
import ch.zhaw.swengineering.model.CoinBoxLevel;
import ch.zhaw.swengineering.model.ParkingLotBooking;
import ch.zhaw.swengineering.model.persistence.ParkingLot;
import ch.zhaw.swengineering.model.persistence.SecretActionEnum;
import ch.zhaw.swengineering.slotmachine.controller.IntelligentSlotMachineBackendInteractionInterface;
import ch.zhaw.swengineering.slotmachine.exception.CoinBoxFullException;
import ch.zhaw.swengineering.view.SimulationViewInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

/**
 * @author Daniel Brun Controller for the view.
 */
@Component
public class ViewControllerImpl implements ViewController, ViewEventListener {

    @Autowired
    private SimulationViewInterface view;

    @Autowired
    private ParkingMeter parkingMeter;

    @Autowired
    private ConfigurableApplicationContext appContext;

    @Autowired
    private IntelligentSlotMachineBackendInteractionInterface slotMachine;

    @Autowired
    private TransactionLogHandler transactionLog;

    @Override
    public final void start() {
        transactionLog.write("Starting ParkingMeter App.");

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
        transactionLog.write(String.format("Parking lot number %d entered.",
                parkingLotEnteredEvent.getParkingLotNumber()));

        boolean processed = false;
        ParkingLot parkingLot = parkingMeter
                .getParkingLot(parkingLotEnteredEvent.getParkingLotNumber());

        // Step One: Check if it is a parking lot number
        if (parkingLot != null) {
            transactionLog.write("Recognized as valid parking lot number.");
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

            transactionLog.write(String.format("Recognized secret code %s", actionEnum));

            switch (actionEnum) {
            case VIEW_ALL_PARKING_CHARGE:
                processed = true;
                view.displayBookedParkingLots(parkingMeter.getParkingLots());
                view.promptForParkingLotNumber();
                break;
            case VIEW_ALL_INFORMATION:
                processed = true;

                view.displayAllInformation(
                        slotMachine.getCurrentCoinBoxLevel(),
                        parkingMeter.getParkingTimeDefinitions(),
                        new Date(),
                        parkingMeter.getParkingLots(),
                        parkingMeter.getParkingTimeTable());

                view.promptForParkingLotNumber();
                break;
            case VIEW_CONTENT_OF_COIN_BOXES:
                processed = true;
                view.displayContentOfCoinBoxes(slotMachine
                        .getCurrentCoinBoxLevel());
                view.promptForParkingLotNumber();
                break;
            case VIEW_ALL_TRANSACTION_LOGS:
                processed = true;
                view.displayAllTransactionLogs();
                view.promptForParkingLotNumber();
                break;
            case VIEW_LAST_24_HOURS_OF_TRANSACTION_LOG:
                processed = true;
                view.displayLast24HoursOfTransactionLog();
                view.promptForParkingLotNumber();
                break;
            case VIEW_N_TRANSACTION_LOG_ENTRIES:
                processed = true;
                view.promptForNumberOfTransactionLogEntriesToShow();
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
            transactionLog.write("Invalid parking lot number.");
            view.displayErrorParkingLotNumberInvalid();
            view.promptForParkingLotNumber();
        }
    }

    @Override
    public void actionAborted(final ActionAbortedEvent actionAbortedEvent) {
        transactionLog.write("Action aborted.");
        slotMachine.finishTransaction(slotMachine
                .getAmountOfCurrentlyInsertedMoney());
        view.displayMessageForDrawback();
        transactionLog.write("Action aborted.");
        slotMachine.finishTransaction(BigDecimal.ZERO);
        view.promptForParkingLotNumber();
    }

    @Override
    public void moneyInserted(MoneyInsertedEvent moneyInsertedEvent) {
        BigDecimal insertedMoney = slotMachine
                .getAmountOfCurrentlyInsertedMoney();
        int parkingLotNumber = moneyInsertedEvent.getParkingLotNumber();
        Map<BigDecimal, Integer> insertedCoins = slotMachine.getInsertedCoins();

        transactionLog.write(String.format("Money %s inserted for parking lot %s",
                insertedMoney, parkingLotNumber));

        ParkingLotBooking booking = parkingMeter.calculateBookingForParkingLot(
                parkingLotNumber, insertedMoney);

        if (booking.isNotEnoughMoney()) {
            transactionLog.write("Amount is invalid. Not enough money.");
            view.displayNotEnoughMoneyError();
            view.promptForMoney(parkingLotNumber);
        } else {
            Date paidTill = booking.getPaidTill();

            transactionLog.write(String.format(
                    "Amount is valid. Saving new booking of parking lot %d till %s",
                    parkingLotNumber, paidTill));
            
            parkingMeter.persistBooking(booking);
            slotMachine.finishTransaction(booking.getDrawbackMoney());
            
            int bufferSize = 2;

            if (slotMachine.hasDrawback()) {
                bufferSize++;
            }

            view.increaseInfoBufferSizeTemporarily(bufferSize);
            view.displayParkingLotPayment(
                    moneyInsertedEvent.getParkingLotNumber(), insertedCoins);

            if (slotMachine.hasDrawback()) {
                transactionLog.write("Disbursing drawback.");
            }

            view.displayMessageForDrawback();

            view.displayParkingLotNumberAndParkingTime(
                    moneyInsertedEvent.getParkingLotNumber(),
                    booking.getPaidTill());
            view.promptForParkingLotNumber();
        }
    }

    @Override
    public void shutdownRequested(final ShutdownEvent shutdownEvent) {
        transactionLog.write("Shutting app down.");

        view.displayShutdownMessage();
        view.shutdown();

        transactionLog.write("Shutdown complete. Exit app now.");

        appContext.close();
    }

    @Override
    public void coinBoxLevelEntered(
            final CoinBoxLevelEnteredEvent coinBoxLevelEnteredEvent) {

        transactionLog.write("New coin box level entered.");

        try {
            slotMachine.updateCoinLevelInCoinBoxes(coinBoxLevelEnteredEvent
                    .getCoinBoxLevels());
            view.displayContentOfCoinBoxes(slotMachine.getCurrentCoinBoxLevel());

            for (CoinBoxLevel coinBoxLevel : coinBoxLevelEnteredEvent.getCoinBoxLevels()) {
                transactionLog.write(String.format("Level is valid. Coin box for %s updated to %d",
                        coinBoxLevel.getCoinValue(), coinBoxLevel.getCurrentCoinCount()));
            }

            view.promptForParkingLotNumber();
        } catch (CoinBoxFullException e) {
            BigDecimal coinValue = e.getCoinValue();

            transactionLog.write(String.format("Level is invalid. Amount of coin box %s is to high.",
                    coinValue));

            view.displayCoinCountTooHigh(coinValue);
        }
    }

    @Override
    public void numberOfTransactionLogEntriesToShowEntered(
            NumberOfTransactionLogEntriesToShowEvent event) {
        transactionLog.write(String.format("Showing %d of transaction log entries.", event.getNumber()));

        view.displayNTransactionLogEntries(event.getNumber());
        view.promptForParkingLotNumber();
    }
}
