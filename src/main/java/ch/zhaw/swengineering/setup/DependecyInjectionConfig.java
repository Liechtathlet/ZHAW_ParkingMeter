package ch.zhaw.swengineering.setup;

import ch.zhaw.swengineering.view.SimulationView;
import ch.zhaw.swengineering.view.console.ConsoleSimulationView;
import ch.zhaw.swengineering.view.gui.GuiSimulationView;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.BufferedReader;
import java.io.InputStreamReader;

@Configuration
public class DependecyInjectionConfig {

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
}
