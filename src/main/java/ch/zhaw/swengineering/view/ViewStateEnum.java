package ch.zhaw.swengineering.view;

/**
 * @author Daniel Brun
 * 
 *         Enum which represents the current state of the console view.
 */
public enum ViewStateEnum {

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
	 * State if the view is stopped/shut down.
	 */
	EXIT
}
