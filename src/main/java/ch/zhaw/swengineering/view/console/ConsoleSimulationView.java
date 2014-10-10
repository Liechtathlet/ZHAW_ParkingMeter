/**
 * 
 */
package ch.zhaw.swengineering.view.console;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import ch.zhaw.swengineering.event.ViewEventListener;
import ch.zhaw.swengineering.event.ViewInputEvent;
import ch.zhaw.swengineering.view.SimulationViewActionHandler;

/**
 * @author Daniel Brun
 *
 * Console implementation of the interface {@link SimulationViewActionHandler}
 */
@Component
public class ConsoleSimulationView implements SimulationViewActionHandler, Runnable{

	private static final Logger LOG = LogManager.getLogger(ConsoleSimulationView.class);
	
	private List<ViewEventListener> eventListeners;
	
	private Thread consoleThread;
	private boolean run;
	
	/**
	 * Creates a new instance of this class.
	 */
	public ConsoleSimulationView(){
		eventListeners = new ArrayList<ViewEventListener>();
	}
	
	@Override
	public void startSimulationView() {
		LOG.info("Trying to start console simulation");
		
		if(consoleThread == null){
			run = true;
			
			//Create and start thread
			consoleThread = new Thread(this);
			consoleThread.start();
			
			LOG.info("Console simulation started");
		}else{
			//TODO: Throw exception. 
		}

	}
	
	/**
	 * Propagates the view input event to all registered listeners.
	 * 
	 * @param aViewInputEvent The input to propagate.
	 */
	private void notifyViewEventListeners(String anInput){
		LOG.info("Registered view input event: " + anInput);
		
		for(ViewEventListener listener : eventListeners){
			listener.inputEntered(new ViewInputEvent(this, anInput));
		}
	}
	
	@Override
	public void run() {
		LOG.info("Initialize console reader...");
		
		Scanner consoleScanner = new Scanner(System.in);
		LOG.debug("Console aquired");
		//Read console input
		while(run){
			//TODO: Replace hard coded string. (Controller: printXyz, then: start application
			System.out.print("Parkplatznummer: > ");
			String userInputLine = consoleScanner.nextLine();
			
			notifyViewEventListeners(userInputLine);
		}
		
		consoleScanner.close();
		
		LOG.info("Console reader stopped...");
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
