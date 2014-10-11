/**
 * 
 */
package ch.zhaw.swengineering.view.gui;

import ch.zhaw.swengineering.view.SimulationViewActionHandler;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.awt.*;

/**
 * @author Daniel Brun
 *
 * Gui implementation of the interface {@link SimulationViewActionHandler}
 * 
 * TODO: Find solution for console / gui switch.
 */
//@Component("gui")
public class GuiSimulationView extends SimulationViewActionHandler {

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

}
