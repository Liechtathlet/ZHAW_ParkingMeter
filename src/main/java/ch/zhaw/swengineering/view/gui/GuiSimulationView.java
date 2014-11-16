package ch.zhaw.swengineering.view.gui;

import java.awt.BorderLayout;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.text.MessageFormat;
import java.util.List;

import javax.swing.JFrame;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import ch.zhaw.swengineering.helper.MessageProvider;
import ch.zhaw.swengineering.model.CoinBoxLevel;
import ch.zhaw.swengineering.model.persistence.ParkingLot;
import ch.zhaw.swengineering.slotmachine.controller.IntelligentSlotMachineUserInteractionInterface;
import ch.zhaw.swengineering.view.SimulationView;
import ch.zhaw.swengineering.view.ViewStateEnum;
import ch.zhaw.swengineering.view.helper.ViewOutputMode;

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

    @Autowired
    private IntelligentSlotMachineUserInteractionInterface slotMachine;

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

        parkingMeterPanel = new ParkingMeterPanel();
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

    /* ******** View-Implementation Methods ******** */

    @Override
    public final void print(final String aKey, final ViewOutputMode aMode,
            final Object... arguments) {
        String message = MessageFormat.format(messageProvider.get(aKey).trim(),
                arguments);

        if (aMode.equals(ViewOutputMode.PROMPT)) {
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

    @Override
    protected void executeActionsForStateDroppingInMoney() {
        print("view.enter.coins", ViewOutputMode.PROMPT, dataStore.getParkingLotNumber());

        parkingMeterPanel.waitForOK();
        
        setViewState(ViewStateEnum.INIT);
        notifyForMoneyInserted(dataStore.getParkingLotNumber());
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
    public void displayAllInformation() {
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
}
