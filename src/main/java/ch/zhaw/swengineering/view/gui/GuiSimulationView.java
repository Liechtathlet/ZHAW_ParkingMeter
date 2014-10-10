/**
 * 
 */
package ch.zhaw.swengineering.view.gui;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JLabel;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import ch.zhaw.swengineering.event.ViewEventListener;
import ch.zhaw.swengineering.view.SimulationViewActionHandler;

/**
 * @author Daniel Brun
 *
 * Gui implementation of the interface {@link SimulationViewActionHandler}
 * 
 * TODO: Find soluation for console / gui switch.
 */
//@Component("gui")
public class GuiSimulationView implements SimulationViewActionHandler, Runnable{

	private static final Logger LOG = LogManager.getLogger(GuiSimulationView.class);
	
	private List<ViewEventListener> eventListeners;
	
	private Thread guiThread;
	
	/**
	 * Creates a new instance of this class.
	 */
	public GuiSimulationView(){
		eventListeners = new ArrayList<ViewEventListener>();
	}
	
	@Override
	public void startSimulationView() {
		
		if(guiThread == null){
			//Create and start thread
			guiThread = new Thread(this);
			guiThread.start();
		}else{
			//TODO: Throw exception. 
		}

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
	
	@Override
	public void run() {
		initGui();
	}

	@Override
	public void addViewEventListener(ViewEventListener aListener) {
		if(aListener == null){
			throw new IllegalArgumentException("The parameter 'aListener' must not be null!");
		}
		
		if(!eventListeners.contains(aListener)){
			eventListeners.add(aListener);
		}
	}

	@Override
	public void removeViewEventListener(ViewEventListener aListener) {
		if(aListener == null){
			throw new IllegalArgumentException("The parameter 'aListener' must not be null!");
		}
		
		if(eventListeners.contains(aListener)){
			eventListeners.remove(aListener);
		}
		
	}

}
