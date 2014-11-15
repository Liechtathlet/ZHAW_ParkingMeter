package ch.zhaw.swengineering.view.gui;

import java.awt.BorderLayout;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.swing.JFrame;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import ch.zhaw.swengineering.helper.MessageProvider;
import ch.zhaw.swengineering.model.CoinBoxLevel;
import ch.zhaw.swengineering.model.persistence.ParkingLot;
import ch.zhaw.swengineering.slotmachine.exception.CoinBoxFullException;
import ch.zhaw.swengineering.slotmachine.exception.InvalidCoinException;
import ch.zhaw.swengineering.slotmachine.exception.NoTransactionException;
import ch.zhaw.swengineering.view.SimulationView;
import ch.zhaw.swengineering.view.console.ConsoleViewStateEnum;

/**
 * @author Daniel Brun
 * 
 *         Gui implementation of the interface
 *         {@link ch.zhaw.swengineering.view.SimulationView}
 * 
 */
public class GuiSimulationView extends SimulationView implements WindowListener {

    private static final Logger LOG = LogManager
            .getLogger(GuiSimulationView.class);

    @Autowired
    private MessageProvider messageProvider;

    private JFrame frame;
    private ParkingMeterPanel parkingMeterPanel;

    /**
     * Creates the GUI.
     */
    public void startSimulationView() {
        LOG.debug("Initialize frame");
        frame = new JFrame("ParkingMeter");

        // TODO: Change list
        parkingMeterPanel = new ParkingMeterPanel(new ArrayList<BigDecimal>());

        frame.addWindowListener(this);
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        frame.getContentPane().add(parkingMeterPanel, BorderLayout.CENTER);
        frame.pack();
        frame.setVisible(true);
        LOG.debug("Frame initialized...");

    }

    @Override
    public void run() {
        // Nothing to do here...
    }

    @Override
    public void windowDeactivated(final WindowEvent e) {
        // Not used.
    }

    @Override
    public void windowDeiconified(final WindowEvent e) {
        // Not used.
    }

    @Override
    public void windowIconified(final WindowEvent e) {
        // Not used.
    }

    @Override
    public void windowOpened(final WindowEvent e) {
        // Not used.
    }

    /* ******** View-Implementation Methods ******** */
    @Override
    public void promptForParkingLotNumber() {
        print("view.enter.parkinglotnumber", true);

        Integer parkingLotNumber = parkingMeterPanel.readInteger();

        notifyForParkingLotNumberEntered(parkingLotNumber);
    }

    /**
     * Prints a text with the given.
     * 
     * @param aKey
     *            the key of the text to output.
     * @param prompt
     *            True if the printed text is an input prompt
     * @param arguments
     *            The arguments for the message.
     */
    public final void print(final String aKey, final boolean prompt,
            final Object... arguments) {
        String message = MessageFormat.format(messageProvider.get(aKey).trim(),
                arguments);

        if (prompt) {
            message += messageProvider.get("view.prompt.separator");
        }

        parkingMeterPanel.print(message, prompt);
    }

    @Override
    public void displayAllInformation() {
        // TODO Auto-generated method stub

    }

    @Override
    public void displayMessageForDrawback() {
        // TODO Auto-generated method stub

    }

    @Override
    public void promptForNewCoinBoxLevels(
            List<CoinBoxLevel> someCurrentCoinBoxLevels) {
        // TODO Auto-generated method stub

    }

    @Override
    public void displayParkingLotNumberAndParkingTime(int aParkingLotNumber,
            Date aPaidParkingtime) {
        // TODO Auto-generated method stub

    }

    @Override
    public void promptForMoney(int aParkingLotNumber) {
        print("view.enter.coins", true, aParkingLotNumber);
        /*String input = readFromConsole();

        // if input == null: no input was provided or another event occurred.
        if (input != null) {
            boolean error = false;
            boolean drawback = false;

            try {
                parseAndInsertCoins(input);
            } catch (CoinBoxFullException e) {
                error = true;
                drawback = true;
                LOG.error("Received exception "
                        + "from slot machine: coin box is full!", e);

                if (e.isAllCoinBoxesFull()) {
                    print("view.slot.machine.coin.box.full", false,
                            e.getCoinValue());
                } else {
                    print("view.slot.machine.coin.box.single.full", false,
                            e.getCoinValue());
                }

            } catch (NoTransactionException e) {
                error = true;
                drawback = true;

                // This should not occur!
                LOG.error("Received exception "
                        + "from slot machine: no transaction!", e);
            } catch (InvalidCoinException e) {
                error = true;
                drawback = true;

                StringBuilder coinString = new StringBuilder();

                for (BigDecimal validCoin : e.getValidCoins()) {
                    coinString.append(validCoin);
                    coinString.append(", ");
                }

                LOG.error("Received exception "
                        + "from slot machine: invalid coin!", e);
                print("view.slot.machine.coin.invalid", false, coinString);
            } catch (NumberFormatException e) {
                error = true;
                print("view.slot.machine.format.invalid", false);
            }

            if (error) {
                if (drawback) {
                    roleBackTransaction();
                }
            } else {
                // Reset view state if operation was successful.
                setViewState(ConsoleViewStateEnum.INIT);
                notifyForMoneyInserted(storeParkingLotNumber);
            }
        }*/
    }

    @Override
    public void displayParkingLotNumberInvalid() {
        // TODO Auto-generated method stub

    }

    @Override
    public void displayShutdownMessage() {
        // TODO Auto-generated method stub

    }

    @Override
    public void shutdown() {
        frame.dispose();
    }

    @Override
    public void windowActivated(final WindowEvent e) {
        // Not used.
    }

    @Override
    public void windowClosed(WindowEvent e) {
        // Not used.
    }

    @Override
    public void windowClosing(final WindowEvent e) {
        notifyForShutdownRequested();
    }

    @Override
    public void displayBookedParkingLots(List<ParkingLot> parkingLots) {
        // TODO Auto-generated method stub

    }
}
