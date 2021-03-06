package ch.zhaw.swengineering.model.persistence;

/**
 * @author Daniel Brun
 * 
 *         Enum to map the secret codes to specific actions.
 */
public enum SecretActionEnum {

    /**
     * 
     * Action for viewing all available information.
     */
    VIEW_ALL_INFORMATION,

    /**
     * Action for viewing the current state of the parking lots.
     */
    VIEW_ALL_PARKING_CHARGE,

    /**
     * Action for viewing the content of the coin boxes.
     */
    VIEW_CONTENT_OF_COIN_BOXES,

    /**
     * Action for entering a new level for a coin box.
     */
    ENTER_NEW_LEVEL_FOR_COIN_BOXES,

    /**
     * Action for viewing all transaction logs.
     */
    VIEW_ALL_TRANSACTION_LOGS,

    /**
     * Action for viewing the last 24 hours of the transaction log.
     */
    VIEW_LAST_24_HOURS_OF_TRANSACTION_LOG,

    /**
     * Action for viewing the last n transaction log entries.
     */
    VIEW_N_TRANSACTION_LOG_ENTRIES
}
