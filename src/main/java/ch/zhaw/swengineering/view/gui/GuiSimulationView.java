/**
 * 
 */
package ch.zhaw.swengineering.view.gui;

import ch.zhaw.swengineering.view.SimulationView;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.awt.*;

/**
 * @author Daniel Brun
 *
 * Gui implementation of the interface {@link ch.zhaw.swengineering.view.SimulationView}
 * 
 * TODO: Find solution for console / gui switch.
 */
//@Component("gui")
public class GuiSimulationView extends SimulationView {

	private static final Logger LOG = LogManager.getLogger(GuiSimulationView.class);
	
	/**
	 * Creates the GUI.
	 */
	private void initGui() {
		JFrame frame = new JFrame("ParkingMeter");
		
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().add(new JLabel("Test"), BorderLayout.CENTER);
		frame.pack();
		frame.setVisible(true);
	}
	
	@Override
	public void run() {
		initGui();
	}

	@Override
	public int GetParkingLotNumber() {
		return 0;
	}

	@Override
	public void welcome() {
		System.out.println("GuiSimulationView: Welcome");
	}

}
