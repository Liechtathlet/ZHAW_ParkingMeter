package ch.zhaw.swengineering.controller;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import ch.zhaw.swengineering.setup.ParkingMeterRunner;
import ch.zhaw.swengineering.view.SimulationView;

/**
 * @author Daniel Brun
 * 
 *         Tests the Class: 'ViewControllerImpl'.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = ParkingMeterRunner.class, loader = AnnotationConfigContextLoader.class)
public class ViewControllerImplTest {

    @Mock
    private SimulationView view;

    @InjectMocks
    private ViewControllerImpl controller;

    /**
     * Set up a test case.
     */
    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        // TODO: Die Methode addViewEventListener kann irgendwie nicht gemockt
        // werden, es wird immer die Implementation aufgerufen, welche in einem
        // NullPointer endet
        // doNothing().when(view).addViewEventListener(controller);
    }

    @Test
    public void checkStartMethodCallsCorrectMethods() throws IOException {

        // TODO sl: Is this a good test? What can be tested on the controller?

        // Run
        controller.start();

        // Assert
        verify(view).addViewEventListener(controller);
        verify(view).startSimulationView();
        verify(view).promptForParkingLotNumber();
    }
}
