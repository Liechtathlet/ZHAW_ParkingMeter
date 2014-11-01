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
import java.text.MessageFormat;
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

import ch.zhaw.swengineering.event.MoneyInsertedEvent;
import ch.zhaw.swengineering.event.ParkingLotEnteredEvent;
import ch.zhaw.swengineering.event.ShutdownEvent;
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

    private static final String MSG_KEY_ENTER_PARKING_LOT = "view.enter.parkinglotnumber";
    private static final String MSG_VAL_ENTER_PARKING_LOT = "EnterParkingLotNumber";

    private static final String MSG_KEY_ENTER_PARKING_LOT_INVALID = "view.enter.parkinglotnumber.invalid";
    private static final String MSG_VAL_ENTER_PARKING_LOT_INVALID = "EnterParkingLotNumberInvalid";

    private static final String MSG_KEY_ENTER_COINS = "view.enter.coins";
    private static final String MSG_VAL_ENTER_COINS = "Coins: {0}";

    private static final String MSG_KEY_SHUTDOWN = "application.bye";
    private static final String MSG_VAL_SHUTDOWN = "bye";

    // Replacement for the command line
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();

    @InjectMocks
    private ConsoleSimulationView view;

    @Mock
    private MessageProvider messageProvider;

    @Mock
    private BufferedReader bufferedReader;

    private DateFormatter dateFormatter;

    private ViewEventListener listener;

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

        // Init mockito
        MockitoAnnotations.initMocks(this);

        // Init mocks
        listener = mock(ViewEventListener.class);

        // Mocking message provider
        when(messageProvider.get(MSG_KEY_ENTER_PARKING_LOT)).thenReturn(
                MSG_VAL_ENTER_PARKING_LOT);

        when(messageProvider.get(MSG_KEY_ENTER_PARKING_LOT_INVALID))
                .thenReturn(MSG_VAL_ENTER_PARKING_LOT_INVALID);

        when(messageProvider.get(MSG_KEY_ENTER_COINS)).thenReturn(
                MSG_VAL_ENTER_COINS);

        when(messageProvider.get(MSG_KEY_SHUTDOWN))
                .thenReturn(MSG_VAL_SHUTDOWN);

        when(messageProvider.get("view.info.parkingTime"))
                .thenReturn("{0}:{1}");

        // Initialize view
        view.addViewEventListener(listener);
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

        // Mock User Input
        when(bufferedReader.readLine()).thenReturn(
                Integer.toString(expectedParkingLotNumber));

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
        String expectedSequence = MSG_VAL_ENTER_PARKING_LOT + ": "
                + MSG_VAL_ENTER_PARKING_LOT_INVALID + System.lineSeparator();

        String invalidParkingLotNumber = "invalid parkinglot number";

        // Mock
        when(bufferedReader.readLine()).thenReturn(invalidParkingLotNumber);

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

        // Mock
        when(bufferedReader.readLine()).thenReturn("0");

        // Run
        view.executeActionsForStateEnteringParkingLotNumber();

        // Assert
        assertEquals(MSG_VAL_ENTER_PARKING_LOT + ": ", outContent.toString());
    }

    @Test
    public void WhenASpaceIsAtTheEndOfTheTextTrimTheText() throws IOException {
        String parkingLotText = "Parkplatznummer eingeben:     ";

        // Mock
        when(messageProvider.get(MSG_KEY_ENTER_PARKING_LOT)).thenReturn(
                MSG_VAL_ENTER_PARKING_LOT + "     ");
        when(bufferedReader.readLine()).thenReturn("0");

        // Run
        view.executeActionsForStateEnteringParkingLotNumber();

        // Assert
        assertEquals(MSG_VAL_ENTER_PARKING_LOT + ": ", outContent.toString());
    }

    @Test
    public void testDisplayParkingLotAndParkingTime() throws IOException {
        Calendar cal = Calendar.getInstance();

        final int expectedParkingLotNumber = 44;
        final Date expectedParkingDate = cal.getTime();
        String expectedOutput = expectedParkingLotNumber + ":"
                + dateFormatter.print(expectedParkingDate, Locale.GERMAN)
                + System.lineSeparator();

        // Mock
        when(messageProvider.get("view.info.parkingTime"))
                .thenReturn("{0}:{1}");

        // Run
        view.displayParkingLotNumberAndParkingTime(expectedParkingLotNumber,
                expectedParkingDate);

        // Assert
        assertEquals(expectedOutput, outContent.toString());
    }

    // ************** Tests for Entering Parking Lot **************

    @Test
    public void testDisplayParkingLotInvalid() throws IOException {
        // Run
        view.displayParkingLotNumberInvalid();

        // Assert
        assertEquals(
                MSG_VAL_ENTER_PARKING_LOT_INVALID + System.lineSeparator(),
                outContent.toString());
    }

    // ************** Tests for Money-Prompt **************

    @Test
    public final void testStatetForDroppingInMoneyState() {
        // Check init state.
        assertEquals(ConsoleViewStateEnum.INIT, view.getViewState());

        // Run
        view.promptForMoney(5);

        // check state after prompt
        assertEquals(ConsoleViewStateEnum.DROPPING_IN_MONEY,
                view.getViewState());
    }

    @Test
    public void testStateForDroppingInMoneyExecute() throws IOException {
        String exptectedMessage = MessageFormat.format(MSG_VAL_ENTER_COINS
                + ": ", 5);

        MoneyInsertedEvent mInsertedEvent = new MoneyInsertedEvent(view);

        // Mock
        when(bufferedReader.readLine()).thenReturn("0");

        // Run
        view.promptForMoney(5);
        view.executeActionsForDroppingInMoney();

        // Assert
        assertEquals(exptectedMessage, outContent.toString());

        // verify(listener).moneyInserted(mInsertedEvent);

        // TODO: Implement test (e.g. verify if intelligent slot machine was
        // called...)
    }

    // ************** Tests for Shuting down **************

    @Test
    public void testDisplayShutdownMessage() throws IOException {
        // Run
        view.displayShutdownMessage();

        // Assert
        assertEquals(MSG_VAL_SHUTDOWN + System.lineSeparator(),
                outContent.toString());
    }

    @Test
    public final void whenEnteredExitAndEventShouldBeGenerated()
            throws IOException {
        String shutdownCode = "exit";

        // Mock
        when(bufferedReader.readLine()).thenReturn(shutdownCode);

        // Run
        view.executeActionsForStateEnteringParkingLotNumber();

        //Read and forget
        outContent.toString();
        
        // Verify that no notification was sent about the entered data.
        verify(listener).shutdownRequested(any(ShutdownEvent.class));
    }

    @Test
    public void testViewShutdown() throws IOException {
        // Run
        view.shutdown();

        // Assert
        assertEquals(ConsoleViewStateEnum.EXIT, view.getViewState());
    }
}
