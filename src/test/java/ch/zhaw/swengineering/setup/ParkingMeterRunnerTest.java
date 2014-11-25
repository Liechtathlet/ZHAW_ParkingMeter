package ch.zhaw.swengineering.setup;

import ch.zhaw.swengineering.controller.ViewController;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class ParkingMeterRunnerTest {

    @Mock
    private SpringApplication springApplication;

    @Mock
    private ConfigurableApplicationContext context;

    @Mock
    private ViewController viewController;

    @Spy
    private ParkingMeterRunner runner = new ParkingMeterRunner();

    @Test
    public void testMain() throws Exception {


        // Mock
        MockitoAnnotations.initMocks(this);
        doReturn(springApplication).when(runner).getSpringApplication();
        doReturn(context).when(springApplication).run();
        doReturn(viewController).when(context).getBean(ViewController.class);

        // Run
        runner.start(new String[0]);

        // Assert
        verify(runner).getSpringApplication();
        verify(springApplication).run();
    }

    @Test
    public void testGetArgumentsGetsTheCorrectArguments() throws Exception {


        // Mock
        String[] expectedArguments = new String[1];
        expectedArguments[0] = "gui";
        ParkingMeterRunner.arguments = expectedArguments;

        // Run
        String[] arguments = ParkingMeterRunner.getArguments();

        // Assert
        assertEquals(expectedArguments, arguments);
    }
}