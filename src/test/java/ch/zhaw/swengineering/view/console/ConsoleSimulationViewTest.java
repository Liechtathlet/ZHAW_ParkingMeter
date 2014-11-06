package ch.zhaw.swengineering.view.console;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.format.datetime.DateFormatter;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import ch.zhaw.swengineering.event.ActionAbortedEvent;
import ch.zhaw.swengineering.event.MoneyInsertedEvent;
import ch.zhaw.swengineering.event.ParkingLotEnteredEvent;
import ch.zhaw.swengineering.event.ShutdownEvent;
import ch.zhaw.swengineering.event.ViewEventListener;
import ch.zhaw.swengineering.helper.MessageProvider;
import ch.zhaw.swengineering.setup.ParkingMeterRunner;
import ch.zhaw.swengineering.slotmachine.controller.IntelligentSlotMachine;
import ch.zhaw.swengineering.slotmachine.exception.CoinBoxFullException;
import ch.zhaw.swengineering.slotmachine.exception.InvalidCoinException;
import ch.zhaw.swengineering.slotmachine.exception.NoTransactionException;

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
    private static final String MSG_VAL_ENTER_SECRET_CODE_INVALID = "EnterSecretCodeInvalid";

    private static final String MSG_KEY_ENTER_COINS = "view.enter.coins";
    private static final String MSG_VAL_ENTER_COINS = "Coins: {0}";

    private static final String MSG_KEY_SHUTDOWN = "application.bye";
    private static final String MSG_VAL_SHUTDOWN = "bye";

    private static final String MSG_KEY_INVALID_FORMAT = "view.slot.machine.format.invalid";
    private static final String MSG_VAL_INVALID_FORMAT = "invalidformat";

    private static final String MSG_KEY_CB_FULL = "view.slot.machine.coin.box.full";
    private static final String MSG_VAL_CB_FULL = "coinBoxFull";

    private static final String MSG_KEY_ONE_CB_FULL = "view.slot.machine.coin.box.single.full";
    private static final String MSG_VAL_ONE_CB_FULL = "oneCoinBoxFull";

    private static final String MSG_KEY_DRAWBACK = "view.slot.machine.drawback";
    private static final String MSG_VAL_DRAWBACK = "drawback";

    private static final String MSG_KEY_COIN_INVALID = "view.slot.machine.coin.invalid";
    private static final String MSG_VAL_COIN_INVALID = "coininvalid";

    private static final String MSG_KEY_NOTENOUGH_MONEY = "view.booking.not.enough.money";
    private static final String MSG_VAL_NOTENOUGH_MONEY = "notenoughmoney";

    // Replacement for the command line
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();

    @InjectMocks
    private ConsoleSimulationView view;

    @Mock
    private MessageProvider messageProvider;

    @Mock
    private BufferedReader bufferedReader;

    @Spy
    private PrintStream printStreamWriter;

    @Mock
    private IntelligentSlotMachine slotMachine;

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
        printStreamWriter = new PrintStream(outContent);

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

        when(messageProvider.get(MSG_KEY_INVALID_FORMAT)).thenReturn(
                MSG_VAL_INVALID_FORMAT);

        when(messageProvider.get(MSG_KEY_CB_FULL)).thenReturn(MSG_VAL_CB_FULL);

        when(messageProvider.get(MSG_KEY_DRAWBACK))
                .thenReturn(MSG_VAL_DRAWBACK);

        when(messageProvider.get(MSG_KEY_ONE_CB_FULL)).thenReturn(
                MSG_VAL_ONE_CB_FULL);

        when(messageProvider.get(MSG_KEY_COIN_INVALID)).thenReturn(
                MSG_VAL_COIN_INVALID);

        when(messageProvider.get(MSG_KEY_NOTENOUGH_MONEY)).thenReturn(
                MSG_VAL_NOTENOUGH_MONEY);

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
    public void testStateForDroppingInMoneyExecuteWithValidCoins()
            throws IOException {
        String exptectedMessage = MessageFormat.format(MSG_VAL_ENTER_COINS
                + ": ", 5);

        MoneyInsertedEvent mInsertedEvent = new MoneyInsertedEvent(view, 5);

        // Mock
        when(bufferedReader.readLine()).thenReturn("0.5 1.0");

        // Run
        view.promptForMoney(5);
        view.executeActionsForDroppingInMoney();

        // Assert
        assertEquals(exptectedMessage, outContent.toString());

        // verify(listener).moneyInserted(mInsertedEvent);
        try {
            verify(slotMachine, Mockito.times(2)).insertCoin(
                    any(BigDecimal.class));
        } catch (NoTransactionException | InvalidCoinException e) {
            fail("Reiceved unexpected exception: " + e);
        }
    }

    @Test
    public void testStateForDroppingInMoneyExecuteWithInvalidCoinString()
            throws IOException {
        String exptectedMessage = MessageFormat.format(MSG_VAL_ENTER_COINS
                + ": ", 5)
                + MSG_VAL_INVALID_FORMAT + System.lineSeparator();

        MoneyInsertedEvent mInsertedEvent = new MoneyInsertedEvent(view, 5);

        // Mock
        when(bufferedReader.readLine()).thenReturn("abc");

        // Run
        view.promptForMoney(5);
        view.executeActionsForDroppingInMoney();

        // Assert
        assertEquals(exptectedMessage, outContent.toString());

        try {
            verify(slotMachine, Mockito.times(0)).insertCoin(
                    any(BigDecimal.class));
        } catch (CoinBoxFullException | NoTransactionException
                | InvalidCoinException e) {
            fail("Unexptected Exception");
        }
    }

    @Test
    public void testStateForDroppingInMoneyExecuteWithInValidCoin()
            throws IOException, CoinBoxFullException, NoTransactionException,
            InvalidCoinException {
        String exptectedMessage = MessageFormat.format(MSG_VAL_ENTER_COINS
                + ": ", 5)
                + MSG_VAL_COIN_INVALID
                + System.lineSeparator()
                + MSG_VAL_DRAWBACK + System.lineSeparator();

        List<BigDecimal> validCoins = new ArrayList<BigDecimal>();
        validCoins.add(new BigDecimal("2.00").setScale(2));
        validCoins.add(new BigDecimal("1.00").setScale(2));

        // Mock
        doThrow(new InvalidCoinException("invalidcoin", validCoins)).when(
                slotMachine).insertCoin(any(BigDecimal.class));
        when(bufferedReader.readLine()).thenReturn("0.6");

        // Run
        view.promptForMoney(5);
        view.executeActionsForDroppingInMoney();

        // Assert
        assertEquals(exptectedMessage, outContent.toString());

        verify(slotMachine).insertCoin(any(BigDecimal.class));
    }

    @Test
    public void testStateForDroppingInMoneyExecuteWithInvalidCoinDelimiter()
            throws IOException {
        String exptectedMessage = MessageFormat.format(MSG_VAL_ENTER_COINS
                + ": ", 5)
                + MSG_VAL_INVALID_FORMAT + System.lineSeparator();

        MoneyInsertedEvent mInsertedEvent = new MoneyInsertedEvent(view, 5);

        // Mock
        when(bufferedReader.readLine()).thenReturn("0.5,0.6");

        // Run
        view.promptForMoney(5);
        view.executeActionsForDroppingInMoney();

        // Assert
        assertEquals(exptectedMessage, outContent.toString());
    }

    @Test
    public void testStateForDroppingInMoneyExecuteWithCoinBoxFull()
            throws IOException, CoinBoxFullException, NoTransactionException,
            InvalidCoinException {
        String exptectedMessage = MessageFormat.format(MSG_VAL_ENTER_COINS
                + ": ", 5)
                + MSG_VAL_CB_FULL
                + System.lineSeparator()
                + MSG_VAL_DRAWBACK
                + System.lineSeparator();

        Map<BigDecimal, Integer> drawbackMap = new Hashtable<BigDecimal, Integer>();

        BigDecimal coin = new BigDecimal(2.00);
        coin = coin.setScale(2);

        drawbackMap.put(coin, new Integer(1));

        // Mock
        doThrow(new CoinBoxFullException("coinboxfull")).when(slotMachine)
                .insertCoin(coin);
        when(slotMachine.rolebackTransaction()).thenReturn(drawbackMap);
        when(bufferedReader.readLine()).thenReturn(coin.toPlainString());

        // Run
        view.promptForMoney(5);
        view.executeActionsForDroppingInMoney();

        assertEquals(exptectedMessage, outContent.toString());
    }

    @Test
    public void testStateForDroppingInMoneyWithNoTransaction()
            throws IOException, CoinBoxFullException, NoTransactionException,
            InvalidCoinException {
        Map<BigDecimal, Integer> drawbackMap = new Hashtable<BigDecimal, Integer>();

        String exptectedMessage = MessageFormat.format(MSG_VAL_ENTER_COINS
                + ": ", 5)
                + MSG_VAL_DRAWBACK + System.lineSeparator();

        BigDecimal coin = new BigDecimal(2.00);
        coin = coin.setScale(2);

        drawbackMap.put(coin, new Integer(1));

        // Mock
        doThrow(new NoTransactionException("noTransaction")).when(slotMachine)
                .insertCoin(any(BigDecimal.class));
        when(slotMachine.rolebackTransaction()).thenReturn(drawbackMap);
        when(bufferedReader.readLine()).thenReturn(coin.toPlainString());

        // Run
        view.promptForMoney(5);
        view.executeActionsForDroppingInMoney();

        assertEquals(exptectedMessage, outContent.toString());

    }

    @Test
    public void testStateForDroppingInMoneyExecuteWithOneCoinBoxFull()
            throws IOException, CoinBoxFullException, NoTransactionException,
            InvalidCoinException {
        String exptectedMessage = MessageFormat.format(MSG_VAL_ENTER_COINS
                + ": ", 5)
                + MSG_VAL_ONE_CB_FULL
                + System.lineSeparator()
                + MSG_VAL_DRAWBACK + System.lineSeparator();

        Map<BigDecimal, Integer> drawbackMap = new Hashtable<BigDecimal, Integer>();

        BigDecimal coin = new BigDecimal(2.00);
        coin = coin.setScale(2);

        drawbackMap.put(coin, new Integer(2));

        // Mock
        doThrow(new CoinBoxFullException("coinboxfull", coin))
                .when(slotMachine).insertCoin(coin);
        when(slotMachine.rolebackTransaction()).thenReturn(drawbackMap);
        when(bufferedReader.readLine()).thenReturn(
                coin.toPlainString() + " " + coin.toPlainString());

        // Run
        view.promptForMoney(5);
        view.executeActionsForDroppingInMoney();

        assertEquals(exptectedMessage, outContent.toString());

    }

    // ************** Tests for Entering Secret Codes **************

    /*
     * @Test public void testDisplaySecretCodeInvalid() throws IOException { //
     * Run view.displaySecretCodeInvalid();
     * 
     * // Assert assertEquals( MSG_VAL_ENTER_SECRET_CODE_INVALID +
     * System.lineSeparator(), outContent.toString()); }
     */

    // ************** Tests for Shutting down **************

    @Test
    public void testDisplayShutdownMessage() throws IOException {
        // Run
        view.displayShutdownMessage();

        // Assert
        assertEquals(MSG_VAL_SHUTDOWN + System.lineSeparator(),
                outContent.toString());
    }

    @Test
    public void testDisplayNotEnoughMoneyError() throws IOException {
        // Run
        view.displayNotEnoughMoneyError();

        // Assert
        assertEquals(MSG_VAL_NOTENOUGH_MONEY + System.lineSeparator(),
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

        // Read and forget
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

    @Test
    public void testReadFromConsole() throws IOException {
        String exptectedMessage = MessageFormat.format(MSG_VAL_ENTER_COINS
                + ": ", 5);

        // Mock
        when(bufferedReader.readLine()).thenReturn("x");

        // Run
        view.promptForMoney(5);
        view.executeActionsForDroppingInMoney();

        // Assert
        verify(listener).actionAborted(any(ActionAbortedEvent.class));
        assertEquals(ConsoleViewStateEnum.INIT, view.getViewState());
        assertEquals(exptectedMessage, outContent.toString());
    }
}
