package ch.zhaw.swengineering.view.console;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.format.datetime.DateFormatter;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import ch.zhaw.swengineering.event.ParkingLotEnteredEvent;
import ch.zhaw.swengineering.event.ViewEventListener;
import ch.zhaw.swengineering.helper.MessageProvider;
import ch.zhaw.swengineering.setup.ParkingMeterRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = ParkingMeterRunner.class, loader = AnnotationConfigContextLoader.class)
public class ConsoleSimulationViewTest {

    /**
     * Date-Format.
     */
    private static final String DATE_FORMAT = "dd.MM.YYYY HH:mm";

    // Replacement for the command line
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();

    private ViewEventListener listener;

    @InjectMocks
    private ConsoleSimulationView view;

    @Mock
    private MessageProvider messageProvider;

    @Mock
    private BufferedReader bufferedReader;

    private DateFormatter dateFormatter;

    /**
     * Creates a new instance of this class.
     */
    public ConsoleSimulationViewTest() {
        dateFormatter = new DateFormatter(DATE_FORMAT);
    }

    @Before
    public void setUp() {
        // Set replacement "command line".
        System.setOut(new PrintStream(outContent));

        MockitoAnnotations.initMocks(this);

        listener = mock(ViewEventListener.class);
    }

    @After
    public void tearDown() {
        // Clean up our "command line" for the next test.
        System.setOut(null);
    }

    @Test
    public void whenEnteredAParkingLotNumberAnEventShouldBeFired()
            throws IOException {
        final int expectedParkingLotNumber = 44;

        // Mock
        when(messageProvider.get("view.enter.parkinglotnumber")).thenReturn("");
        when(bufferedReader.readLine()).thenReturn(
                Integer.toString(expectedParkingLotNumber));

        view.addViewEventListener(listener);

        // Run
        view.executeActionsForStateEnteringParkingLotNumber();

        // Assert
        ArgumentCaptor<ParkingLotEnteredEvent> argument = ArgumentCaptor
                .forClass(ParkingLotEnteredEvent.class);
        verify(listener).parkingLotEntered(argument.capture());
        assertEquals(expectedParkingLotNumber, argument.getValue()
                .getParkingLotNumber());
    }

    @Test
    public final void whenEnteredAStringItShouldNotGenerateAnEvent()
            throws IOException {
        String expectedSequence = "Enter: Invalid" + System.lineSeparator();
        String invalidParkingLotNumber = "invalid parkinglot number";

        // Mock
        when(messageProvider.get("view.enter.parkinglotnumber")).thenReturn(
                "Enter");
        when(messageProvider.get("view.enter.invalid")).thenReturn("Invalid");
        when(bufferedReader.readLine()).thenReturn(invalidParkingLotNumber);

        // Setup
        view.addViewEventListener(listener);

        // Run
        view.executeActionsForStateEnteringParkingLotNumber();

        // Assert
        assertEquals(expectedSequence, outContent.toString());

        // Verify that no notification was sent about the entered data.
        verify(listener, Mockito.times(0)).parkingLotEntered(
                any(ParkingLotEnteredEvent.class));
    }

    @Test
    public final void testStateOfPromptForParkingLot() {
        // Check init state.
        assertEquals(ConsoleViewStateEnum.INIT, view.getViewState());

        // Run
        view.promptForParkingLotNumber();

        // check state after prompt
        assertEquals(ConsoleViewStateEnum.ENTERING_PARKING_LOT,
                view.getViewState());
    }

    @Test
    public final void WhenAskedForParkingLotNumberItPrintsText()
            throws IOException {
        String parkingLotText = "Parkplatznummer eingeben";
        String expectedQuestion = parkingLotText + ": ";

        // Mock
        when(messageProvider.get("view.enter.parkinglotnumber")).thenReturn(
                parkingLotText);
        when(bufferedReader.readLine()).thenReturn("0");

        // Run
        view.executeActionsForStateEnteringParkingLotNumber();

        // Assert
        assertEquals(expectedQuestion, outContent.toString());
    }

    @Test
    public void WhenASpaceIsAtTheEndOfTheTextTrimTheText() throws IOException {
        String parkingLotText = "Parkplatznummer eingeben:     ";
        String expectedQuestion = parkingLotText.trim() + ": ";

        // Mock
        when(messageProvider.get("view.enter.parkinglotnumber")).thenReturn(
                parkingLotText);
        when(bufferedReader.readLine()).thenReturn("0");

        // Run
        view.executeActionsForStateEnteringParkingLotNumber();

        // Assert
        assertEquals(expectedQuestion, outContent.toString());
    }

    @Test
    public void testDisplayParkingLotAndParkingTime() throws IOException {
        Calendar cal = Calendar.getInstance();

        final int expectedParkingLotNumber = 44;
        final Date expectedParkingDate = cal.getTime();
        String expectedOutput = expectedParkingLotNumber + ":"
                + dateFormatter.print(expectedParkingDate, Locale.GERMAN) + System.lineSeparator();

        // Mock
        when(messageProvider.get("view.info.parkingTime"))
                .thenReturn("{0}:{1}");

        // Run
        view.displayParkingLotNumberAndParkingTime(expectedParkingLotNumber,
                expectedParkingDate);

        // Assert
        assertEquals(expectedOutput, outContent.toString());
    }
}
