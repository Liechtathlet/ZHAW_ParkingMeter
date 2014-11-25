package ch.zhaw.swengineering.controller;

import ch.zhaw.swengineering.business.ParkingMeter;
import ch.zhaw.swengineering.helper.TransactionLogHandler;
import ch.zhaw.swengineering.slotmachine.controller.IntelligentSlotMachineBackendInteractionInterface;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.verify;

public class RestControllerTest {

    @Mock
    private TransactionLogHandler transactionLogHandler;

    @Mock
    private ParkingMeter parkingMeter;

    @Mock
    private IntelligentSlotMachineBackendInteractionInterface slotMachine;

    @InjectMocks
    private RestController restController;


    /**
     * Sets up a test case.
     */
    @Before
    public final void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testTransactionLogNEntriesGetsAllMessagesByDefault() {
        // Run
        restController.transactionLogNEntries(null);

        // Assert
        verify(transactionLogHandler).getAll();
    }

    @Test
    public void testTransactionLogNEntriesGetsAllMessagesIf3EntriesAreRequested() {
        Integer count = 3;

        // Run
        restController.transactionLogNEntries(count.toString());

        // Assert
        verify(transactionLogHandler).get(count);
    }

    @Test
    public void testTransactionLogNEntriesGetsAllMessagesIf0EntriesAreRequested() {
        Integer count = 0;

        // Run
        restController.transactionLogNEntries(count.toString());

        // Assert
        verify(transactionLogHandler).get(count);
    }

    @Test
    public void testTransactionLogNEntriesGetsAllMessagesIfMinus4EntriesAreRequested() {
        Integer count = -4;

        // Run
        restController.transactionLogNEntries(count.toString());

        // Assert
        verify(transactionLogHandler).get(count);
    }

    @Test
    public void testTransactionLog24HoursCallsCorrectMethod() {

        // Run
        restController.transactionLog24Hours();

        // Assert
        verify(transactionLogHandler).getLast24Hours();
    }

    @Test
    public void testParkingTimeDefinitionsCorrectMethod() {

        // Run
        restController.parkingTimeDefinitions();

        // Assert
        verify(parkingMeter).getParkingTimeDefinitions();
    }
}