package ch.zhaw.swengineering.view.console;

import java.io.BufferedReader;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Date;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.format.datetime.DateFormatter;

import ch.zhaw.swengineering.event.ParkingLotEnteredEvent;
import ch.zhaw.swengineering.event.ViewEventListener;
import ch.zhaw.swengineering.helper.MessageProvider;
import ch.zhaw.swengineering.view.SimulationView;

/**
 * @author Daniel Brun Console implementation of the interface
 *         {@link ch.zhaw.swengineering.view.SimulationView}.
 */
public class ConsoleSimulationView extends SimulationView {

    /**
     * Date-Format.
     */
    private static final String DATE_FORMAT = "dd.MM.YYYY HH:mm";

    /**
     * Thread-Sleep-Time.
     */
    private static final int SLEEP_TIME = 100;

    /**
     * The Logger.
     */
    private static final Logger LOG = LogManager
            .getLogger(ConsoleSimulationView.class);

    @Autowired
    private MessageProvider messageProvider;

    @Autowired
    private BufferedReader reader;

    private ConsoleViewStateEnum viewState;

    private boolean run;

    private DateFormatter dateFormatter;

    /**
     * Creates a new instance of this class and sets the initial state.
     */
    public ConsoleSimulationView() {
        super();
        viewState = ConsoleViewStateEnum.INIT;

        dateFormatter = new DateFormatter(DATE_FORMAT);
    }

    @Override
    public final void run() {
        if (!run) {
            LOG.info("Running console simulation");
            run = true;

            while (run) {

                switch (viewState) {
                case ENTERING_PARKING_LOT:
                    executeActionsForStateEnteringParkingLotNumber();
                    break;
                case DROPPING_IN_MONEY:
                    // TODO:Implement
                    break;
                case INIT:
                default:
                    try {
                        Thread.sleep(SLEEP_TIME);
                    } catch (InterruptedException e) {
                        LOG.error("Sleep in loop failed.", e);
                    }
                    break;
                }
            }
        }
    }

    @Override
    public final void promptForParkingLotNumber() {
        viewState = ConsoleViewStateEnum.ENTERING_PARKING_LOT;
    }

    @Override
    public final void promptForMoney() {
        viewState = ConsoleViewStateEnum.DROPPING_IN_MONEY;
    }

    @Override
    public final void displayParkingLotNumberAndParkingTime(
            final int aParkingLotNumber, final Date aPaidParkingTime) {
        String formattedDate = dateFormatter.print(aPaidParkingTime,
                LocaleContextHolder.getLocale());

        printToConsole("view.info.parkingTime", false, aParkingLotNumber,
                formattedDate);
    }

    /**
     * Executes all necessary actions, which are required in the state
     * 'EnteringParkingLotNumber'.
     */
    public final void executeActionsForStateEnteringParkingLotNumber() {
        printToConsole("view.enter.parkinglotnumber", true);
        String input = readFromConsole();

        Integer parkingLotNumber = null;

        try {
            parkingLotNumber = new Integer(input);
        } catch (NumberFormatException e) {
            // TODO: Fehlermeldung aus UseCase kann nicht angezeigt werden:
            // Parkplatznummern sind frei wählbar -> UC und Story anpassen
            printToConsole("view.enter.invalid", false);
        }

        if (parkingLotNumber != null) {
            notifyForParkingLotNumberEntered(parkingLotNumber);
            viewState = ConsoleViewStateEnum.INIT;
        }
    }

    /**
     * Notifies all attached listeners about the entered parking lot.
     * 
     * @param parkingLotNumber
     *            The parking lot number.
     */
    private void notifyForParkingLotNumberEntered(final int parkingLotNumber) {
        ParkingLotEnteredEvent event = new ParkingLotEnteredEvent(this,
                parkingLotNumber);

        for (ViewEventListener listener : eventListeners) {
            listener.parkingLotEntered(event);
        }
    }

    /**
     * Prints a text with the given key to the console.
     * 
     * @param aKey
     *            the key of the text to output.
     * @param prompt
     *            True if the printed text is an input prompt
     * @param arguments
     *            The arguments for the message.
     */
    private void printToConsole(final String aKey, final boolean prompt,
            final Object... arguments) {
        if (prompt) {
            System.out.print(MessageFormat.format(messageProvider.get(aKey)
                    .trim() + ": ", arguments));
        } else {
            System.out.println(MessageFormat.format(messageProvider.get(aKey)
                    .trim(), arguments));
        }
    }

    /**
     * Reads a string from the console and checks it for default actions.
     * Default actions are shutdown of the ParkingMeter and abort of the current
     * action / flow.
     * 
     * @return the string.
     */
    private String readFromConsole() {
        String line = null;

        try {
            line = reader.readLine();
        } catch (IOException e) {
            LOG.error("Failed to read from console!", e);
        }

        // TODO: Process / Parse / Checke here for Shutdown and abort.

        return line;
    }

    /**
     * @return the view state.
     */
    public final ConsoleViewStateEnum getViewState() {
        return viewState;
    }
}
