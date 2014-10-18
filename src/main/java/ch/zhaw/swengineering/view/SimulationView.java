/**
 * 
 */
package ch.zhaw.swengineering.view;

import ch.zhaw.swengineering.event.ViewEventListener;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Daniel Brun
 *
 * Interface which defines the actions which the controller can invoke on the simulation view.
 * TODO sl: Ist als abstrake Klasse umgesetzt. Man könnte auch ganz auf Vererbung/Implementierung verzichten und die View's injecten?
 * TODO: db wenn es kein Interface / abstrakte Klasse ist, dann müsste es zwei verschieden Controller geben, da diese sonst nicht in einem injected werden können.
 */
public abstract class SimulationView implements Runnable {

	private static final Logger LOG = LogManager.getLogger(SimulationView.class);

	private Thread thread;

	protected List<ViewEventListener> eventListeners;

	public SimulationView() {
		eventListeners = new ArrayList<>();
	}

	/**
	 * Starts the simulation gui.
	 *
	 * TODO: Possibly pass controller.
	 */
	public void startSimulationView() {
		LOG.info("Trying to start console simulation");

		if(thread == null){
			//Create and start thread
			thread = new Thread(this);
			thread.start();

			LOG.info("Console simulation started");
		}else{
			//TODO: Throw exception.
		}
	}

	public abstract void run();
	public abstract void showParkingLotMessage() throws IOException;

	/**
	 * Registers a view event listener.
	 *
	 * @param aListener The listener to register.
	 */
	public void addViewEventListener(ViewEventListener aListener) {
		if(aListener == null){
			throw new IllegalArgumentException("The parameter 'aListener' must not be null!");
		}

		if(!eventListeners.contains(aListener)){
			eventListeners.add(aListener);
		}
	}

	/**
	 * Deregisters a view event listener.
	 *
	 * @param aListener The listener to deregister.
	 */
	public void removeViewEventListener(ViewEventListener aListener) {
		if(aListener == null){
			throw new IllegalArgumentException("The parameter 'aListener' must not be null!");
		}

		if(eventListeners.contains(aListener)){
			eventListeners.remove(aListener);
		}
	}
}
