package ch.zhaw.swengineering.view.console;

import ch.zhaw.swengineering.event.ParkingLotEnteredEvent;
import ch.zhaw.swengineering.event.ViewEventListener;
import ch.zhaw.swengineering.helper.MessageProvider;
import ch.zhaw.swengineering.setup.ParkingMeterRunner;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes=ParkingMeterRunner.class, loader=AnnotationConfigContextLoader.class)
public class ConsoleSimulationViewTest {

	// Replacement for the command line
	private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();

	private ViewEventListener listener;

	@Before
	public void setUp() {
		// Set replacement "command line".
		System.setOut(new PrintStream(outContent));
	}

	@After
	public void tearDown() {
		// Clean up our "command line" for the next test.
		System.setOut(null);
	}

	@InjectMocks
	private ConsoleSimulationView view;

	@Mock
	private MessageProvider messageProvider;

	@Mock
	private BufferedReader bufferedReader;

	@Test
	public void WhenAskedForParkingLotNumberItShouldReturn44() throws IOException {
		int expectedParkingLotNumber = 44;

		// Mock
		MockitoAnnotations.initMocks(this);
		when(messageProvider.get("parking lot number")).thenReturn("");
		when(bufferedReader.readLine()).thenReturn(Integer.toString(expectedParkingLotNumber));

		listener = mock(ViewEventListener.class);
		view.addViewEventListener(listener);

		// Run
		view.showParkingLotMessage();

		// Assert
		ArgumentCaptor<ParkingLotEnteredEvent> argument = ArgumentCaptor.forClass(ParkingLotEnteredEvent.class);
		verify(listener).parkingLotEntered(argument.capture());
		assertEquals(expectedParkingLotNumber, argument.getValue().getParkingLotNumber());
	}

	@Test(expected = NumberFormatException.class)
	public void WhenEnteredAStringItShouldItShouldThrowAnIOException() throws IOException {
		String invalidParkingLotNumber = "invalid parkinglot number";

		// Mock
		MockitoAnnotations.initMocks(this);
		when(messageProvider.get("parking lot number")).thenReturn("");
		when(bufferedReader.readLine()).thenReturn(invalidParkingLotNumber);

		// Run
		view.showParkingLotMessage();

		// Assert: Expecting NumberFormatException as defined by the Test annotation.
	}

	@Test
	public void WhenAskedForParkingLotNumberItPrintsText() throws IOException {
		String parkingLotText = "Parkplatznummer eingeben:";
		String expectedQuestion = parkingLotText + " ";

		// Mock
		MockitoAnnotations.initMocks(this);
		when(messageProvider.get("parking lot number")).thenReturn(parkingLotText);
		when(bufferedReader.readLine()).thenReturn("0");

		// Run
		view.getParkingLotNumber();

		// Assert
		assertEquals(expectedQuestion, outContent.toString());
	}

	@Test
	public void WhenASpaceIsAtTheEndOfTheTextTrimTheText() throws IOException {
		String parkingLotText = "Parkplatznummer eingeben:     ";
		String expectedQuestion = parkingLotText.trim() + " ";

		// Mock
		MockitoAnnotations.initMocks(this);
		when(messageProvider.get("parking lot number")).thenReturn(parkingLotText);
		when(bufferedReader.readLine()).thenReturn("0");

		// Run
		view.getParkingLotNumber();

		// Assert
		assertEquals(expectedQuestion, outContent.toString());
	}
}