package ch.zhaw.swengineering.event;

import java.util.EventListener;

/**
 * @author Daniel Brun
 * 
 *         Listener interface for events generated by the view.
 */
public interface ViewEventListener extends EventListener {
	/**
	 * Called when the user entered a parking lot number.
	 * 
	 * @param parkingLotEnteredEvent
	 *            Event data
	 */
	void parkingLotEntered(ParkingLotEnteredEvent parkingLotEnteredEvent);

	/**
	 * Called when the user aborted the current action.
	 * 
	 * @param actionAbortedEvent
	 *            Event data
	 */
	void actionAborted(ActionAbortedEvent actionAbortedEvent);

	/**
	 * Called when the user has finished inserting coins into the intelligent
	 * slot machine.
	 * 
	 * @param moneyInsertedEvent
	 *            the event data.
	 */
	void moneyInserted(MoneyInsertedEvent moneyInsertedEvent);

	/**
	 * Request to shut down the parking meter.
	 * 
	 * @param shutdownEvent
	 *            the event data.
	 */
	void shutdownRequested(ShutdownEvent shutdownEvent);
	
	
	/**
	 * Called when the user entered the new coin box levels.
	 * 
	 * @param coinBoxLevelEnteredEvent the event data.
	 */
	void coinBoxLevelEntered(CoinBoxLevelEnteredEvent coinBoxLevelEnteredEvent);

}
