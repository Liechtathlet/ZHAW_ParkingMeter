package ch.zhaw.swengineering.view.console;

/**
 * @author Daniel Brun
 * 
 *         Enum which represents the current state of the console view.
 */
public enum ConsoleViewStateEnum {

	/**
	 * State during initialization or if no other state is set.
	 */
	INIT,
	/**
	 * State if the user must enter a parking lot number.
	 */
	ENTERING_PARKING_LOT,
	/**
	 * State if the user must enter a new level for the coin boxes.
	 */
	ENTERING_COIN_BOX_COIN_LEVEL,
	/**
	 * State if the user must drop in some money.
	 */
	DROPPING_IN_MONEY,
	/**
	 * State if the view all information secret code is entered.
	 */
	DISPLAY_ALL_INFORMATION,
	/**
	 * State if the view booked parkingLots secret code is entered.
	 */
	DISPLAY_BOOKED_PARKINGLOTS,
	/**
	 * State if the view is stopped / shut down.
	 */
	EXIT
}
