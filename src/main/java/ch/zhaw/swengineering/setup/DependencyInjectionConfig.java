package ch.zhaw.swengineering.setup;

import ch.zhaw.swengineering.helper.ConfigurationProvider;
import ch.zhaw.swengineering.helper.ConfigurationWriter;
import ch.zhaw.swengineering.view.SimulationView;
import ch.zhaw.swengineering.view.console.ConsoleSimulationView;
import ch.zhaw.swengineering.view.gui.GuiSimulationView;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.BufferedReader;
import java.io.InputStreamReader;

@Configuration
public class DependencyInjectionConfig {

	private static final Logger LOG = LogManager.getLogger(ParkingMeterRunner.class);

	@Bean
	public BufferedReader bufferedReader() {
		return new BufferedReader(new InputStreamReader(System.in));
	}

	@Bean
	public SimulationView simulationView() {
		String[] args = ParkingMeterRunner.getArgs();

		if (args != null && args.length == 1) {
			String versionParameter = args[0].trim().toLowerCase();

			if (versionParameter.equals("gui")) {
				LOG.info("Detected startup parameter for gui-version. Loading GUI view");
				return new GuiSimulationView();
			}
		}

		LOG.info("Detected startup parameter for gui-version. Loading console view");
		return new ConsoleSimulationView();
	}

	@Bean
	@Qualifier("coinBoxes")
	public ConfigurationProvider coinBoxesConfigurationProvider() {
		return new ConfigurationProvider(
				"./src/main/resources/coinBoxes.xml",
				"ch.zhaw.swengineering.model.CoinBoxes");
	}

	@Bean
	@Qualifier("messages-de")
	public ConfigurationProvider messagesDeConfigurationProvider() {
		return new ConfigurationProvider(
				"./src/main/resources/messages-de.xml",
				"ch.zhaw.swengineering.model.Messages");
	}

	@Bean
	@Qualifier("secretCodes")
	public ConfigurationProvider secretCodesConfigurationProvider() {
		return new ConfigurationProvider(
				"./src/main/resources/secretCodes.xml",
				"ch.zhaw.swengineering.model.SecretCodes");
	}

	@Bean
	@Qualifier("parkingMeter")
	public ConfigurationProvider parkingMeterConfigurationProvider() {
		return new ConfigurationProvider(
				"./src/main/resources/parkingMeter.xml",
				"ch.zhaw.swengineering.model.ParkingMeter");
	}

	@Bean
	@Qualifier("transactionLog")
	public ConfigurationProvider transactionLogConfigurationProvider() {
		return new ConfigurationProvider(
				"./src/main/resources/transactionLog.xml",
				"ch.zhaw.swengineering.model.TransactionLog");
	}

	@Bean
	@Qualifier("transactionLog")
	public ConfigurationWriter transactionLogConfigurationWriter() {
		return new ConfigurationWriter(
				"./src/main/resources/transactionLog.xml",
				"ch.zhaw.swengineering.model.TransactionLog");
	}
}