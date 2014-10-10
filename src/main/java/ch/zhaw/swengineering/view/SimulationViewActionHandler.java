/**
 * 
 */
package ch.zhaw.swengineering.view;

import ch.zhaw.swengineering.event.ViewEventListener;

/**
 * @author Daniel Brun
 *
 * Interface which defines the actions which the controller can invoke on the simulation view.
 */
public interface SimulationViewActionHandler {

	/**
	 * Starts the simulation gui.
	 * 
	 * TODO: Possibly pass controller.
	 */
	void startSimulationView();
	
	/**
	 * Registers a view event listener.
	 * 
	 * @param aListener The listener to register.
	 */
	void addViewEventListener(ViewEventListener aListener);
	
	/**
	 * Deregisters a view event listener.
	 * 
	 * @param aListener The listener to deregister.
	 */
	void removeViewEventListener(ViewEventListener aListener);
}
