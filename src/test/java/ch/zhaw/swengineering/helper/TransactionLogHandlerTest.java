package ch.zhaw.swengineering.helper;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import ch.zhaw.swengineering.model.persistence.TransactionLog;
import ch.zhaw.swengineering.model.persistence.TransactionLogEntry;
import ch.zhaw.swengineering.setup.ParkingMeterRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = ParkingMeterRunner.class, loader = AnnotationConfigContextLoader.class)
public class TransactionLogHandlerTest {

    @InjectMocks
    private TransactionLogHandler transactionLogHandler;

    @Mock
    private ConfigurationProvider configurationProvider;

    @Mock
    private ConfigurationWriter configurationWriter;

    private TransactionLog transactionLog;

    @Before
    public void setUp() throws ParseException {

        MockitoAnnotations.initMocks(this);

        // Mock
        transactionLog = getMock();

        when(configurationProvider.get()).thenReturn(transactionLog);

        transactionLogHandler.init();
    }

    @Test
    public void gettingAllItemsReturnsAllItems() throws Exception {

        // Run
        List<TransactionLogEntry> result = transactionLogHandler.getAll();

        // Assert
        assertEquals(result.size(), transactionLog.entries.size());
    }

    @Test
    public void aksingForTheEntriesOfTheLast24HoursReturnsOnlyEntriesOfTheLast24Hours() {

        // Mock
        Calendar withingRangeCalendar = Calendar.getInstance();
        withingRangeCalendar.add(Calendar.HOUR, -1);
        String text1 = "today log message";
        Date withingRangeDate = withingRangeCalendar.getTime();

        final TransactionLogEntry entry1 = new TransactionLogEntry();
        entry1.creationTime = withingRangeDate;
        entry1.text = text1;

        Calendar outOfRangeCalendar = Calendar.getInstance();
        outOfRangeCalendar.add(Calendar.DATE, -1);
        outOfRangeCalendar.add(Calendar.HOUR, -1);
        Date outOfRangeDate = outOfRangeCalendar.getTime();
        String text2 = "yesterday log message";

        final TransactionLogEntry entry2 = new TransactionLogEntry();
        entry2.creationTime = outOfRangeDate;
        entry2.text = text2;

        TransactionLog transactionLog = new TransactionLog();
        transactionLog.entries = new ArrayList<TransactionLogEntry>() {
            {
                add(entry1);
                add(entry2);
            }
        };

        when(configurationProvider.get()).thenReturn(transactionLog);

        transactionLogHandler.init();

        // Run
        List<TransactionLogEntry> result = transactionLogHandler.getLast24Hours();

        // Assert
        assertEquals(1, result.size());
        assertEquals(withingRangeDate, result.get(0).creationTime);
        assertEquals(text1, result.get(0).text);
    }

    @Test
    public void checkIfMessagesAreInCorrectOrder() throws Exception {

        // Run
        List<TransactionLogEntry> result = transactionLogHandler.getAll();

        // Assert
        assertEquals(result.get(0).text, transactionLog.entries.get(0).text);
        assertEquals(result.get(1).text, transactionLog.entries.get(1).text);
        assertEquals(result.get(2).text, transactionLog.entries.get(2).text);
    }

    @Test
    public void askingFor2EntriesReturns2Entries() throws Exception {
        int numberOfEntries = 2;

        // Run
        List<TransactionLogEntry> result = transactionLogHandler
                .get(numberOfEntries);

        // Assert
        assertEquals(result.size(), numberOfEntries);
    }

    @Test
    public void askingFor2EntriesReturnsTheLast2Entries() throws Exception {
        int numberOfEntries = 2;

        // Run
        List<TransactionLogEntry> result = transactionLogHandler
                .get(numberOfEntries);

        // Assert
        assertEquals(result.get(0).text, transactionLog.entries.get(1).text);
        assertEquals(result.get(1).text, transactionLog.entries.get(2).text);
    }

    @Test
    public void addingAnEntryToTheTransactionLogPassesCorrectObjectToConfigurationWriter()
            throws Exception {

        String text = "New Item";

        final Object[] tempTransactionLog = new Object[1];
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                tempTransactionLog[0] = args[0];
                return null;
            }
        }).when(configurationWriter).write(any(Object.class));

        // Run
        transactionLogHandler.write(text);

        // Assert
        TransactionLog newTransactionLog = (TransactionLog) tempTransactionLog[0];
        assertEquals(newTransactionLog.entries.size(), 4);
        assertEquals(newTransactionLog.entries.get(3).text, text);
    }

    private static TransactionLog getMock() throws ParseException {
        final TransactionLogEntry entry1 = new TransactionLogEntry();
        entry1.creationTime = new SimpleDateFormat("dd/MM/yyyy")
                .parse("21/12/2012");
        entry1.text = "m1";

        final TransactionLogEntry entry2 = new TransactionLogEntry();
        entry2.creationTime = new SimpleDateFormat("dd/MM/yyyy")
                .parse("22/12/2012");
        entry2.text = "m2";

        // Within the last 24 hours
        final TransactionLogEntry entry3 = new TransactionLogEntry();
        entry3.text = "m3";
        Calendar withingRangeCalendar = Calendar.getInstance();
        withingRangeCalendar.add(Calendar.HOUR, -1);
        entry3.creationTime = withingRangeCalendar.getTime();

        TransactionLog transactionLog = new TransactionLog();
        transactionLog.entries = new ArrayList<TransactionLogEntry>() {
            {
                add(entry1);
                add(entry2);
                add(entry3);
            }
        };
        return transactionLog;
    }
}
