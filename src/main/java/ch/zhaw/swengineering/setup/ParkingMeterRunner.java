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

    /**
     * The Logger.
     */
    private static final Logger LOG = LogManager
            .getLogger(ParkingMeterRunner.class);

    /**
     * The arguments.
     */
    static String[] arguments;

    /**
     * Entry-Method for the Java-Application.
     * 
     * @param args
     *            The arguments.
     */
    public static void main(final String[] args) {
        new ParkingMeterRunner().start(args);
    }

    void start(String[] args) {
        System.setProperty("java.awt.headless", "false");

        ParkingMeterRunner.arguments = args;

        LOG.info("Init application startup. Arguments: "
                + Arrays.toString(args));

        SpringApplication app;
        ConfigurableApplicationContext context;

        // Start Spring
        app = getSpringApplication();

        context = app.run(args);
        context.getBean(ViewController.class).start();
    }

    SpringApplication getSpringApplication() {
        return new SpringApplication(ParkingMeterRunner.class);
    }

    /**
     * Gets the arguments which were set during application startup.
     * 
     * @return the arguments.
     */
    public static String[] getArguments() {
        return arguments;
    }
}
