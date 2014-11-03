package ch.zhaw.swengineering.slotmachine.controller;

import java.math.BigDecimal;

import ch.zhaw.swengineering.slotmachine.exception.TransactionAlreadyStartedException;

/**
 * @author Daniel Brun
 * 
 *         Interfaces which defines all methods for the interaction betwenn the
 *         intelligent slot machine and the backend.
 */
public interface IntelligentSlotMachineBackendInteractionInterface {

    /**
     * Gets the amount of the currently inserted money.
     * 
     * @return the amount of money, which was inserted in the current
     *         transaction.
     */
    BigDecimal getAmountOfCurrentlyInsertedMoney();

    /**
     * Finishes the transaction and transfers the coin to the coin box. The
     * drawback will be subtracted and prepared for returning to the user.
     * If no transaction is active, this method will have no effect.
     * 
     * @param drawback the drawback to issue.
     */
    void finishTransaction(BigDecimal drawback);

    /**
     * Starts a new transaction.
     * 
     * @throws TransactionAlreadyStartedException
     *             thrown if a transaction was already started.
     */
    void startTransaction();

}
