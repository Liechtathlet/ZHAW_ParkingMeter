package ch.zhaw.swengineering.helper;

import ch.zhaw.swengineering.model.CoinBoxes;
import ch.zhaw.swengineering.model.Messages;
import ch.zhaw.swengineering.model.ParkingTimes;
import ch.zhaw.swengineering.model.SecretCodes;
import ch.zhaw.swengineering.setup.ParkingMeterRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

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

	@Test
	public void canDeserializeParkingTimeDefXml() throws Exception {
		ParkingTimes parkingTimes = (ParkingTimes) parkingTimeConfigurationProvider.get();
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
}