package ch.zhaw.swengineering.setup;

import java.util.Arrays;

//import ch.zhaw.swengineering.setup.ParkingMeterBeanProcessor;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
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

	private static String[] args;

	/**
	 * Entry-Method for the Java-Application
	 *
	 * @param args
	 *            The arguments.
	 */
	public static void main(String[] args) {

		ParkingMeterRunner.args = args;

		LOG.info("Init application startup. Arguments: " + Arrays.toString(args));

		//Java based configuration: http://www.ibm.com/developerworks/library/ws-springjava/, http://spring.io/blog/2009/12/22/configuration-simplifications-in-spring-3-0/

		// Start Spring
		ConfigurableApplicationContext context = SpringApplication.run(
				ParkingMeterRunner.class, args);

		context.getBean(ViewController.class).start();
	}

	public static String[] getArgs() {
		return args;
	}
}
