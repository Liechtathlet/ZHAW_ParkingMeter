package ch.zhaw.swengineering.setup;

import java.util.Arrays;

//import ch.zhaw.swengineering.helper.ParkingMeterBeanProcessor;
import ch.zhaw.swengineering.view.SimulationView;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ImportResource;

import ch.zhaw.swengineering.controller.ViewController;

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

	private static AutowireCapableBeanFactory beanFactory;
	/**
	 * Entry-Method for the Java-Application
	 * 
	 * @param args
	 *            The arguments.
	 */
	public static void main(String[] args) {

		LOG.info("Init application startup. Arguments: " + Arrays.toString(args));

		//Java based configuration: http://www.ibm.com/developerworks/library/ws-springjava/, http://spring.io/blog/2009/12/22/configuration-simplifications-in-spring-3-0/
		
		// Start Spring
		ConfigurableApplicationContext context = SpringApplication.run(
				ParkingMeterRunner.class, args);
//		context.addBeanFactoryPostProcessor(new ParkingMeterBeanProcessor());
//		context.refresh();

		SimulationView viewHandler = null;
		ViewController viewController = null;
		
		if (args != null && args.length == 1) {
			String versionParameter = args[0].trim().toLowerCase();

			if (versionParameter.equals("gui")) {
				LOG.info("Detected startup parameter for gui-version. Init gui...");
				
				//TODO: Find solution for GUI with Spring-Boot
				//TODO: switch between gui and console with configuration parameter or factory...
				//viewHandler = context.getBean("gui",SimulationViewActionHandler.class);
			}
		}

		viewController = context.getBean(ViewController.class);

		viewController.start();
	}
}
