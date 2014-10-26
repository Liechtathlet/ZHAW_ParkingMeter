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
     * State if the user must drop in some money.
     */
    DROPPING_IN_MONEY;
}
