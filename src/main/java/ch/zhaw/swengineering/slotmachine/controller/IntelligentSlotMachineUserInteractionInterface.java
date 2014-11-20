package ch.zhaw.swengineering.slotmachine.controller;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import ch.zhaw.swengineering.slotmachine.exception.CoinBoxFullException;
import ch.zhaw.swengineering.slotmachine.exception.InvalidCoinException;
import ch.zhaw.swengineering.slotmachine.exception.NoTransactionException;

/**
 * @author Daniel Brun
 * 
 *         Interfaces which defines all methods for the interaction between the
 *         intelligent slot machine and the user.
 */
public interface IntelligentSlotMachineUserInteractionInterface {

    /**
     * Inserts a coin into the slot machine. Before a coin can be inserted, a
     * transaction must be started from the back-end.
     * 
     * @param aCoinValue
     *            the coin value (e.g. 1.0).
     * @throws InvalidCoinException
     *             thrown if an invalid coins was inserted.
     * @throws NoTransactionException
     *             thrown if no transaction is active.
     * @throws CoinBoxFullException
     *             thrown if one or all coin boxes are full.
     */
    void insertCoin(BigDecimal aCoinValue) throws NoTransactionException,
            InvalidCoinException, CoinBoxFullException;

    /**
     * Roles back the current transaction to its start point.
     * 
     * @return the map with the coins to output.
     */
    Map<BigDecimal, Integer> rolebackTransaction();

    /**
     * Gets the drawback of the current transaction. The transaction must be
     * finished to deliver the drawback.
     * 
     * @return the current drawback.
     */
    Map<BigDecimal, Integer> getDrawback();

    /**
     * Gets a list with all available coins.
     * 
     * @return the coin list.
     */
    List<BigDecimal> getAvailableCoins();
}
