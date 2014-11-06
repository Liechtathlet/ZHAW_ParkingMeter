package ch.zhaw.swengineering.slotmachine.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.annotation.PostConstruct;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;

import ch.zhaw.swengineering.helper.ConfigurationProvider;
import ch.zhaw.swengineering.model.persistence.CoinBox;
import ch.zhaw.swengineering.model.persistence.CoinBoxes;
import ch.zhaw.swengineering.slotmachine.exception.CoinBoxFullException;
import ch.zhaw.swengineering.slotmachine.exception.InvalidCoinException;
import ch.zhaw.swengineering.slotmachine.exception.NoTransactionException;
import ch.zhaw.swengineering.slotmachine.exception.SlotMachineException;
import ch.zhaw.swengineering.slotmachine.exception.TransactionAlreadyStartedException;

/**
 * @author Daniel Brun
 * 
 *         Implementation of the {@link IntelligentSlotMachine}. TODO: Refactor
 *         & Review
 */
@Controller
public class IntelligentSlotMachineImpl implements IntelligentSlotMachine {

    /**
     * The Logger.
     */
    private static final Logger LOG = LogManager
            .getLogger(IntelligentSlotMachineImpl.class);

    @Autowired
    @Qualifier("coinBoxes")
    private ConfigurationProvider coinBoxesProvider;
    private CoinBoxes coinBoxes;

    private boolean transactionStarted;

    private Map<BigDecimal, Integer> transactionCoinCache;
    private Map<BigDecimal, Integer> crawBackCoinCache;
    private SortedSet<BigDecimal> availableCoinList;
    private SortedSet<BigDecimal> reverseAvailableCoinList;

    /**
     * Creates a new instance of this class.
     */
    public IntelligentSlotMachineImpl() {
        transactionCoinCache = new Hashtable<>();
        availableCoinList = new TreeSet<>();
        reverseAvailableCoinList = new TreeSet<>(Collections.reverseOrder());
        crawBackCoinCache = new Hashtable<>();
    }

    /**
     * Loads the data from the configuration providers.
     */
    @PostConstruct
    public void init() {
        transactionStarted = false;

        LOG.info("Loading data...");

        LOG.info("Loading CoinBoxes...");
        if (coinBoxesProvider != null && coinBoxesProvider.get() != null) {
            coinBoxes = (CoinBoxes) coinBoxesProvider.get();

            resetCoinCache();

            availableCoinList.addAll(transactionCoinCache.keySet());
            reverseAvailableCoinList.addAll(transactionCoinCache.keySet());
        }
    }

    @Override
    public void startTransaction() {
        LOG.info("Trying to start new transaction");
        if (transactionStarted) {
            throw new TransactionAlreadyStartedException(
                    "A transaction was already started.");
        }

        LOG.info("Starting new transaction...");
        setTransaction(true);
        resetCoinCache();
    }

    @Override
    public void finishTransaction(BigDecimal drawback) {
        if (transactionStarted) {
            BigDecimal currDrawback = drawback.setScale(2);

            // TODO: Request for Review: Is there a case, when this doesn't
            // work?
            for (BigDecimal coinVal : reverseAvailableCoinList) {
                CoinBox cb = coinBoxes.getCoinBox(coinVal);
                int count = currDrawback.divideToIntegralValue(cb.getCoinValue())
                        .toBigInteger().intValue();

                // Check if enough coins are available
                if (count > cb.getCurrentCoinCount()) {
                    count = cb.getCurrentCoinCount();
                }

                if (count > 0) {
                    // Put into drawback cache
                    crawBackCoinCache.put(cb.getCoinValue(),
                            Integer.valueOf(count));
                    cb.setCurrentCoinCount(cb.getCurrentCoinCount() - count);

                    // Subtract: count * value
                    currDrawback = currDrawback.subtract(cb.getCoinValue()
                            .multiply(new BigDecimal(count)));

                    if (currDrawback.compareTo(BigDecimal.ZERO) == 0) {
                        break;
                    }
                }
            }

            setTransaction(false);

            if (currDrawback.compareTo(BigDecimal.ZERO) != 0) {
                LOG.error("Error during drawback calculation...");
                throw new SlotMachineException(
                        "Error during drawback calculation...");
            }
        }
    }

    @Override
    public Map<BigDecimal, Integer> rolebackTransaction() {
        Map<BigDecimal, Integer> drawBackMap = new Hashtable<>(
                transactionCoinCache);

        for (Entry<BigDecimal, Integer> entry : transactionCoinCache.entrySet()) {
            if (entry.getValue() > 0) {
                CoinBox cb = coinBoxes.getCoinBox(entry.getKey());
                cb.setCurrentCoinCount(cb.getCurrentCoinCount()
                        - entry.getValue().intValue());
            }
        }

        return drawBackMap;
    }

    @Override
    public BigDecimal getAmountOfCurrentlyInsertedMoney() {
        BigDecimal summ = BigDecimal.ZERO;

        if (transactionStarted) {
            for (Entry<BigDecimal, Integer> entry : transactionCoinCache
                    .entrySet()) {
                BigDecimal coinTotal = entry.getKey().multiply(
                        BigDecimal.valueOf(entry.getValue()));
                summ = summ.add(coinTotal);
            }
        }
        return summ;
    }

    @Override
    public void insertCoin(final BigDecimal aCoinValue)
            throws NoTransactionException, InvalidCoinException,
            CoinBoxFullException {
        LOG.info("Inserting coin: " + aCoinValue);

        BigDecimal coinVal = aCoinValue.setScale(2);
        // Check if transaction is started
        if (!transactionStarted) {
            throw new NoTransactionException(
                    "A transaction is required to insert coins}");
        }

        Integer count = transactionCoinCache.get(coinVal);

        // Check if coin is valid.
        if (count == null) {
            LOG.error("Invalid coin inserted!");
            throw new InvalidCoinException("The inserted coin was invalid.",
                    new ArrayList<>(availableCoinList));
        } else {
            // transfer the coin to the coinbox.
            transferCoinToCoinBox(coinVal);

            // save in the cache
            transactionCoinCache.put(coinVal,
                    Integer.valueOf(count.intValue() + 1));
        }
    }

    /**
     * Transfers the given coin to the corresponding coin box.
     * 
     * @param aCoinValue
     *            the value of the coin.
     */
    private void transferCoinToCoinBox(BigDecimal aCoinValue) {
        boolean allCoinBoxesFull = true;
        boolean coinBoxFull = true;

        for (CoinBox cb : coinBoxes.getCoinBoxes()) {
            if (cb.getCurrentCoinCount() < cb.getMaxCoinCount()) {
                allCoinBoxesFull = false;
            }

            if (cb.getCoinValue().equals(aCoinValue)) {
                if (cb.getCurrentCoinCount() < cb.getMaxCoinCount()) {
                    cb.setCurrentCoinCount(cb.getCurrentCoinCount() + 1);
                    coinBoxFull = false;
                }
            }
        }

        if (allCoinBoxesFull) {
            LOG.warn("All coin boxes are full!");
            throw new CoinBoxFullException("All coin boxes are full!");
        } else if (coinBoxFull) {
            LOG.warn("The coin boxe are full!");
            throw new CoinBoxFullException("The coin box for "
                    + aCoinValue.toPlainString() + " is full!", aCoinValue);
        }
    }

    /**
     * Resets the coins in the coin cache.
     */
    private void resetCoinCache() {
        for (CoinBox cb : coinBoxes.getCoinBoxes()) {
            cb.setCoinValue(cb.getCoinValue().setScale(2));
            transactionCoinCache.put(cb.getCoinValue(), Integer.valueOf(0));
            crawBackCoinCache.put(cb.getCoinValue(), Integer.valueOf(0));
        }
    }

    /**
     * Sets the value of the transaction in a synchronized block.
     * 
     * @param aValue
     *            the value to set.
     */
    private synchronized void setTransaction(final boolean aValue) {
        transactionStarted = aValue;
    }

    @Override
    public Map<BigDecimal, Integer> getDrawback() {
        if (!transactionStarted) {
            return crawBackCoinCache;
        } else {
            return null;
        }
    }

}
