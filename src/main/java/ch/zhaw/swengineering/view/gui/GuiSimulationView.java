package ch.zhaw.swengineering.view.gui;

import java.awt.BorderLayout;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.JFrame;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import ch.zhaw.swengineering.helper.MessageProvider;
import ch.zhaw.swengineering.model.CoinBoxLevel;
import ch.zhaw.swengineering.model.persistence.ParkingLot;
import ch.zhaw.swengineering.view.SimulationView;

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

    /*
     * (non-Javadoc)
     * 
     * @see ch.zhaw.swengineering.view.SimulationView#init()
     */
    @Override
    protected final void init() {
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
    public final void print(final String aKey, final boolean prompt,
            final Object... arguments) {
        String message = MessageFormat.format(messageProvider.get(aKey).trim(),
                arguments);

        if (prompt) {
            message += messageProvider.get("view.prompt.separator");
            parkingMeterPanel.printPrompt(message);
        } else {
            parkingMeterPanel.printErrorOrInfo(message);
        }
    }

    @Override
    public Integer readInteger() {
        return parkingMeterPanel.readInteger();
    }

    /* *** TODO: To CleanUp *** */
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
    public void shutdown() {
        super.shutdown();
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

    @Override
    protected void executeActionsForStateDroppingInMoney() {
        // TODO Auto-generated method stub

    }

    @Override
    protected void executeActionsForStateViewingAllInformation() {
        // TODO Auto-generated method stub

    }

    @Override
    protected void executeActionsForStateDisplayBookedParkingLots() {
        // TODO Auto-generated method stub

    }

    @Override
    protected void executeActionsForStateEnteringCoinBoxLevels() {
        // TODO Auto-generated method stub

    }
}
