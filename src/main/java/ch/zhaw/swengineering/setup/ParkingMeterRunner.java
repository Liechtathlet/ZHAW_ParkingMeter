package ch.zhaw.swengineering.setup;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;

import ch.zhaw.swengineering.LogAndXmlService;

/**
 * @author Daniel Brun
 * 
 *         Starter class for the Parking Meter. Runs Spring-Boot,
 *         Spring-WebServer and the ParkingMeter Console or GUI.
 */
@ComponentScan("ch.zhaw.swengineering")
@EnableAutoConfiguration
public class ParkingMeterRunner {

	/**
	 * Entry-Method for the Java-Application
	 * 
	 * @param args The arguments.
	 */
	public static void main(String[] args) {
		
		//TODO: Detect Parameter (GUI or Console), Default: Console
		
		//Start Spring
		ConfigurableApplicationContext context = SpringApplication.run(
				ParkingMeterRunner.class, args);
		
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
