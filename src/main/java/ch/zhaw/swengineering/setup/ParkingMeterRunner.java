package ch.zhaw.swengineering.setup;

import java.util.Arrays;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ImportResource;
import org.springframework.core.env.CommandLinePropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.core.env.SimpleCommandLinePropertySource;

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

    private static final Logger LOG = LogManager
            .getLogger(ParkingMeterRunner.class);

    /**
     * Entry-Method for the Java-Application.
     * 
     * @param args
     *            The arguments.
     */
    public static void main(final String[] args) {
        System.setProperty("java.awt.headless", "false");

        LOG.info("Init application startup. Arguments: "
                + Arrays.toString(args));

        SpringApplication app = null;
        ConfigurableApplicationContext context = null;

        // Start Spring
        app = new SpringApplication(ParkingMeterRunner.class);

        PropertySource<?> clps = new SimpleCommandLinePropertySource(args);
        
        context = app.run(args);
        context.getEnvironment().getPropertySources().addFirst(clps);
        context.getBean(ViewController.class).start();
    }
}
