package ch.zhaw.swengineering.helper;

import ch.zhaw.swengineering.model.TransactionLog;
import ch.zhaw.swengineering.model.TransactionLogEntry;
import ch.zhaw.swengineering.setup.ParkingMeterRunner;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes=ParkingMeterRunner.class, loader=AnnotationConfigContextLoader.class)
public class TransactionLogHandlerTest {

	@InjectMocks
	private TransactionLogHandler transactionLogHandler;

	@Mock
	private ConfigurationProvider configurationProvider;

	@Mock
	private ConfigurationWriter configurationWriter;

	@Test
	public void gettingAllItemsReturnsAllItems() throws Exception {
		// Mock
		TransactionLog transactionLog = getMock();
		MockitoAnnotations.initMocks(this);
		when(configurationProvider.get()).thenReturn(transactionLog);

		// Run
		List<TransactionLogEntry> result = transactionLogHandler.getAll();

		// Assert
		assertEquals(result.size(), transactionLog.entries.size());
	}

	@Test
	public void checkIfMessagesAreInCorrectOrder() throws Exception {
		// Mock
		TransactionLog transactionLog = getMock();
		MockitoAnnotations.initMocks(this);
		when(configurationProvider.get()).thenReturn(transactionLog);

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

		// Mock
		TransactionLog transactionLog = getMock();
		MockitoAnnotations.initMocks(this);
		when(configurationProvider.get()).thenReturn(transactionLog);

		// Run
		List<TransactionLogEntry> result = transactionLogHandler.get(numberOfEntries);

		// Assert
		assertEquals(result.size(), numberOfEntries);
	}

	@Test
	public void askingFor2EntriesReturnsTheLast2Entries() throws Exception {
		int numberOfEntries = 2;

		// Mock
		TransactionLog transactionLog = getMock();
		MockitoAnnotations.initMocks(this);
		when(configurationProvider.get()).thenReturn(transactionLog);

		// Run
		List<TransactionLogEntry> result = transactionLogHandler.get(numberOfEntries);

		// Assert
		assertEquals(result.get(0).text, transactionLog.entries.get(1).text);
		assertEquals(result.get(1).text, transactionLog.entries.get(2).text);
	}

	@Test
	public void addingAnEntryToTheTransactionLogPassesCorrectObjectToConfigurationWriter() throws Exception {
		String text = "New Item";

		// Mock
		TransactionLog transactionLog = getMock();
		MockitoAnnotations.initMocks(this);

		when(configurationProvider.get()).thenReturn(transactionLog);

		// TODO: Not nicely done. How to check parameter to configurationWriter.write(...)?
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
		TransactionLog newTransactionLog = (TransactionLog)tempTransactionLog[0];
		assertEquals(newTransactionLog.entries.size(), 4);
		assertEquals(newTransactionLog.entries.get(3).text, text);
	}

	private TransactionLog getMock() throws ParseException {
		final TransactionLogEntry entry1 = new TransactionLogEntry();
		entry1.creationTime = new SimpleDateFormat("dd/MM/yyyy").parse("21/12/2012");
		entry1.text = "m1";
		final TransactionLogEntry entry2 = new TransactionLogEntry();
		entry2.creationTime = new SimpleDateFormat("dd/MM/yyyy").parse("22/12/2012");
		entry2.text = "m2";
		final TransactionLogEntry entry3 = new TransactionLogEntry();
		entry3.creationTime = new SimpleDateFormat("dd/MM/yyyy").parse("23/12/2012");
		entry3.text = "m3";

		TransactionLog transactionLog = new TransactionLog();
		transactionLog.entries = new ArrayList<TransactionLogEntry>() {{
			add(entry1);
			add(entry2);
			add(entry3);
		}};
		return transactionLog;
	}
}