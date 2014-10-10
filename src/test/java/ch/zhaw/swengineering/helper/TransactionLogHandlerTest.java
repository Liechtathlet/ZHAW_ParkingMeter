package ch.zhaw.swengineering.helper;

import ch.zhaw.swengineering.model.TransactionLog;
import ch.zhaw.swengineering.model.TransactionLogEntry;
import ch.zhaw.swengineering.setup.ParkingMeterRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes=ParkingMeterRunner.class, loader=AnnotationConfigContextLoader.class)
public class TransactionLogHandlerTest {

	@InjectMocks
	private TransactionLogHandler transactionLogHandler;

	@Mock
	private ConfigurationProvider configurationProvider;

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