/**
 * 
 */
package ch.zhaw.swengineering.view.gui;

import java.awt.BorderLayout;

import javax.swing.JFrame;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import ch.zhaw.swengineering.view.SimulationView;

/**
 * @author Daniel Brun
 *
 * Gui implementation of the interface {@link ch.zhaw.swengineering.view.SimulationView}
 * 
 */
public class GuiSimulationView extends SimulationView {

	private static final Logger LOG = LogManager.getLogger(GuiSimulationView.class);
	
	/**
	 * Creates the GUI.
	 */
	private void initGui() {
		JFrame frame = new JFrame("ParkingMeter");
		
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().add(new ParkingMeterPanel(), BorderLayout.CENTER);
		frame.pack();
		frame.setVisible(true);
	}
	
	@Override
	public void run() {
		initGui();
	}

	@Override
	public void showParkingLotMessage() {}
}
