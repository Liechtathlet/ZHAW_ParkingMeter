package ch.zhaw.swengineering.slotmachine.controller;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import ch.zhaw.swengineering.model.CoinBoxLevel;
import ch.zhaw.swengineering.slotmachine.exception.TransactionAlreadyStartedException;

/**
 * @author Daniel Brun
 * 
 *         Interfaces which defines all methods for the interaction between the
 *         intelligent slot machine and the back-end.
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
     * drawback will be subtracted and prepared for returning to the user. If no
     * transaction is active, this method will have no effect.
     * 
     * @param drawback
     *            the drawback to issue.
     */
    void finishTransaction(BigDecimal drawback);

    /**
     * Starts a new transaction.
     * 
     * @throws TransactionAlreadyStartedException
     *             thrown if a transaction was already started.
     */
    void startTransaction();

    /**
     * Gets the current coin boxes.
     * 
     * @return the coin box levels.
     */
    List<CoinBoxLevel> getCurrentCoinBoxLevel();

    /**
     * Updates the coin box levels.
     * 
     * @param someCoinBoxLevels
     *            The coin box levels.
     */
    void updateCoinLevelInCoinBoxes(List<CoinBoxLevel> someCoinBoxLevels);

    /**
     * Checks if the current transaction has a drawback.
     * 
     * @return true if the current transaction has a drawback, false otherwise.
     */
    boolean hasDrawback();

    /**
     * Gets a map with the inserted coins and their count.
     * 
     * @return the map with the coins and counts.
     */
    Map<BigDecimal, Integer> getInsertedCoins();

}
