/**
 * 
 */
package ch.zhaw.swengineering.controller;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import ch.zhaw.swengineering.event.ViewEventListener;
import ch.zhaw.swengineering.event.ViewInputEvent;
import ch.zhaw.swengineering.view.SimulationViewActionHandler;

/**
 * @author Daniel Brun
 *
 * Controller for the view.
 * 
 * TODO: Het öpert en bessere Name?
 * TODO sl: GuiController? Es wird denn wahrschinli no en RestController geh..
 * TODO: db: Hani mer au überleit, denn isch console au = gui
 */
@Controller
public class ViewControllerImpl implements ViewController, ViewEventListener{

	private static final Logger LOG = LogManager.getLogger(ViewControllerImpl.class);
	
	@Autowired
	private SimulationViewActionHandler viewActionHandler;
	
	@Override
	public void start() {
		LOG.info("Starting controller...");
		
		//Register event listener.
		viewActionHandler.addViewEventListener(this);
		
		//Start simulation view.
		viewActionHandler.startSimulationView();
	}

	@Override
	public void inputEntered(ViewInputEvent anInputEvent) {
		System.out.println("Received input event: " + anInputEvent.getInput());
		
	}
}
