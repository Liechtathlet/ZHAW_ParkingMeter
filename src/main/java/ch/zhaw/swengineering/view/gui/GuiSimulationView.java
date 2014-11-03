package ch.zhaw.swengineering.view.gui;

import java.awt.BorderLayout;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.Date;

import javax.swing.JFrame;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

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

    private JFrame frame;
    /**
     * Creates the GUI.
     */
    private void initGui() {
        LOG.debug("Initialize frame");
        frame = new JFrame("ParkingMeter");

        frame.addWindowListener(this);
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.getContentPane()
                .add(new ParkingMeterPanel(), BorderLayout.CENTER);
        frame.pack();
        frame.setVisible(true);
        LOG.debug("Frame initialized...");
    }

    @Override
    public void run() {
        initGui();
    }

    @Override
    public void promptForParkingLotNumber() {
    }

    @Override
    public void displayParkingLotNumberAndParkingTime(int aParkingLotNumber,
            Date aPaidParkingtime) {
        // TODO Auto-generated method stub

    }

    @Override
    public void promptForMoney(int aParkingLotNumber) {
        // TODO Auto-generated method stub

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
    public void displayAllInformation() {
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
}
