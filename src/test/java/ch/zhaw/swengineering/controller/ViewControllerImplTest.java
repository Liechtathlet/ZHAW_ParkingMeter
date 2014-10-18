package ch.zhaw.swengineering.controller;

import ch.zhaw.swengineering.setup.ParkingMeterRunner;
import ch.zhaw.swengineering.view.SimulationView;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import java.io.IOException;

import static org.mockito.Mockito.verify;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes=ParkingMeterRunner.class, loader=AnnotationConfigContextLoader.class)
public class ViewControllerImplTest {

	@InjectMocks
	private ViewControllerImpl controller;

	@Mock
	private SimulationView view;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
	}


	@Test
	public void checkStartMethodCallsCorrectMethods() throws IOException {

		// TODO sl: Is this a good test? What can be tested on the controller?

		// Run
		controller.start();

		// Assert
		verify(view).addViewEventListener(controller);
		verify(view).startSimulationView();
		verify(view).showParkingLotMessage();
	}
}