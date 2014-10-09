package ch.zhaw.swengineering.setup;

import java.util.Arrays;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ImportResource;

import ch.zhaw.swengineering.LogAndXmlService;
import ch.zhaw.swengineering.view.SimulationViewActionHandler;

/**
 * @author Daniel Brun
 * 
 *         Starter class for the Parking Meter. Runs Spring-Boot,
 *         Spring-WebServer and the ParkingMeter Console or GUI.
 */
@ComponentScan("ch.zhaw.swengineering")
@EnableAutoConfiguration
@ImportResource("classpath:beans.xml")
public class ParkingMeterRunner {

	private static final Logger LOG = LogManager.getLogger(ParkingMeterRunner.class);
	
	/**
	 * Entry-Method for the Java-Application
	 * 
	 * @param args
	 *            The arguments.
	 */
	public static void main(String[] args) {

		LOG.info("Init application startup. Arguments: " + Arrays.toString(args));
		
		// Start Spring
		ConfigurableApplicationContext context = SpringApplication.run(
				ParkingMeterRunner.class, args);

		SimulationViewActionHandler viewHandler = null;
		
		if (args != null && args.length == 1) {
			String versionParameter = args[0].trim().toLowerCase();

			if (versionParameter.equals("gui")) {
				LOG.info("Detected startup parameter for gui-version. Init gui...");
				viewHandler = context.getBean("gui",SimulationViewActionHandler.class);
			}
		}
		
		if(viewHandler == null){
			viewHandler = context.getBean("console", SimulationViewActionHandler.class);
		}
		
		viewHandler.startSimulationGui();
		
		RunLogAndXmlService(context);
	}

	/**
	 * FIXME: Only for Test, delete after first implementation
	 * 
	 * @param context
	 */
	private static void RunLogAndXmlService(
			ConfigurableApplicationContext context) {
		// org.springframework.context.ApplicationContext context = new
		// AnnotationConfigApplicationContext(TestApp.class);
		LogAndXmlService service = context.getBean(LogAndXmlService.class);
		System.out.println(service.run());
	}

}
