package ch.zhaw.swengineering.view.console;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import ch.zhaw.swengineering.helper.ConfigurationProvider;
import ch.zhaw.swengineering.helper.MessageProvider;
import ch.zhaw.swengineering.model.persistence.ParkingLot;
import ch.zhaw.swengineering.model.persistence.ParkingTimeDefinition;
import ch.zhaw.swengineering.model.persistence.ParkingTimeDefinitions;
import ch.zhaw.swengineering.slotmachine.exception.CoinBoxFullException;
import ch.zhaw.swengineering.slotmachine.exception.InvalidCoinException;
import ch.zhaw.swengineering.slotmachine.exception.NoTransactionException;
import ch.zhaw.swengineering.view.SimulationView;
import ch.zhaw.swengineering.view.ViewStateEnum;
import ch.zhaw.swengineering.view.helper.ViewOutputMode;

/**
 * @author Daniel Brun Console implementation of the interface
 *         {@link ch.zhaw.swengineering.view.SimulationView}.
 */
public class ConsoleSimulationView extends SimulationView {

    /**
     * The 'command' / string which is used to abort the current action.
     */
    private static final String ABORT_COMMAND = "x";

    /**
     * The 'command' / string which is used to exit the ParkingMeter.
     */
    private static final String EXIT_COMMAND = "exit";

    /**
     * Thread-Sleep-Time.
     */
    private static final int SLEEP_TIME = 100;

    private static final String COIN_SPLITTER = " ";

    /**
     * The Logger.
     */
    private static final Logger LOG = LogManager
            .getLogger(ConsoleSimulationView.class);

    @Autowired
    private MessageProvider messageProvider;

    @Autowired
    private BufferedReader reader;

    @Autowired
    @Qualifier("parkingTimeDef")
    private ConfigurationProvider parkingTimeConfigurationProvider;

    private List<ParkingLot> parkingLots;

    @Autowired
    private PrintStream writer;

    private Date now = new Date();

    /*
     * (non-Javadoc)
     * 
     * @see ch.zhaw.swengineering.view.SimulationView#init()
     */
    @Override
    protected void init() {
        // Nothing to do here..
    }

    /* ********* Refactor from here ********** */
    @Override
    public void displayAllInformation() {
        // TODO: Der View-State ist nicht notwendig -> Parameter -> Output
        
    }

    public void executeActionsForStateViewingAllInformation() {
        print("view.all.information.title.template", ViewOutputMode.LARGE_INFO,
                2, messageProvider.get("view.parking.time.def").trim());

        // TODO: View sollte nicht direkt auf model zugreifen
        ParkingTimeDefinitions parkingTimes = (ParkingTimeDefinitions) parkingTimeConfigurationProvider
                .get();
        int i = 1;
        int lastMinuteCount = 0;
        BigDecimal lastPrice = new BigDecimal(0);
        for (ParkingTimeDefinition parkingTime : parkingTimes
                .getParkingTimeDefinitions()) {
            for (int j = 0; j < parkingTime.getCountOfSuccessivePeriods(); j++) {
                BigDecimal newPrice = lastPrice.add(parkingTime
                        .getPricePerPeriod());
                int newMinuteCount = lastMinuteCount
                        + parkingTime.getDurationOfPeriodInMinutes();
                print("view.all.information.parking.time",
                        ViewOutputMode.LARGE_INFO, i, formatPrice(lastPrice),
                        formatPrice(newPrice), newMinuteCount);
                lastPrice = newPrice;
                lastMinuteCount = newMinuteCount;
                i++;
            }
        }

        setViewState(ViewStateEnum.ENTERING_PARKING_LOT);
    }

  

    private String formatPrice(BigDecimal price) {
        // TODO: Auslagern in Helper
        price = price.setScale(2, BigDecimal.ROUND_DOWN);
        DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(2);
        df.setMinimumFractionDigits(2);
        df.setGroupingUsed(false);
        return df.format(price);
    }

    /* ******** Refactoring bis hier notwendig ***** */

    /**
     * Reads a string from the console and checks it for default actions.
     * Default actions are shutdown of the ParkingMeter and abort of the current
     * action / flow.
     * 
     * @return the string. Null if an event was thrown, an empty string if no
     *         input could be red.
     */
    private String readFromConsole() {
        String line = "";

        try {
            line = reader.readLine();
        } catch (IOException e) {
            LOG.error("Failed to read from console!", e);
        }

        if (line != null) {
            line = line.trim();

            if (line.toLowerCase().equals(ABORT_COMMAND)) {
                line = null;
                setViewState(ViewStateEnum.INIT);
                rolebackTransaction();
                notifyForActionAborted();
            } else if (line.toLowerCase().equals(EXIT_COMMAND)) {
                line = null;
                setViewState(ViewStateEnum.INIT);
                rolebackTransaction();
                notifyForShutdownRequested();
            }
        }

        return line;
    }

    /* ********** Impl methods of superclass ********** */

    @Override
    protected void print(final String aKey, final ViewOutputMode aMode,
            final Object... arguments) {
        if (aMode.equals(ViewOutputMode.PROMPT)) {
            writer.print(MessageFormat.format(messageProvider.get(aKey).trim()
                    + messageProvider.get("view.prompt.separator"), arguments));
        } else {
            writer.println(MessageFormat.format(messageProvider.get(aKey)
                    .trim(), arguments));
        }
    }

    @Override
    public Integer readInteger() {
        // TODO: Evtl. clean up.
        Integer enteredInteger = null;
        String input = readFromConsole();

        if (input != null) {
            try {
                enteredInteger = new Integer(input);
            } catch (NumberFormatException e) {
                print("view.slot.machine.format.invalid", ViewOutputMode.ERROR);
            }
        }

        return enteredInteger;
    }

    @Override
    public void executeActionsForStateDroppingInMoney() {
        print("view.enter.coins", ViewOutputMode.PROMPT,
                dataStore.getParkingLotNumber());
        String input = readFromConsole();

        // if input == null: no input was provided or another event occurred.
        if (input != null) {
            try {
                String[] split = input.split(COIN_SPLITTER);

                for (String coin : split) {
                    BigDecimal coinDec = new BigDecimal(coin.trim());

                    slotMachine.insertCoin(coinDec);
                }

                setViewState(ViewStateEnum.INIT);
                notifyForMoneyInserted(dataStore.getParkingLotNumber());
            } catch (CoinBoxFullException e) {
                handleCoinBoxFullException(e);
            } catch (NoTransactionException e) {
                handleNoTransactionException(e);
            } catch (InvalidCoinException e) {
                handleInvalidCoinException(e);
            } catch (NumberFormatException e) {
                handleNumberFormatException(e);
            }
        }
    }
}
