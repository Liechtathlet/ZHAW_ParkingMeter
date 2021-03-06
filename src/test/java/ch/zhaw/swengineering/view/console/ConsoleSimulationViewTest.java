package ch.zhaw.swengineering.view.console;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
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
import ch.zhaw.swengineering.event.CoinBoxLevelEnteredEvent;
import ch.zhaw.swengineering.event.NumberOfTransactionLogEntriesToShowEvent;
import ch.zhaw.swengineering.event.ParkingLotEnteredEvent;
import ch.zhaw.swengineering.event.ShutdownEvent;
import ch.zhaw.swengineering.event.ViewEventListener;
import ch.zhaw.swengineering.helper.ConfigurationProvider;
import ch.zhaw.swengineering.helper.MessageProvider;
import ch.zhaw.swengineering.helper.TransactionLogHandler;
import ch.zhaw.swengineering.model.CoinBoxLevel;
import ch.zhaw.swengineering.model.ParkingTimeTable;
import ch.zhaw.swengineering.model.ParkingTimeTableItem;
import ch.zhaw.swengineering.model.persistence.ParkingLot;
import ch.zhaw.swengineering.model.persistence.ParkingTimeDefinition;
import ch.zhaw.swengineering.model.persistence.TransactionLogEntry;
import ch.zhaw.swengineering.setup.ParkingMeterRunner;
import ch.zhaw.swengineering.slotmachine.controller.IntelligentSlotMachine;
import ch.zhaw.swengineering.slotmachine.exception.CoinBoxFullException;
import ch.zhaw.swengineering.slotmachine.exception.InvalidCoinException;
import ch.zhaw.swengineering.slotmachine.exception.NoTransactionException;
import ch.zhaw.swengineering.view.ViewStateEnum;

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

    private static final String MSG_KEY_PROMPT_SEPARATOR = "view.prompt.separator";
    private static final String MSG_VAL_PROMPT_SEPARATOR = ": ";

    private static final String MSG_KEY_ALL_INFORMATION_COIN_BOX_CONTENT = "view.all.information.coin.box.content.total";
    private static final String MSG_VAL_ALL_INFORMATION_COIN_BOX_CONTENT = "coin box content. total {0} €";

    private static final String MSG_KEY_ALL_INFORMATION_PARKING_TIME_DEF_TITLE = "view.all.information.parking.time.def";
    private static final String MSG_VAL_ALL_INFORMATION_PARKING_TIME_DEF_TITLE = "parking time def";

    private static final String MSG_KEY_ALL_INFORMATION_PARKING_TIME_TEMPLATE = "view.all.information.parking.time";
    private static final String MSG_VAL_ALL_INFORMATION_PARKING_TIME_TEMPLATE = "timerange [{0}] | from {1} € | to {2} € | time: {3} min |";

    private static final String MSG_KEY_ALL_INFORMATION_PARKING_LOTS = "view.all.information.parking.lots";
    private static final String MSG_VAL_ALL_INFORMATION_PARKING_LOTS = "parking lots of {0}";

    private static final String MSG_KEY_ALL_INFORMATION_TEST_COMPUTE_PARKING_TIME = "view.all.information.compute.parking.time";
    private static final String MSG_VAL_ALL_INFORMATION_TEST_COMPUTE_PARKING_TIME = "test of parking time calculation";

    private static final String MSG_KEY_ALL_INFORMATION_TRANSACTION_LOG = "view.all.information.transaction.log";
    private static final String MSG_VAL_ALL_INFORMATION_TRANSACTION_LOG = "transaction log of {0}";

    private static final String MSG_KEY_VIEW_TRANSACTION_LOG_ENTRY = "view.transaction.log.entry";
    private static final String MSG_VAL_VIEW_TRANSACTION_LOG_ENTRY = "[{0}] {1}";

    private static final String MSG_KEY_VIEW_N_TRANSACTION_LOG_ENTRIES = "view.n.transaction.log.entries";
    private static final String MSG_VAL_VIEW_N_TRANSACTION_LOG_ENTRIES = "number of transaction log entries to show";

    private static final String MSG_KEY_INVALID_NUMBER_OF_TRANSACTION_LOG_ENTRIES_TO_SHOW = "view.invalid.number.of.transaction.log.entries.to.show";
    private static final String MSG_VAL_INVALID_NUMBER_OF_TRANSACTION_LOG_ENTRIES_TO_SHOW = "invalid number";

    private static final String MSG_KEY_ALL_COIN_LEVEL_TOO_HIGH = "view.slot.machine.coin.box.level.too.high";
    private static final String MSG_VAL_ALL_COIN_LEVEL_TOO_HIGH = "too high: {0}";

    private static final String MSG_KEY_ALL_VIEW_CBL_CONTENT = "view.info.coin.box.content";
    private static final String MSG_VAL_ALL_VIEW_CBL_CONTENT = "CB-Content: {0} {1} {2}";

    private static final String MSG_KEY_ALL_VIEW_CBL_CONTENT_NEW = "view.info.coin.box.content.new";
    private static final String MSG_VAL_ALL_VIEW_CBL_CONTENT_NEW = "new";

    private static final String MSG_KEY_VIEW_CBL_COUNT_LIMIT = "view.info.coin.box.content.limit";
    private static final String MSG_VAL_VIEW_CBL_COUNT_LIMIT = "limitReached";

    private static final String MSG_KEY_VIEW_INFO_CB_TOTAL = "view.info.coin.box.content.total";
    private static final String MSG_VAL_VIEW_INFO_CB_TOTAL = "total: {0}";

    private static final String MSG_KEY_PARKING_TIME_INFO = "view.parking.time.info";
    private static final String MSG_VAL_PARKING_TIME_INFO = "info";

    private static final String MSG_KEY_PARKING_TIME_EXCEEDED = "parking.time.exceeded";
    private static final String MSG_VAL_PARKING_TIME_EXCEEDED = "exceeded";

    private static final String MSG_KEY_BOOKED_PARKINGLOT = "view.booked.parkinglot";
    private static final String MSG_VAL_BOOKED_PARKINGLOT = "parkingmeter {0}  | paid till {1}  | deviation {2}  | {3}";

    private static final String MSG_KEY_PARKING_TIME_TABLE = "view.parking.time.table";
    private static final String MSG_VAL_PARKING_TIME_TABLE = "{0} € -> {1} min";

    private static final String MSG_KEY_PARKING_MAX_TIME = "view.parking.maximal.time";
    private static final String MSG_VAL_PARKING_MAX_TIME = "max {0} min";

    private static final String MSG_KEY_PARKING_MAX_AMOUNT = "view.parking.maximal.amount";
    private static final String MSG_VAL_PARKING_MAX_AMOUNT = "max {0} €";

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

    @Mock
    private ConfigurationProvider parkingTimeConfigurationProvider;

    @Mock
    private TransactionLogHandler transactionLogHandler;

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

        when(messageProvider.get(MSG_KEY_PROMPT_SEPARATOR)).thenReturn(
                MSG_VAL_PROMPT_SEPARATOR);

        when(messageProvider.get(MSG_KEY_ALL_INFORMATION_COIN_BOX_CONTENT))
                .thenReturn(MSG_VAL_ALL_INFORMATION_COIN_BOX_CONTENT);

        when(
                messageProvider
                        .get(MSG_KEY_ALL_INFORMATION_PARKING_TIME_DEF_TITLE))
                .thenReturn(MSG_VAL_ALL_INFORMATION_PARKING_TIME_DEF_TITLE);

        when(messageProvider.get(MSG_KEY_ALL_INFORMATION_PARKING_TIME_TEMPLATE))
                .thenReturn(MSG_VAL_ALL_INFORMATION_PARKING_TIME_TEMPLATE);

        when(messageProvider.get(MSG_KEY_ALL_INFORMATION_PARKING_LOTS))
                .thenReturn(MSG_VAL_ALL_INFORMATION_PARKING_LOTS);

        when(
                messageProvider
                        .get(MSG_KEY_ALL_INFORMATION_TEST_COMPUTE_PARKING_TIME))
                .thenReturn(MSG_VAL_ALL_INFORMATION_TEST_COMPUTE_PARKING_TIME);

        when(messageProvider.get(MSG_KEY_ALL_INFORMATION_TRANSACTION_LOG))
                .thenReturn(MSG_VAL_ALL_INFORMATION_TRANSACTION_LOG);

        when(messageProvider.get(MSG_KEY_ALL_COIN_LEVEL_TOO_HIGH)).thenReturn(
                MSG_VAL_ALL_COIN_LEVEL_TOO_HIGH);

        when(messageProvider.get(MSG_KEY_ALL_VIEW_CBL_CONTENT)).thenReturn(
                MSG_VAL_ALL_VIEW_CBL_CONTENT);

        when(messageProvider.get(MSG_KEY_ALL_VIEW_CBL_CONTENT_NEW)).thenReturn(
                MSG_VAL_ALL_VIEW_CBL_CONTENT_NEW);

        when(messageProvider.get(MSG_KEY_VIEW_CBL_COUNT_LIMIT)).thenReturn(
                MSG_VAL_VIEW_CBL_COUNT_LIMIT);

        when(messageProvider.get(MSG_KEY_VIEW_INFO_CB_TOTAL)).thenReturn(
                MSG_VAL_VIEW_INFO_CB_TOTAL);

        when(messageProvider.get(MSG_KEY_PARKING_TIME_INFO)).thenReturn(
                MSG_VAL_PARKING_TIME_INFO);

        when(messageProvider.get(MSG_KEY_VIEW_TRANSACTION_LOG_ENTRY))
                .thenReturn(MSG_VAL_VIEW_TRANSACTION_LOG_ENTRY);

        when(messageProvider.get(MSG_KEY_VIEW_N_TRANSACTION_LOG_ENTRIES))
                .thenReturn(MSG_VAL_VIEW_N_TRANSACTION_LOG_ENTRIES);

        when(
                messageProvider
                        .get(MSG_KEY_INVALID_NUMBER_OF_TRANSACTION_LOG_ENTRIES_TO_SHOW))
                .thenReturn(
                        MSG_VAL_INVALID_NUMBER_OF_TRANSACTION_LOG_ENTRIES_TO_SHOW);

        when(messageProvider.get(MSG_KEY_PARKING_TIME_EXCEEDED)).thenReturn(
                MSG_VAL_PARKING_TIME_EXCEEDED);

        when(messageProvider.get(MSG_KEY_BOOKED_PARKINGLOT)).thenReturn(
                MSG_VAL_BOOKED_PARKINGLOT);

        when(messageProvider.get(MSG_KEY_PARKING_TIME_TABLE)).thenReturn(
                MSG_VAL_PARKING_TIME_TABLE);

        when(messageProvider.get(MSG_KEY_PARKING_MAX_TIME)).thenReturn(
                MSG_VAL_PARKING_MAX_TIME);

        when(messageProvider.get(MSG_KEY_PARKING_MAX_AMOUNT)).thenReturn(
                MSG_VAL_PARKING_MAX_AMOUNT);

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
        String expectedSequence = MSG_VAL_ENTER_PARKING_LOT
                + MSG_VAL_PROMPT_SEPARATOR + MSG_VAL_INVALID_FORMAT
                + System.lineSeparator();

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
        assertEquals(ViewStateEnum.INIT, view.getViewState());

        // Run
        view.promptForParkingLotNumber();

        // check state after prompt
        assertEquals(ViewStateEnum.ENTERING_PARKING_LOT, view.getViewState());
    }

    @Test
    public final void WhenAskedForParkingLotNumberItPrintsText()
            throws IOException {

        // Mock
        when(bufferedReader.readLine()).thenReturn("0");

        // Run
        view.executeActionsForStateEnteringParkingLotNumber();

        // Assert
        assertEquals(MSG_VAL_ENTER_PARKING_LOT + MSG_VAL_PROMPT_SEPARATOR,
                outContent.toString());
    }

    @Test
    public void WhenASpaceIsAtTheEndOfTheTextTrimTheText() throws IOException {

        // Mock
        when(messageProvider.get(MSG_KEY_ENTER_PARKING_LOT)).thenReturn(
                MSG_VAL_ENTER_PARKING_LOT + "     ");
        when(bufferedReader.readLine()).thenReturn("0");

        // Run
        view.executeActionsForStateEnteringParkingLotNumber();

        // Assert
        assertEquals(MSG_VAL_ENTER_PARKING_LOT + MSG_VAL_PROMPT_SEPARATOR,
                outContent.toString());
    }

    @Test
    public void testDisplayParkingLotAndParkingTime() throws IOException {
        Calendar cal = Calendar.getInstance();

        final int expectedParkingLotNumber = 44;
        final Date expectedParkingDate = cal.getTime();
        String expectedOutput = expectedParkingLotNumber + ":";

        // Mock
        when(messageProvider.get("view.info.parkingTime"))
                .thenReturn("{0}:{1}");

        // Run
        view.displayParkingLotNumberAndParkingTime(expectedParkingLotNumber,
                expectedParkingDate);

        // Assert
        assertTrue(outContent.toString().startsWith(expectedOutput));
    }

    // ************** Tests for Entering Parking Lot **************

    @Test
    public void testDisplayParkingLotInvalid() throws IOException {
        // Run
        view.displayErrorParkingLotNumberInvalid();

        // Assert
        assertEquals(
                MSG_VAL_ENTER_PARKING_LOT_INVALID + System.lineSeparator(),
                outContent.toString());
    }

    // ************** Tests for Money-Prompt **************

    @Test
    public final void testStatetForDroppingInMoneyState() {
        // Check init state.
        assertEquals(ViewStateEnum.INIT, view.getViewState());

        // Run
        view.promptForMoney(5);

        // check state after prompt
        assertEquals(ViewStateEnum.DROPPING_IN_MONEY, view.getViewState());
    }

    @Test
    public void testStateForDroppingInMoneyExecuteWithValidCoins()
            throws IOException {
        String exptectedMessage = MessageFormat.format(MSG_VAL_ENTER_COINS
                + MSG_VAL_PROMPT_SEPARATOR, 5);

        // Mock
        when(bufferedReader.readLine()).thenReturn("0.5 1.0");

        // Run
        view.promptForMoney(5);
        view.executeActionsForStateDroppingInMoney();

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
                + MSG_VAL_PROMPT_SEPARATOR, 5)
                + MSG_VAL_INVALID_FORMAT + System.lineSeparator();

        // Mock
        when(bufferedReader.readLine()).thenReturn("abc");

        // Run
        view.promptForMoney(5);
        view.executeActionsForStateDroppingInMoney();

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
                + MSG_VAL_PROMPT_SEPARATOR, 5)
                + MSG_VAL_COIN_INVALID + System.lineSeparator();

        List<BigDecimal> validCoins = new ArrayList<>();
        validCoins.add(new BigDecimal("2.00").setScale(2));
        validCoins.add(new BigDecimal("1.00").setScale(2));

        // Mock
        doThrow(new InvalidCoinException("invalidcoin", validCoins)).when(
                slotMachine).insertCoin(any(BigDecimal.class));
        when(bufferedReader.readLine()).thenReturn("0.6");

        // Run
        view.promptForMoney(5);
        view.executeActionsForStateDroppingInMoney();

        // Assert
        assertEquals(exptectedMessage, outContent.toString());

        verify(slotMachine).insertCoin(any(BigDecimal.class));
    }

    @Test
    public void testStateForDroppingInMoneyExecuteWithInvalidCoinDelimiter()
            throws IOException {
        String exptectedMessage = MessageFormat.format(MSG_VAL_ENTER_COINS
                + MSG_VAL_PROMPT_SEPARATOR, 5)
                + MSG_VAL_INVALID_FORMAT + System.lineSeparator();

        // Mock
        when(bufferedReader.readLine()).thenReturn("0.5,0.6");

        // Run
        view.promptForMoney(5);
        view.executeActionsForStateDroppingInMoney();

        // Assert
        assertEquals(exptectedMessage, outContent.toString());
    }

    @Test
    public void testStateForDroppingInMoneyExecuteWithCoinBoxFull()
            throws IOException, CoinBoxFullException, NoTransactionException,
            InvalidCoinException {
        String exptectedMessage = MessageFormat.format(MSG_VAL_ENTER_COINS
                + MSG_VAL_PROMPT_SEPARATOR, 5)
                + MSG_VAL_CB_FULL
                + System.lineSeparator()
                + MSG_VAL_DRAWBACK
                + System.lineSeparator();

        Map<BigDecimal, Integer> drawbackMap = new Hashtable<>();

        BigDecimal coin = new BigDecimal(2.00);
        coin = coin.setScale(2);

        drawbackMap.put(coin, 1);

        // Mock
        doThrow(new CoinBoxFullException("coinboxfull", coin, true)).when(
                slotMachine).insertCoin(coin);
        when(slotMachine.rolebackTransaction()).thenReturn(drawbackMap);
        when(slotMachine.getDrawback()).thenReturn(drawbackMap);
        when(slotMachine.hasDrawback()).thenReturn(true);
        when(bufferedReader.readLine()).thenReturn(coin.toPlainString());

        // Run
        view.promptForMoney(5);
        view.executeActionsForStateDroppingInMoney();

        assertEquals(exptectedMessage, outContent.toString());
    }

    @Test
    public void testStateForDroppingInMoneyWithNoTransaction()
            throws IOException, CoinBoxFullException, NoTransactionException,
            InvalidCoinException {
        Map<BigDecimal, Integer> drawbackMap = new Hashtable<>();

        String exptectedMessage = MessageFormat.format(MSG_VAL_ENTER_COINS
                + MSG_VAL_PROMPT_SEPARATOR, 5);

        BigDecimal coin = new BigDecimal(2.00);
        coin = coin.setScale(2);

        drawbackMap.put(coin, 1);

        // Mock
        doThrow(new NoTransactionException("noTransaction")).when(slotMachine)
                .insertCoin(any(BigDecimal.class));
        when(slotMachine.rolebackTransaction()).thenReturn(drawbackMap);
        when(bufferedReader.readLine()).thenReturn(coin.toPlainString());

        // Run
        view.promptForMoney(5);
        view.executeActionsForStateDroppingInMoney();

        assertEquals(exptectedMessage, outContent.toString());

    }

    @Test
    public void testStateForDroppingInMoneyExecuteWithOneCoinBoxFull()
            throws IOException, CoinBoxFullException, NoTransactionException,
            InvalidCoinException {
        String exptectedMessage = MessageFormat.format(MSG_VAL_ENTER_COINS
                + MSG_VAL_PROMPT_SEPARATOR, 5)
                + MSG_VAL_ONE_CB_FULL
                + System.lineSeparator()
                + MSG_VAL_DRAWBACK + System.lineSeparator();

        Map<BigDecimal, Integer> drawbackMap = new Hashtable<>();

        BigDecimal coin = new BigDecimal(2.00);
        coin = coin.setScale(2);

        drawbackMap.put(coin, 2);

        // Mock
        doThrow(new CoinBoxFullException("coinboxfull", coin, false)).when(
                slotMachine).insertCoin(coin);
        when(slotMachine.rolebackTransaction()).thenReturn(drawbackMap);
        when(bufferedReader.readLine()).thenReturn(
                coin.toPlainString() + " " + coin.toPlainString());

        // Run
        view.promptForMoney(5);
        view.executeActionsForStateDroppingInMoney();

        assertEquals(exptectedMessage, outContent.toString());

    }

    @Test
    public void testWhenDisplayAllInformationIsCalledItShouldOutputsText() {

        // Mock coin box level
        BigDecimal coin = new BigDecimal(5);
        int coinCount = 4;
        int coinBoxTotal = coin.intValue() * coinCount;
        List<CoinBoxLevel> cbLevels = getCoinBoxLevelsMock(coin, coinCount);

        // Mock parking time definitions
        ParkingTimeDefinition definition1 = new ParkingTimeDefinition();
        definition1.setPricePerPeriod(new BigDecimal(0.5));
        definition1.setDurationOfPeriodInMinutes(30);
        definition1.setCountOfSuccessivePeriods(2);
        definition1.setOrderId(1);

        ParkingTimeDefinition definition2 = new ParkingTimeDefinition();
        definition2.setPricePerPeriod(new BigDecimal(1));
        definition2.setDurationOfPeriodInMinutes(10);
        definition2.setCountOfSuccessivePeriods(1);
        definition2.setOrderId(2);

        List<ParkingTimeDefinition> parkingTimeDefinitions = new ArrayList<>();
        parkingTimeDefinitions.add(definition1);
        parkingTimeDefinitions.add(definition2);

        Date transactionLogDate = new Date();
        String formattedDate = dateFormatter.print(transactionLogDate,
                Locale.getDefault());

        // Mock parking lots
        List<ParkingLot> parkingLots = new ArrayList<>();

        // Mock parking time calculation table
        int maxBookingTime = 300;
        BigDecimal maxPrice = new BigDecimal(500);
        Integer itemMinutes1 = 5;
        BigDecimal itemAmount1 = new BigDecimal(1.5);
        Integer itemMinutes2 = 100;
        BigDecimal itemAmount2 = new BigDecimal(10);
        final ParkingTimeTableItem item1 = new ParkingTimeTableItem(
                itemAmount1, itemMinutes1);
        final ParkingTimeTableItem item2 = new ParkingTimeTableItem(
                itemAmount2, itemMinutes2);
        List<ParkingTimeTableItem> items = new ArrayList<>();
        items.add(item1);
        items.add(item2);
        ParkingTimeTable table = new ParkingTimeTable(items, maxBookingTime,
                maxPrice);

        // Run
        view.displayAllInformation(cbLevels, parkingTimeDefinitions,
                transactionLogDate, parkingLots, table);

        // Assert
        assertEquals(
                MSG_VAL_PARKING_TIME_INFO
                        + System.lineSeparator()
                        + MessageFormat.format(
                                MSG_VAL_ALL_INFORMATION_COIN_BOX_CONTENT,
                                coinBoxTotal)
                        + System.lineSeparator()
                        + MessageFormat.format(MSG_VAL_ALL_VIEW_CBL_CONTENT,
                                coin, coinCount, coinBoxTotal)
                        + System.lineSeparator()
                        + MSG_VAL_ALL_INFORMATION_PARKING_TIME_DEF_TITLE
                        + System.lineSeparator()
                        + MessageFormat.format(
                                MSG_VAL_ALL_INFORMATION_PARKING_TIME_TEMPLATE,
                                "1", "0.00", "0.50", "30")
                        + System.lineSeparator()
                        + MessageFormat.format(
                                MSG_VAL_ALL_INFORMATION_PARKING_TIME_TEMPLATE,
                                "2", "0.50", "1.00", "60")
                        + System.lineSeparator()
                        + MessageFormat.format(
                                MSG_VAL_ALL_INFORMATION_PARKING_TIME_TEMPLATE,
                                "3", "1.00", "2.00", "70")
                        + System.lineSeparator()
                        + MessageFormat.format(
                                MSG_VAL_ALL_INFORMATION_PARKING_LOTS,
                                formattedDate)
                        + System.lineSeparator()
                        + MessageFormat.format(
                                MSG_VAL_ALL_INFORMATION_TEST_COMPUTE_PARKING_TIME,
                                formattedDate)
                        + System.lineSeparator()
                        + MessageFormat.format(MSG_VAL_PARKING_TIME_TABLE,
                                itemAmount1.toString() + "0", "00"
                                        + itemMinutes1.toString())
                        + System.lineSeparator()
                        + MessageFormat.format(MSG_VAL_PARKING_TIME_TABLE,
                                itemAmount2.toString() + ".00",
                                itemMinutes2.toString())
                        + System.lineSeparator()
                        + MessageFormat.format(MSG_VAL_PARKING_MAX_TIME,
                                maxBookingTime)
                        + System.lineSeparator()
                        + MessageFormat.format(MSG_VAL_PARKING_MAX_AMOUNT,
                                maxPrice.toString() + ".00")
                        + System.lineSeparator()
                        + MessageFormat.format(
                                MSG_VAL_ALL_INFORMATION_TRANSACTION_LOG,
                                formattedDate) + System.lineSeparator(),
                outContent.toString());
    }

    private List<CoinBoxLevel> getCoinBoxLevelsMock(BigDecimal coin,
            int coinCount) {
        List<CoinBoxLevel> cbLevels = new ArrayList<>();
        cbLevels.add(new CoinBoxLevel(coin, coinCount, 10));
        return cbLevels;
    }

    @Test
    public void testWhenDisplayP() {

        // Run
        view.displayParkingMeterInfo(new Date());

        // Assert
        assertEquals(MSG_VAL_PARKING_TIME_INFO + System.lineSeparator(),
                outContent.toString());
    }

    @Test
    public void testDisplayAllTransactionLogsOutputsText() {
        // Mock
        Calendar cal1 = Calendar.getInstance();
        cal1.set(2013, Calendar.JANUARY, 9, 10, 11, 12);
        Date date1 = cal1.getTime();
        String text1 = "text1";

        TransactionLogEntry transactionLogEntry1 = new TransactionLogEntry();
        transactionLogEntry1.creationTime = date1;
        transactionLogEntry1.text = text1;

        Calendar cal2 = Calendar.getInstance();
        cal2.set(2013, Calendar.JANUARY, 9, 11, 12, 13);
        Date date2 = cal2.getTime();
        String text2 = "text2";

        TransactionLogEntry transactionLogEntry2 = new TransactionLogEntry();
        transactionLogEntry2.creationTime = date2;
        transactionLogEntry2.text = text2;

        List<TransactionLogEntry> logEntries = new ArrayList<>();
        logEntries.add(transactionLogEntry1);
        logEntries.add(transactionLogEntry2);

        when(transactionLogHandler.getAll()).thenReturn(logEntries);

        // Run
        view.displayAllTransactionLogs();

        // Assert
        assertEquals(
                MessageFormat.format(MSG_VAL_VIEW_TRANSACTION_LOG_ENTRY, date1,
                        text1)
                        + System.lineSeparator()
                        + MessageFormat.format(
                                MSG_VAL_VIEW_TRANSACTION_LOG_ENTRY, date2,
                                text2) + System.lineSeparator(),
                outContent.toString());
    }

    @Test
    public void testDisplayLast24HoursOfTransactionLogOutputsText() {
        // Mock

        // Looks silly, because the two dates here are not within the last 24
        // hours.
        // But that is ok. Because they are mock data. The actual implementation
        // of the getLast24Hours method does indeed check for the date which is
        // covered by another unit test.

        Calendar cal1 = Calendar.getInstance();
        cal1.set(2013, Calendar.JANUARY, 9, 10, 11, 12);
        Date date1 = cal1.getTime();
        String text1 = "text1";

        TransactionLogEntry transactionLogEntry1 = new TransactionLogEntry();
        transactionLogEntry1.creationTime = date1;
        transactionLogEntry1.text = text1;

        Calendar cal2 = Calendar.getInstance();
        cal2.set(2013, Calendar.JANUARY, 9, 11, 12, 13);
        Date date2 = cal2.getTime();
        String text2 = "text2";

        TransactionLogEntry transactionLogEntry2 = new TransactionLogEntry();
        transactionLogEntry2.creationTime = date2;
        transactionLogEntry2.text = text2;

        List<TransactionLogEntry> logEntries = new ArrayList<>();
        logEntries.add(transactionLogEntry1);
        logEntries.add(transactionLogEntry2);

        when(transactionLogHandler.getLast24Hours()).thenReturn(logEntries);

        // Run
        view.displayLast24HoursOfTransactionLog();

        // Assert
        assertEquals(
                MessageFormat.format(MSG_VAL_VIEW_TRANSACTION_LOG_ENTRY, date1,
                        text1)
                        + System.lineSeparator()
                        + MessageFormat.format(
                                MSG_VAL_VIEW_TRANSACTION_LOG_ENTRY, date2,
                                text2) + System.lineSeparator(),
                outContent.toString());
    }

    @Test
    public void testValidInputForExecuteActionsForStateEnteringTransactionLogEntriesToShow()
            throws IOException {
        int numberOfTransactionLogEntriesToShowInteger = 2;

        // Mock
        when(bufferedReader.readLine()).thenReturn(
                numberOfTransactionLogEntriesToShowInteger + "");

        // Run
        view.executeActionsForStateEnteringTransactionLogEntriesToShow();

        // Assert
        ArgumentCaptor<NumberOfTransactionLogEntriesToShowEvent> argument = ArgumentCaptor
                .forClass(NumberOfTransactionLogEntriesToShowEvent.class);
        verify(listener).numberOfTransactionLogEntriesToShowEntered(
                argument.capture());
        assertEquals(numberOfTransactionLogEntriesToShowInteger, argument
                .getValue().getNumber());

        assertEquals(MSG_VAL_VIEW_N_TRANSACTION_LOG_ENTRIES
                + MSG_VAL_PROMPT_SEPARATOR, outContent.toString());
    }

    @Test
    public void testInvalidInputForExecuteActionsForStateEnteringTransactionLogEntriesToShow()
            throws IOException {
        String numberOfTransactionLogEntriesToShow = "x";

        // Mock
        when(bufferedReader.readLine()).thenReturn(
                numberOfTransactionLogEntriesToShow);

        // Run
        view.executeActionsForStateEnteringTransactionLogEntriesToShow();

        // Assert
        assertEquals(MSG_VAL_VIEW_N_TRANSACTION_LOG_ENTRIES
                + MSG_VAL_PROMPT_SEPARATOR
                + MSG_VAL_INVALID_NUMBER_OF_TRANSACTION_LOG_ENTRIES_TO_SHOW
                + System.lineSeparator(), outContent.toString());
    }

    @Test
    public void testDisplayNTransactionLogEntriesShowText() throws IOException {
        int numberOfTransactionLogEntriesToShow = 2;

        // Mock
        Calendar cal1 = Calendar.getInstance();
        cal1.set(2013, Calendar.JANUARY, 9, 10, 11, 12);
        Date date1 = cal1.getTime();
        String text1 = "text1";

        TransactionLogEntry transactionLogEntry1 = new TransactionLogEntry();
        transactionLogEntry1.creationTime = date1;
        transactionLogEntry1.text = text1;

        Calendar cal2 = Calendar.getInstance();
        cal2.set(2013, Calendar.JANUARY, 9, 11, 12, 13);
        Date date2 = cal2.getTime();
        String text2 = "text2";

        TransactionLogEntry transactionLogEntry2 = new TransactionLogEntry();
        transactionLogEntry2.creationTime = date2;
        transactionLogEntry2.text = text2;

        List<TransactionLogEntry> logEntries = new ArrayList<>();
        logEntries.add(transactionLogEntry1);
        logEntries.add(transactionLogEntry2);

        when(transactionLogHandler.get(numberOfTransactionLogEntriesToShow))
                .thenReturn(logEntries);

        // Run
        view.displayNTransactionLogEntries(numberOfTransactionLogEntriesToShow);

        // Assert
        assertEquals(
                MessageFormat.format(MSG_VAL_VIEW_TRANSACTION_LOG_ENTRY, date1,
                        text1)
                        + System.lineSeparator()
                        + MessageFormat.format(
                                MSG_VAL_VIEW_TRANSACTION_LOG_ENTRY, date2,
                                text2) + System.lineSeparator(),
                outContent.toString());
    }

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
    public void testDisplayDrawbackMessage() throws IOException {
        Map<BigDecimal, Integer> drawbackMap = new Hashtable<>();

        BigDecimal coin = new BigDecimal(2.00);
        coin = coin.setScale(2);

        drawbackMap.put(coin, 1);

        coin = new BigDecimal(0.5);
        coin = coin.setScale(2);

        drawbackMap.put(coin, 2);

        when(slotMachine.getDrawback()).thenReturn(drawbackMap);
        when(slotMachine.hasDrawback()).thenReturn(true);

        // Run
        view.displayMessageForDrawback();

        // Assert
        assertEquals(MessageFormat.format(MSG_VAL_DRAWBACK, "1 x 0.5, 1 x 2.0")
                + System.lineSeparator(), outContent.toString());
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
        assertEquals(ViewStateEnum.EXIT, view.getViewState());
    }

    @Test
    public void testReadFromConsole() throws IOException {
        String exptectedMessage = MessageFormat.format(MSG_VAL_ENTER_COINS
                + MSG_VAL_PROMPT_SEPARATOR, 5);

        // Mock
        when(bufferedReader.readLine()).thenReturn("x");

        // Run
        view.promptForMoney(5);
        view.executeActionsForStateDroppingInMoney();

        // Assert
        verify(listener).actionAborted(any(ActionAbortedEvent.class));
        assertEquals(ViewStateEnum.INIT, view.getViewState());
        assertEquals(exptectedMessage, outContent.toString());
    }

    @Test
    public void testDisplayCoinCountTooHigh() throws IOException {
        BigDecimal coin = new BigDecimal(5);

        // Run
        view.displayCoinCountTooHigh(coin);

        // Assert
        assertEquals(
                MessageFormat.format(MSG_VAL_ALL_COIN_LEVEL_TOO_HIGH, coin)
                        + System.lineSeparator(), outContent.toString());
        assertEquals(ViewStateEnum.INIT, view.getViewState());
    }

    @Test
    public void testStateForEnteringCoinBoxLevelWidthInvalidNumber()
            throws IOException {

        BigDecimal coin = new BigDecimal(5);
        int coinCount = 4;
        List<CoinBoxLevel> cbLevels = getCoinBoxLevelsMock(coin, coinCount);

        String expectedMessage = MessageFormat.format(
                MSG_VAL_ALL_VIEW_CBL_CONTENT, coin, coinCount,
                coin.multiply(new BigDecimal(coinCount)))
                + System.lineSeparator()
                + MSG_VAL_ALL_VIEW_CBL_CONTENT_NEW
                + MSG_VAL_PROMPT_SEPARATOR
                + MSG_VAL_INVALID_FORMAT
                + System.lineSeparator();

        // Mock
        when(bufferedReader.readLine()).thenReturn("abc");

        // Run
        view.promptForNewCoinBoxLevels(cbLevels);
        view.executeActionsForStateEnteringCoinBoxLevels();

        // Assert
        assertEquals(expectedMessage, outContent.toString());

        verify(listener, Mockito.times(0)).coinBoxLevelEntered(
                any(CoinBoxLevelEnteredEvent.class));
    }

    @Test
    public void testStateForEnteringCoinBoxLevelWidthToHighNumber()
            throws IOException {
        BigDecimal coin = new BigDecimal(5);
        int coinCount = 4;

        List<CoinBoxLevel> cbLevels = getCoinBoxLevelsMock(coin, coinCount);

        String exptectedMessage = MessageFormat.format(
                MSG_VAL_ALL_VIEW_CBL_CONTENT, coin, coinCount,
                coin.multiply(new BigDecimal(coinCount)))
                + System.lineSeparator()
                + MSG_VAL_ALL_VIEW_CBL_CONTENT_NEW
                + MSG_VAL_PROMPT_SEPARATOR
                + MSG_VAL_VIEW_CBL_COUNT_LIMIT
                + System.lineSeparator();

        // Mock
        when(bufferedReader.readLine()).thenReturn("200");

        // Run
        view.promptForNewCoinBoxLevels(cbLevels);
        view.executeActionsForStateEnteringCoinBoxLevels();

        // Assert
        assertEquals(exptectedMessage, outContent.toString());

        verify(listener, Mockito.times(0)).coinBoxLevelEntered(
                any(CoinBoxLevelEnteredEvent.class));
    }

    @Test
    public void testStateForEnteringCoinBoxLevel() throws IOException {
        List<CoinBoxLevel> cbLevels = new ArrayList<>();

        BigDecimal coin = new BigDecimal(5);
        cbLevels.add(new CoinBoxLevel(coin, 4, 10));

        String exptectedMessage = MessageFormat.format(
                MSG_VAL_ALL_VIEW_CBL_CONTENT, coin, 4,
                coin.multiply(new BigDecimal(4)))
                + System.lineSeparator()
                + MSG_VAL_ALL_VIEW_CBL_CONTENT_NEW
                + MSG_VAL_PROMPT_SEPARATOR;

        // Mock
        when(bufferedReader.readLine()).thenReturn("5");

        // Run
        view.promptForNewCoinBoxLevels(cbLevels);
        view.executeActionsForStateEnteringCoinBoxLevels();

        // Assert
        assertEquals(exptectedMessage, outContent.toString());

        verify(listener).coinBoxLevelEntered(
                any(CoinBoxLevelEnteredEvent.class));
    }

    @Test
    public void testDisplayContentOfCoinBoxes() throws IOException {
        List<CoinBoxLevel> cbLevels = new ArrayList<>();

        BigDecimal coin = new BigDecimal(5);
        cbLevels.add(new CoinBoxLevel(coin, 4, 10));

        String exptectedMessage = MessageFormat.format(
                MSG_VAL_VIEW_INFO_CB_TOTAL, coin.multiply(new BigDecimal(4)))
                + System.lineSeparator()
                + MessageFormat.format(MSG_VAL_ALL_VIEW_CBL_CONTENT, coin, 4,
                        coin.multiply(new BigDecimal(4)))
                + System.lineSeparator();

        // Run
        view.displayContentOfCoinBoxes(cbLevels);

        // Assert
        assertEquals(exptectedMessage, outContent.toString());
    }

    @Test
    public void testDisplayParkginLotPayment() throws IOException {
        Map<BigDecimal, Integer> coinMap = new Hashtable<>();

        BigDecimal coin = new BigDecimal(2.00);
        coin = coin.setScale(2);

        coinMap.put(coin, 2);

        String expectedOutput = "5:4.00 = 2 x 2.00" + System.lineSeparator();

        when(messageProvider.get("view.info.inserted.coins")).thenReturn(
                "{0}:{1}");

        // Run
        view.displayParkingLotPayment(5, coinMap);

        // Assert
        assertEquals(expectedOutput, outContent.toString());
    }
}
