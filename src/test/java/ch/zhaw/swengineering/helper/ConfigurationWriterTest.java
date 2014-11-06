package ch.zhaw.swengineering.helper;

import ch.zhaw.swengineering.model.persistence.TransactionLog;
import ch.zhaw.swengineering.model.persistence.TransactionLogEntry;
import ch.zhaw.swengineering.setup.ParkingMeterRunner;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import static org.junit.Assert.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes=ParkingMeterRunner.class, loader=AnnotationConfigContextLoader.class)
public class ConfigurationWriterTest {


	@Autowired
	@Qualifier("transactionLog")
	private ConfigurationWriter transactionLogConfigurationWriter;

	@Test
	public void checkIfConfigurationWriterCanBeResolved() throws Exception {
		assertNotNull(transactionLogConfigurationWriter);
	}

	@Test
	@Ignore("!ATTENTION!WARNING!CAUTION! This test overwrites the transactionLog.xml file.")
	public void gettingBarWhenAskingForFoo() throws Exception {
		transactionLogConfigurationWriter.write(getMock());
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