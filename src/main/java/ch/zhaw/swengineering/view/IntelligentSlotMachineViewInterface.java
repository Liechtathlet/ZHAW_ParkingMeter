/**
 * 
 */
package ch.zhaw.swengineering.view;

import ch.zhaw.swengineering.slotmachine.exception.CoinBoxFullException;
import ch.zhaw.swengineering.slotmachine.exception.InvalidCoinException;
import ch.zhaw.swengineering.slotmachine.exception.NoTransactionException;

/**
 * @author Daniel Brun
 * 
 *         Interface for view side slot machine handling.
 */
public interface IntelligentSlotMachineViewInterface {

    /**
     * Handles the coin box full exception.
     * 
     * @param anException
     *            The coin box full exception to handle.
     */
    void handleCoinBoxFullException(CoinBoxFullException anException);

    /**
     * Handles the no transaction exception.
     * 
     * @param anException
     *            The no transaction exception to handle.
     */
    void handleNoTransactionException(NoTransactionException anException);

    /**
     * Handles the invalid coin exception.
     * 
     * @param anException
     *            The invalid coin exception to handle.
     */
    void handleInvalidCoinException(InvalidCoinException anException);

    /**
     * Handles the number format exception.
     * 
     * @param anException
     *            The number format exception to handle.
     */
    void handleNumberFormatException(NumberFormatException anException);
    
    /**
     * Roles back the current transaction and outputs the drawback.
     */
    void rolebackTransaction();
}
