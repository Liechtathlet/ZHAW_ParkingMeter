package ch.zhaw.swengineering.slotmachine.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import ch.zhaw.swengineering.helper.ConfigurationProvider;
import ch.zhaw.swengineering.helper.ConfigurationWriter;
import ch.zhaw.swengineering.model.CoinBoxLevel;
import ch.zhaw.swengineering.model.persistence.CoinBox;
import ch.zhaw.swengineering.model.persistence.CoinBoxes;
import ch.zhaw.swengineering.setup.ParkingMeterRunner;
import ch.zhaw.swengineering.slotmachine.exception.CoinBoxFullException;
import ch.zhaw.swengineering.slotmachine.exception.InvalidCoinException;
import ch.zhaw.swengineering.slotmachine.exception.NoTransactionException;
import ch.zhaw.swengineering.slotmachine.exception.SlotMachineException;
import ch.zhaw.swengineering.slotmachine.exception.TransactionAlreadyStartedException;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = ParkingMeterRunner.class, loader = AnnotationConfigContextLoader.class)
public class IntelligentSlotMachineImplTest {

    @InjectMocks
    private IntelligentSlotMachineImpl slotMachine;

    @Mock(name = "coinBoxesProvider")
    private ConfigurationProvider configProviderCoinBoxes;

    @Mock(name = "coinBoxesWriter")
    private ConfigurationWriter configWriterCoinBoxes;

    /**
     * Sets up a test case.
     */
    @Before
    public final void setUp() {
        MockitoAnnotations.initMocks(this);

        when(configProviderCoinBoxes.get()).thenReturn(getMockedCoinBoxes());

        slotMachine.init();
    }

    /**
     * Method: startTransaction(...)
     * 
     * Scenario: start a transaction.
     * 
     * Expected: No Exception.
     */
    @Test
    public final void testStartTransaction() {
        slotMachine.startTransaction();

        assertEquals(0, BigDecimal.ZERO.compareTo(slotMachine
                .getAmountOfCurrentlyInsertedMoney()));
    }

    /**
     * Method: startTransaction(...)
     * 
     * Scenario: start two transactions.
     * 
     * Expected: An exception, that a transaction already is active.
     */
    @Test(expected = TransactionAlreadyStartedException.class)
    public final void testStartTransactionDouble() {
        slotMachine.startTransaction();
        slotMachine.startTransaction();
    }

    /**
     * Method: insertCoin(...)
     * 
     * Scenario: insert a coin with no started transaction.
     * 
     * Expected: An exception is thrown.
     * 
     * @throws InvalidCoinException
     *             Thrown if an invalid coin was inserted.
     * @throws NoTransactionException
     *             Thrown if no transaction was started (expected).
     */
    @Test(expected = NoTransactionException.class)
    public final void testInsertCoinWithNoTransaction()
            throws NoTransactionException, InvalidCoinException {

        slotMachine.insertCoin(new BigDecimal(0.5));
    }

    /**
     * Method: insertCoin(...)
     * 
     * Scenario: insert a valid coin.
     * 
     * Expected: the coin is accepted.
     */
    @Test
    public final void testInsertCoinWithTransactionAndValidCoin() {
        BigDecimal insertedCoin = new BigDecimal(0.50);
        insertedCoin = insertedCoin.setScale(2);

        // Start
        slotMachine.startTransaction();

        try {
            slotMachine.insertCoin(insertedCoin);
        } catch (NoTransactionException | InvalidCoinException e) {
            fail("Unexpected exception received: " + e);
        }

        assertEquals(insertedCoin,
                slotMachine.getAmountOfCurrentlyInsertedMoney());
    }

    /**
     * Method: insertCoin(...)
     * 
     * Scenario: insert an invalid coin.
     * 
     * Expected: the coin is no accepted and an exception is thrown.
     * 
     */
    @Test(expected = InvalidCoinException.class)
    public final void testInsertCoinWithTransactionAndInValidCoin()
            throws NoTransactionException, InvalidCoinException,
            CoinBoxFullException {
        slotMachine.startTransaction();

        slotMachine.insertCoin(new BigDecimal("0.2"));
    }

    /**
     * Method: insertCoin(...)
     * 
     * Scenario: insert valid coins till the box is full.
     * 
     * Expected: an exception is thrown.
     * 
     */
    @Test(expected = CoinBoxFullException.class)
    public final void testInsertCoinWithCoinBoxFull()
            throws NoTransactionException, InvalidCoinException,
            CoinBoxFullException {
        BigDecimal insertedCoin = new BigDecimal(0.5);
        slotMachine.startTransaction();

        slotMachine.insertCoin(insertedCoin);
        slotMachine.insertCoin(insertedCoin);
        slotMachine.insertCoin(insertedCoin);
        slotMachine.insertCoin(insertedCoin);
        slotMachine.insertCoin(insertedCoin);
        slotMachine.insertCoin(insertedCoin);
    }

    /**
     * Method: insertCoin(...)
     * 
     * Scenario: insert valid coins till all boxes are full.
     * 
     * Expected: an exception is thrown.
     * 
     */
    @Test(expected = CoinBoxFullException.class)
    public final void testInsertCoinWithAllCoinBoxFull()
            throws NoTransactionException, InvalidCoinException,
            CoinBoxFullException {
        BigDecimal coinOne = new BigDecimal(0.5);
        BigDecimal coinTwo = new BigDecimal(2.0);
        BigDecimal coinThree = new BigDecimal(1.0);

        slotMachine.startTransaction();

        slotMachine.insertCoin(coinOne);
        slotMachine.insertCoin(coinOne);
        slotMachine.insertCoin(coinOne);
        slotMachine.insertCoin(coinOne);
        slotMachine.insertCoin(coinOne);

        slotMachine.insertCoin(coinTwo);
        slotMachine.insertCoin(coinTwo);
        slotMachine.insertCoin(coinTwo);
        slotMachine.insertCoin(coinTwo);

        slotMachine.insertCoin(coinThree);
        slotMachine.insertCoin(coinThree);
        slotMachine.insertCoin(coinThree);
        slotMachine.insertCoin(coinThree);
    }

    /**
     * Method: finishTransaction(...)
     * 
     * Scenario: End transaction with drawback
     * 
     * Expected: an exception is thrown.
     * 
     */
    @Test
    public final void testFinishTransactionWithDrawback()
            throws NoTransactionException, InvalidCoinException,
            CoinBoxFullException {
        Map<BigDecimal, Integer> drawbackMap = new HashMap<BigDecimal,Integer>();
        
        BigDecimal insertedCoin = new BigDecimal(0.5);
        BigDecimal drawBack = new BigDecimal(2.0);
        
        insertedCoin = insertedCoin.setScale(2);
        drawBack = drawBack.setScale(2);
        
        drawbackMap.put(insertedCoin, Integer.valueOf(0));
        drawbackMap.put(new BigDecimal(1.0).setScale(2), Integer.valueOf(0));
        drawbackMap.put(drawBack, Integer.valueOf(1));
        
        slotMachine.startTransaction();

        slotMachine.insertCoin(insertedCoin);
        slotMachine.insertCoin(insertedCoin);
        slotMachine.insertCoin(insertedCoin);
        slotMachine.insertCoin(insertedCoin);
        slotMachine.insertCoin(insertedCoin);

        slotMachine.finishTransaction(drawBack);

        assertEquals(0, BigDecimal.ZERO.compareTo(slotMachine
                .getAmountOfCurrentlyInsertedMoney()));

        assertEquals(drawbackMap, slotMachine.getDrawback());

        verify(configWriterCoinBoxes).write(any(Object.class));
    }

    /**
     * Method: finishTransaction(...)
     * 
     * Scenario: End transaction with drawback which is greater then the current
     * available money.
     * 
     * Expected: an exception is thrown.
     * 
     */
    @Test(expected = SlotMachineException.class)
    public final void testFinishTransactionWithToMuchDrawback()
            throws NoTransactionException, InvalidCoinException,
            CoinBoxFullException {
        BigDecimal insertedCoin = new BigDecimal(0.5);
        BigDecimal drawBack = new BigDecimal(100);
        slotMachine.startTransaction();

        slotMachine.insertCoin(insertedCoin);

        slotMachine.finishTransaction(drawBack);
    }

    /**
     * Method: rolebackTransaction(...)
     * 
     * Scenario: roles back a transaction.
     * 
     * Expected: the transaction is roled back and the expected coin counts
     * returned.
     */
    @Test
    public final void testRolebackTransaction() {
        BigDecimal insertedCoin = new BigDecimal(0.50);
        insertedCoin = insertedCoin.setScale(2);

        // Start
        slotMachine.startTransaction();

        try {
            slotMachine.insertCoin(insertedCoin);
            slotMachine.insertCoin(insertedCoin);
        } catch (NoTransactionException | InvalidCoinException e) {
            fail("Unexpected exception received: " + e);
        }

        assertEquals(insertedCoin.multiply(new BigDecimal(2)),
                slotMachine.getAmountOfCurrentlyInsertedMoney());

        Map<BigDecimal, Integer> drawback = slotMachine.rolebackTransaction();

        assertEquals(2, drawback.get(insertedCoin).intValue());
    }

    /**
     * Method: getCoinBoxLevels(...)
     * 
     * Scenario: gets the current coin box levels
     * 
     * Expected: The coin box levels are returned.
     */
    @Test
    public final void testGetCoinBoxLevels() {
        List<CoinBoxLevel> coinBoxLevels = slotMachine.getCurrentCoinBoxLevel();

        for (CoinBoxLevel cbLevel : coinBoxLevels) {

            for (CoinBox cb : getMockedCoinBoxes().getCoinBoxes()) {
                if (cbLevel.getCoinValue().equals(cb.getCoinValue())) {
                    assertEquals(cb.getCurrentCoinCount(),
                            cbLevel.getCurrentCoinCount());
                }
            }
        }
    }

    /**
     * Method: updateCoinBoxLevels(...)
     * 
     * Scenario: Updates the coin box levels
     * 
     * Expected: The coin box levels are updated and correctly returned.
     */
    @Test
    public final void testUpdateCoinBoxLevels() {
        List<CoinBoxLevel> coinBoxLevels = slotMachine.getCurrentCoinBoxLevel();

        for (CoinBoxLevel cbLevel : coinBoxLevels) {
            cbLevel.setCurrentCoinCount(cbLevel.getCurrentCoinCount() + 2);
        }

        slotMachine.updateCoinLevelInCoinBoxes(coinBoxLevels);

        coinBoxLevels = slotMachine.getCurrentCoinBoxLevel();

        for (CoinBoxLevel cbLevel : coinBoxLevels) {

            for (CoinBox cb : getMockedCoinBoxes().getCoinBoxes()) {
                if (cbLevel.getCoinValue().equals(cb.getCoinValue())) {
                    assertEquals(cb.getCurrentCoinCount(),
                            cbLevel.getCurrentCoinCount());
                }
            }
        }
    }

    /**
     * Method: updateCoinBoxLevels(...)
     * 
     * Scenario: Updates the coin box levels over the maximum capacity.
     * 
     * Expected: An exception is thrown.
     */
    @Test(expected = CoinBoxFullException.class)
    public final void testUpdateCoinBoxLevelsOverTheMax() {
        List<CoinBoxLevel> coinBoxLevels = slotMachine.getCurrentCoinBoxLevel();

        for (CoinBoxLevel cbLevel : coinBoxLevels) {
            cbLevel.setCurrentCoinCount(cbLevel.getMaxCoinCount() + 1);
        }

        slotMachine.updateCoinLevelInCoinBoxes(coinBoxLevels);
    }

    /**
     * Method: getAvailableCoins(...)
     * 
     * Scenario: Gets the available coins.
     * 
     * Expected: The correct coins are returned.
     */
    @Test
    public final void testGetAvailableCoins() {
        List<BigDecimal> coinList = slotMachine.getAvailableCoins();

        List<CoinBox> coinBoxList = getMockedCoinBoxes().getCoinBoxes();

        assertEquals(coinBoxList.size(), coinList.size());

        int remaining = coinList.size();

        for (BigDecimal coin : coinList) {
            for (CoinBox box : coinBoxList) {
                if (box.getCoinValue().setScale(2).equals(coin)) {
                    remaining--;
                }
            }
        }

        assertEquals(0, remaining);
    }

    /**
     * Creates coin boxes for testing.
     * 
     * @return the mocked coin box.
     */
    private CoinBoxes getMockedCoinBoxes() {
        CoinBoxes coinBoxes = new CoinBoxes();

        CoinBox cb1 = new CoinBox(BigDecimal.valueOf(0.5), 5, 10);
        coinBoxes.getCoinBoxes().add(cb1);

        CoinBox cb2 = new CoinBox(BigDecimal.valueOf(1.0), 6, 8);
        coinBoxes.getCoinBoxes().add(cb2);

        CoinBox cb3 = new CoinBox(BigDecimal.valueOf(2.0), 3, 7);
        coinBoxes.getCoinBoxes().add(cb3);

        return coinBoxes;
    }
}
