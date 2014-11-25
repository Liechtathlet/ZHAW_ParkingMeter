package ch.zhaw.swengineering.setup;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.nio.charset.Charset;

import javax.xml.bind.JAXBException;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import ch.zhaw.swengineering.helper.ConfigurationProvider;
import ch.zhaw.swengineering.helper.ConfigurationWriter;
import ch.zhaw.swengineering.view.SimulationView;
import ch.zhaw.swengineering.view.console.ConsoleSimulationView;
import ch.zhaw.swengineering.view.gui.GuiSimulationView;

@Configuration
public class DependencyInjectionConfig {

    private static final Logger LOG = LogManager
            .getLogger(ParkingMeterRunner.class);

    @Autowired
    private Environment env;

    @Bean
    public BufferedReader bufferedReader() {
        return new BufferedReader(new InputStreamReader(System.in,
                Charset.defaultCharset()));
    }

    @Bean
    public PrintStream printWriter() {
        return System.out;
    }

    @Bean
    public SimulationView simulationView() {
        String versionParameter = null;

        if (env != null && env.getProperty("nonOptionArgs") != null) {
            versionParameter = env.getProperty("nonOptionArgs").toLowerCase();
        }

        if (versionParameter != null) {

            if (versionParameter.equals("gui")) {
                LOG.info("Detected startup parameter for gui version. Loading GUI view.");
                return new GuiSimulationView();
            }
        }

        LOG.info("Detected startup parameter for console version. Loading console view.");
        return new ConsoleSimulationView();
    }

    @Bean
    @Qualifier("coinBoxes")
    public ConfigurationProvider coinBoxesConfigurationProvider() {
        return new ConfigurationProvider("./src/main/resources/coinBoxes.xml",
                "ch.zhaw.swengineering.model.persistence.CoinBoxes");
    }

    @Bean
    @Qualifier("coinBoxes")
    public ConfigurationWriter coinBoxesConfigurationWriter()
            throws JAXBException {
        return new ConfigurationWriter("./src/main/resources/coinBoxes.xml",
                "ch.zhaw.swengineering.model.persistence.CoinBoxes");
    }

    @Bean
    @Qualifier("messages-de")
    public ConfigurationProvider messagesDeConfigurationProvider() {
        return new ConfigurationProvider(
                "./src/main/resources/messages-de.xml",
                "ch.zhaw.swengineering.model.persistence.Messages");
    }

    @Bean
    @Qualifier("secretCodes")
    public ConfigurationProvider secretCodesConfigurationProvider() {
        return new ConfigurationProvider(
                "./src/main/resources/secretCodes.xml",
                "ch.zhaw.swengineering.model.persistence.SecretCodes");
    }

    @Bean
    @Qualifier("parkingMeter")
    public ConfigurationProvider parkingMeterConfigurationProvider() {
        return new ConfigurationProvider(
                "./src/main/resources/parkingMeter.xml",
                "ch.zhaw.swengineering.model.persistence.ParkingMeter");
    }

    @Bean
    @Qualifier("parkingMeter")
    public ConfigurationWriter parkingMeterConfigurationWriter()
            throws JAXBException {
        return new ConfigurationWriter("./src/main/resources/parkingMeter.xml",
                "ch.zhaw.swengineering.model.persistence.ParkingMeter");
    }

    @Bean
    @Qualifier("transactionLog")
    public ConfigurationProvider transactionLogConfigurationProvider() {
        return new ConfigurationProvider(
                "./src/main/resources/transactionLog.xml",
                "ch.zhaw.swengineering.model.persistence.TransactionLog");
    }

    @Bean
    @Qualifier("transactionLog")
    public ConfigurationWriter transactionLogConfigurationWriter()
            throws JAXBException {
        return new ConfigurationWriter(
                "./src/main/resources/transactionLog.xml",
                "ch.zhaw.swengineering.model.persistence.TransactionLog");
    }

}
