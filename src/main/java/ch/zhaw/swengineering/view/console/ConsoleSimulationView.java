/**
 * 
 */
package ch.zhaw.swengineering.view.console;

import ch.zhaw.swengineering.event.ViewEventListener;
import ch.zhaw.swengineering.event.ViewInputEvent;
import ch.zhaw.swengineering.helper.MessageProvider;
import ch.zhaw.swengineering.view.SimulationViewActionHandler;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Scanner;

/**
 * @author Daniel Brun
 *
 * Console implementation of the interface {@link SimulationViewActionHandler}
 */
@Component
public class ConsoleSimulationView extends SimulationViewActionHandler {

	private static final Logger LOG = LogManager.getLogger(ConsoleSimulationView.class);
	
	private boolean run;

	@Autowired
	private MessageProvider messageProvider;
	
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

		run = true;
		
		Scanner consoleScanner = new Scanner(System.in);
		LOG.debug("Console aquired");
		//Read console input
		while(run){
			//TODO: Replace hard coded string. (Controller: printXyz, then: start application)
			//TODO sl: I replaced the hard coded string. But what does the stuff in the brackets mean?
			System.out.print(messageProvider.get("pakring lot number") + ": > ");
			String userInputLine = consoleScanner.nextLine();
			
			notifyViewEventListeners(userInputLine);
		}
		
		consoleScanner.close();
		
		LOG.info("Console reader stopped...");
	}
}
