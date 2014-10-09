/**
 * 
 */
package ch.zhaw.swengineering.view.gui;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JLabel;

import org.springframework.stereotype.Component;

import ch.zhaw.swengineering.view.SimulationViewActionHandler;

/**
 * @author Daniel Brun
 *
 * Gui implementation of the interface {@link SimulationViewActionHandler}
 */
@Component("gui")
public class GuiSimulationView implements SimulationViewActionHandler, Runnable{

	private Thread consoleThread;
	
	public GuiSimulationView(){
		
	}
	
	@Override
	public void startSimulationGui() {
		
		if(consoleThread == null){
			//Create and start thread
			consoleThread = new Thread(this);
			consoleThread.start();
		}else{
			//TODO: Throw exception. 
		}

	}
	
	@Override
	public void run() {
		initGui();
	}

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

	

}
