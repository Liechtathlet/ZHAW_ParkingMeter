package ch.zhaw.swengineering.helper;

import ch.zhaw.swengineering.model.persistence.*;
import ch.zhaw.swengineering.setup.ParkingMeterRunner;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import java.util.Date;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes=ParkingMeterRunner.class, loader=AnnotationConfigContextLoader.class)
public class ConfigurationProviderTest {

	@Autowired
	@Qualifier("parkingTimeDef")
	private ConfigurationProvider parkingTimeConfigurationProvider;

	@Autowired
	@Qualifier("coinBoxes")
	private ConfigurationProvider coinBoxesConfigurationProvider;

	@Autowired
	@Qualifier("messages-de")
	private ConfigurationProvider messagesConfigurationProvider;

	@Autowired
	@Qualifier("secretCodes")
	private ConfigurationProvider secretCodesConfigurationProvider;

	@Autowired
	@Qualifier("parkingMeter")
	private ConfigurationProvider parkingMeterConfigurationProvider;

	@Autowired
	@Qualifier("transactionLog")
	private ConfigurationProvider transactionLogConfigurationProvider;

	@Test
	public void canDeserializeParkingTimeDefXml() throws Exception {
		ParkingTimeDefinitions parkingTimes = (ParkingTimeDefinitions) parkingTimeConfigurationProvider.get();
		assertNotNull(parkingTimes);
	}

	@Test
	public void canDeserializeCoinBoxesXml() throws Exception {
		CoinBoxes coinBoxes = (CoinBoxes) coinBoxesConfigurationProvider.get();
		assertNotNull(coinBoxes);
	}

	@Test
	public void canDeserializeMessagesXml() throws Exception {
		Messages messages = (Messages) messagesConfigurationProvider.get();
		assertNotNull(messages);
	}

	@Test
	public void canDeserializeSecretCodesXml() throws Exception {
		SecretCodes secretCodes = (SecretCodes) secretCodesConfigurationProvider.get();
		assertNotNull(secretCodes);
	}

	@Test
	public void canDeserializeParkingMeterXml() throws Exception {
		ParkingMeter parkingMeter = (ParkingMeter) parkingMeterConfigurationProvider.get();
		assertNotNull(parkingMeter);
	}

	@Test
	public void canDeserializeTransactionLogXml() throws Exception {
		TransactionLog transactionLog = (TransactionLog) transactionLogConfigurationProvider.get();
		assertNotNull(transactionLog);
	}
}