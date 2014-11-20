package ch.zhaw.swengineering.view.gui;

import java.awt.BorderLayout;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.PrintStream;
import java.text.MessageFormat;

import javax.swing.JFrame;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import ch.zhaw.swengineering.slotmachine.controller.IntelligentSlotMachineUserInteractionInterface;
import ch.zhaw.swengineering.view.SimulationView;
import ch.zhaw.swengineering.view.ViewStateEnum;
import ch.zhaw.swengineering.view.gui.listeners.ActionAbortListener;
import ch.zhaw.swengineering.view.helper.ViewOutputMode;

/**
 * @author Daniel Brun
 * 
 *         Gui implementation of the interface
 *         {@link ch.zhaw.swengineering.view.SimulationView}
 * 
 */
public class GuiSimulationView extends SimulationView implements
        WindowListener, ActionAbortListener {

    private static final Logger LOG = LogManager
            .getLogger(GuiSimulationView.class);

    @Autowired
    private IntelligentSlotMachineUserInteractionInterface slotMachine;

    @Autowired
    private PrintStream writer;

    private JFrame frame;
    private ParkingMeterPanel parkingMeterPanel;
    private SlotMachinePanel slotMachinePanel;

    /*
     * (non-Javadoc)
     * 
     * @see ch.zhaw.swengineering.view.SimulationView#init()
     */
    @Override
    protected final void init() {
        LOG.debug("Initialize frame");
        frame = new JFrame("ParkingMeter");

        parkingMeterPanel = new ParkingMeterPanel(this);
        slotMachinePanel = new SlotMachinePanel(parkingMeterPanel, slotMachine,
                this);

        frame.addWindowListener(this);
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        frame.getContentPane().add(parkingMeterPanel, BorderLayout.CENTER);
        frame.getContentPane().add(slotMachinePanel, BorderLayout.EAST);
        frame.pack();
        frame.setVisible(true);
        LOG.debug("Frame initialized...");

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

    /* ******** View-Implementation Methods ******** */

    @Override
    public final void print(final String aKey, final ViewOutputMode aMode,
            final Object... arguments) {
        String message = MessageFormat.format(messageProvider.get(aKey).trim(),
                arguments);

        if (aMode.equals(ViewOutputMode.PROMPT)) {
            message += messageProvider.get("view.prompt.separator");
            parkingMeterPanel.printPrompt(message);
        } else if (aMode.equals(ViewOutputMode.ERROR)) {
            parkingMeterPanel.printError(message);
        } else if (aMode.equals(ViewOutputMode.INFO)) {
            parkingMeterPanel.printInfo(message);
        } else if (aMode.equals(ViewOutputMode.LARGE_INFO)) {
            // Print to another output
            writer.println(message);
        }
    }

    @Override
    public Integer readInteger() {
        return parkingMeterPanel.readInteger();
    }

    @Override
    protected void executeActionsForStateDroppingInMoney() {
        parkingMeterPanel.setNumberBlockBlocked(true);
        print("view.enter.coins", ViewOutputMode.PROMPT,
                dataStore.getParkingLotNumber());

        if (parkingMeterPanel.waitForOK()) {

            setViewState(ViewStateEnum.INIT);
            notifyForMoneyInserted(dataStore.getParkingLotNumber());
        }
        parkingMeterPanel.setNumberBlockBlocked(false);
    }

    @Override
    public void shutdown() {
        parkingMeterPanel.interruptWait();
        super.shutdown();
        frame.dispose();
    }

    @Override
    public void calledAbort() {
        notifyForActionAborted();
    }

    @Override
    protected void clearOutput() {
        parkingMeterPanel.clearOutput();
    }
}
