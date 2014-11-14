package ch.zhaw.swengineering.view.console;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.format.datetime.DateFormatter;

import ch.zhaw.swengineering.helper.ConfigurationProvider;
import ch.zhaw.swengineering.helper.MessageProvider;
import ch.zhaw.swengineering.model.CoinBoxLevel;
import ch.zhaw.swengineering.model.persistence.ParkingLot;
import ch.zhaw.swengineering.model.persistence.ParkingTimeDefinition;
import ch.zhaw.swengineering.model.persistence.ParkingTimeDefinitions;
import ch.zhaw.swengineering.slotmachine.controller.IntelligentSlotMachineUserInteractionInterface;
import ch.zhaw.swengineering.slotmachine.exception.CoinBoxFullException;
import ch.zhaw.swengineering.slotmachine.exception.InvalidCoinException;
import ch.zhaw.swengineering.slotmachine.exception.NoTransactionException;
import ch.zhaw.swengineering.view.SimulationView;

/**
 * @author Daniel Brun Console implementation of the interface
 *         {@link ch.zhaw.swengineering.view.SimulationView}.
 */
public class ConsoleSimulationView extends SimulationView {

	/**
	 * Date-Format.
	 */
	private static final String DATE_FORMAT = "dd.MM.YYYY HH:mm";

	/**
	 * The 'command' / string which is used to abort the current action.
	 */
	private static final String ABORT_COMMAND = "x";

	/**
	 * The 'command' / string which is used to exit the ParkingMeter.
	 */
	private static final String EXIT_COMMAND = "exit";

	/**
	 * Thread-Sleep-Time.
	 */
	private static final int SLEEP_TIME = 100;

	private static final String COIN_SPLITTER = " ";

	/**
	 * The Logger.
	 */
	private static final Logger LOG = LogManager
			.getLogger(ConsoleSimulationView.class);

	private final Lock runLock;
	private final Condition waitCondition;

	@Autowired
	private MessageProvider messageProvider;

	@Autowired
	private BufferedReader reader;

	@Autowired
	private IntelligentSlotMachineUserInteractionInterface slotMachine;

	@Autowired
	@Qualifier("parkingTimeDef")
	private ConfigurationProvider parkingTimeConfigurationProvider;

	private List<ParkingLot> parkingLots;

	@Autowired
	private PrintStream writer;

	private ConsoleViewStateEnum viewState;

	private boolean run;

	private DateFormatter dateFormatter;
	private Date now = new Date();

	private int storeParkingLotNumber;
	private List<CoinBoxLevel> coinBoxLevels;

	/**
	 * Creates a new instance of this class and sets the initial state.
	 */
	public ConsoleSimulationView() {
		super();
		viewState = ConsoleViewStateEnum.INIT;

		dateFormatter = new DateFormatter(DATE_FORMAT);

		storeParkingLotNumber = -1;

		runLock = new ReentrantLock();
		waitCondition = runLock.newCondition();
	}

	@Override
	public final void run() {
		if (!run) {
			LOG.info("Running console simulation");
			run = true;

			while (run) {
				try {
					runLock.lock();

					switch (viewState) {
					case ENTERING_PARKING_LOT:
						executeActionsForStateEnteringParkingLotNumber();
						break;
					case DROPPING_IN_MONEY:
						executeActionsForDroppingInMoney();
						break;
					case DISPLAY_ALL_INFORMATION:
						executeActionForViewingAllInformation();
						break;
					case DISPLAY_BOOKED_PARKINGLOTS:
						executeActionForDisplayBookedParkingLots();
						break;
					case EXIT:
						run = false;
						break;
					case ENTERING_COIN_BOX_COIN_LEVEL:
						executeActionsForEnteringCoinBoxLevels();
						break;
					case INIT:
					default:
						try {
							waitCondition.await();
						} catch (InterruptedException e) {
							LOG.error("Wait condition failed", e);
						}
						break;
					}
				} finally {
					runLock.unlock();
				}
			}
		}
	}

	@Override
	public void promptForParkingLotNumber() {
		setViewState(ConsoleViewStateEnum.ENTERING_PARKING_LOT);
	}

	@Override
	public void promptForMoney(final int aParkingLotNumber) {
		setViewState(ConsoleViewStateEnum.DROPPING_IN_MONEY);

		// TODO: Any better ideas? Perhaps a Hash-Map as an "Object-Store" with
		// Keys?
		storeParkingLotNumber = aParkingLotNumber;
	}

	@Override
	public void displayParkingLotNumberAndParkingTime(
			final int aParkingLotNumber, final Date aPaidParkingTime) {

		String formattedDate = "-";
		if (aPaidParkingTime != null) {
			formattedDate = dateFormatter.print(aPaidParkingTime,
					LocaleContextHolder.getLocale());
		}

		print("view.info.parkingTime", false, aParkingLotNumber, formattedDate);
	}

	@Override
	public void displayParkingLotNumberInvalid() {
		print("view.enter.parkinglotnumber.invalid", false);
	}

	@Override
	public void displayShutdownMessage() {
		print("application.bye", false);
	}

	@Override
	public void displayAllInformation() {
		// TODO: Der View-State ist nicht notwendig -> Parameter -> Output
		setViewState(ConsoleViewStateEnum.DISPLAY_ALL_INFORMATION);
	}

	@Override
	public void displayBookedParkingLots(List<ParkingLot> parkingLots) {
		// TODO Auto-generated method stub
		this.parkingLots = parkingLots;
		setViewState(ConsoleViewStateEnum.DISPLAY_BOOKED_PARKINGLOTS);

	}

	public void executeActionForViewingAllInformation() {
		print("view.all.information.title.template", false, 2, messageProvider
				.get("view.parking.time.def").trim());

		ParkingTimeDefinitions parkingTimes = (ParkingTimeDefinitions) parkingTimeConfigurationProvider
				.get();
		int i = 1;
		int lastMinuteCount = 0;
		BigDecimal lastPrice = new BigDecimal(0);
		for (ParkingTimeDefinition parkingTime : parkingTimes
				.getParkingTimeDefinitions()) {
			for (int j = 0; j < parkingTime.getCountOfSuccessivePeriods(); j++) {
				BigDecimal newPrice = lastPrice.add(parkingTime
						.getPricePerPeriod());
				int newMinuteCount = lastMinuteCount
						+ parkingTime.getDurationOfPeriodInMinutes();
				print("view.all.information.parking.time", false, i,
						formatPrice(lastPrice), formatPrice(newPrice),
						newMinuteCount);
				lastPrice = newPrice;
				lastMinuteCount = newMinuteCount;
				i++;
			}
		}

		setViewState(ConsoleViewStateEnum.ENTERING_PARKING_LOT);
	}

	public void executeActionForDisplayBookedParkingLots() {
		SimpleDateFormat sdf = new SimpleDateFormat("YYYY-MM-dd  HH:mm");
		print("view.booked.parkinglots.title.template", false, sdf.format(now));
		for (ParkingLot parkingLot : parkingLots) {
			print("view.booked.parkinglot", false,
					getParkingLotNumber(parkingLot),
					getParkingLotPaidUntil(parkingLot),
					getParkingLotDifferenceTime(parkingLot),
					getParkingLotOutOfPaidTime(parkingLot));
		}

		setViewState(ConsoleViewStateEnum.ENTERING_PARKING_LOT);
	}

	private int getParkingLotNumber(ParkingLot parkingLot) {
		return parkingLot.getNumber();
	}

	private String getParkingLotPaidUntil(ParkingLot parkingLot) {
		if (parkingLot.getPaidUntil() == null) {
			return "--";
		} else {
			SimpleDateFormat sdf = new SimpleDateFormat("YYYY-MM-dd  HH:mm");
			return sdf.format(parkingLot.getPaidUntil());
		}
	}

	private String getParkingLotDifferenceTime(ParkingLot parkingLot) {

		DecimalFormat df = new DecimalFormat("00");
		int hh = 00;
		int mm = 00;
		int ss = 00;

		if (parkingLot.getPaidUntil() == null) {
			return "--";
		} else {
			hh = (int) ((parkingLot.getRemainingTimeInMillisec() / 1000) / 3600);
			mm = (int) (((parkingLot.getRemainingTimeInMillisec() / 1000) % 3600) / 60);
			ss = (int) ((parkingLot.getRemainingTimeInMillisec() / 1000) - ((mm * 60) + (hh * 3600)));

			String diffTime = hh + ":" + df.format(mm) + ":" + df.format(ss);
			return diffTime;
		}
	}

	private String getParkingLotOutOfPaidTime(ParkingLot parkingLot) {
		if (parkingLot.getPaidUntil() == null) {
			return "** Parkzeit abgelaufen **";
		} else if (parkingLot.getPaidUntil().getTime() <= now.getTime()) {
			return "** Parkzeit abgelaufen **";
		} else {
			return "";
		}
	}

	private String formatPrice(BigDecimal price) {
		// TODO: Auslagern in Helper
		price = price.setScale(2, BigDecimal.ROUND_DOWN);
		DecimalFormat df = new DecimalFormat();
		df.setMaximumFractionDigits(2);
		df.setMinimumFractionDigits(2);
		df.setGroupingUsed(false);
		return df.format(price);
	}

	/**
	 * Executes all necessary actions, which are required in the state
	 * 'EnteringParkingLotNumber'.
	 */
	public void executeActionsForStateEnteringParkingLotNumber() {
		print("view.enter.parkinglotnumber", true);
		String input = readFromConsole();

		if (input != null) {
			Integer parkingLotNumber = null;

			try {
				parkingLotNumber = new Integer(input);
			} catch (NumberFormatException e) {
				print("view.enter.parkinglotnumber.invalid", false);
			}

			if (parkingLotNumber != null) {
				setViewState(ConsoleViewStateEnum.INIT);
				notifyForParkingLotNumberEntered(parkingLotNumber);
			}
		}
	}

	/**
	 * Executes all necessary actions, which are required in the state
	 * 'DroppingInMoney'.
	 */
	public void executeActionsForDroppingInMoney() {
		print("view.enter.coins", true, storeParkingLotNumber);
		String input = readFromConsole();

		// if input == null: no input was provided or another event occurred.
		if (input != null) {
			boolean error = false;
			boolean drawback = false;

			try {
				parseAndInsertCoins(input);
			} catch (CoinBoxFullException e) {
				error = true;
				drawback = true;
				LOG.error("Received exception "
						+ "from slot machine: coin box is full!", e);

				if (e.isAllCoinBoxesFull()) {
					print("view.slot.machine.coin.box.full", false,
							e.getCoinValue());
				} else {
					print("view.slot.machine.coin.box.single.full", false,
							e.getCoinValue());
				}

			} catch (NoTransactionException e) {
				error = true;
				drawback = true;

				// This should not occur!
				LOG.error("Received exception "
						+ "from slot machine: no transaction!", e);
			} catch (InvalidCoinException e) {
				error = true;
				drawback = true;

				StringBuilder coinString = new StringBuilder();

				for (BigDecimal validCoin : e.getValidCoins()) {
					coinString.append(validCoin);
					coinString.append(", ");
				}

				LOG.error("Received exception "
						+ "from slot machine: invalid coin!", e);
				print("view.slot.machine.coin.invalid", false, coinString);
			} catch (NumberFormatException e) {
				error = true;
				print("view.slot.machine.format.invalid", false);
			}

			if (error) {
				if (drawback) {
					roleBackTransaction();
				}
			} else {
				// Reset view state if operation was successful.
				setViewState(ConsoleViewStateEnum.INIT);
				notifyForMoneyInserted(storeParkingLotNumber);
			}
		}
	}

	/**
	 * Displays a message for the drawback.
	 * 
	 * @param drawbackMap
	 *            the drawback map
	 */
	private void displayMessageForDrawback(Map<BigDecimal, Integer> drawbackMap) {
		StringBuilder sb = new StringBuilder();

		List<BigDecimal> keyList = new ArrayList<>(drawbackMap.keySet());
		for (int i = 0; i < keyList.size(); i++) {
			BigDecimal key = keyList.get(i);

			sb.append(drawbackMap.get(key));
			sb.append(" x ");
			sb.append(key);

			if (i < (keyList.size() - 1)) {
				sb.append(", ");
			}
		}

		print("view.slot.machine.drawback", false, sb.toString());

	}

	/**
	 * Parses the input string and inserts the coins into the slot machine.
	 * 
	 * @param anInput
	 *            the input string.
	 * @throws InvalidCoinException
	 *             thrown if an invalid coin was inserted.
	 * @throws NoTransactionException
	 *             thrown if no transaction was started.
	 * @throws CoinBoxFullException
	 *             thrown if one or alle coin boxes are full.
	 */
	private void parseAndInsertCoins(String anInput)
			throws CoinBoxFullException, NoTransactionException,
			InvalidCoinException {
		String split[] = anInput.split(COIN_SPLITTER);

		for (String coin : split) {
			BigDecimal coinDec = new BigDecimal(coin.trim());

			slotMachine.insertCoin(coinDec);
		}
	}

	/**
	 * Prints a text with the given key to the console. TODO sl: Move print
	 * methods to its own class
	 * 
	 * @param aKey
	 *            the key of the text to output.
	 * @param prompt
	 *            True if the printed text is an input prompt
	 * @param arguments
	 *            The arguments for the message.
	 */
	@Override
	protected void print(final String aKey, final boolean prompt,
			final Object... arguments) {
		if (prompt) {
			writer.print(MessageFormat.format(messageProvider.get(aKey).trim()
					+ messageProvider.get("view.prompt.separator"), arguments));
		} else {
			writer.println(MessageFormat.format(messageProvider.get(aKey)
					.trim(), arguments));
		}
	}

	/**
	 * Reads a string from the console and checks it for default actions.
	 * Default actions are shutdown of the ParkingMeter and abort of the current
	 * action / flow.
	 * 
	 * @return the string. Null if an event was thrown, an empty string if no
	 *         input could be red.
	 */
	private String readFromConsole() {
		String line = "";

		try {
			line = reader.readLine();
		} catch (IOException e) {
			LOG.error("Failed to read from console!", e);
		}

		if (line != null) {
			line = line.trim();

			if (line.toLowerCase().equals(ABORT_COMMAND)) {
				line = null;
				viewState = ConsoleViewStateEnum.INIT;
				roleBackTransaction();
				notifyForActionAborted();
			} else if (line.toLowerCase().equals(EXIT_COMMAND)) {
				line = null;
				viewState = ConsoleViewStateEnum.INIT;
				roleBackTransaction();
				notifyForShutdownRequested();
			}
		}

		return line;
	}

	/**
	 * Roles back the transaction.
	 */
	private void roleBackTransaction() {
		Map<BigDecimal, Integer> drawbackMap = slotMachine
				.rolebackTransaction();
		boolean displayMsg = false;

		for (Integer count : drawbackMap.values()) {
			if (count.intValue() > 0) {
				displayMsg = true;
				break;
			}
		}

		if (displayMsg) {
			displayMessageForDrawback(drawbackMap);
		}
	}

	/**
	 * @return the view state.
	 */
	public final ConsoleViewStateEnum getViewState() {
		return viewState;
	}

	/**
	 * Sets the view state. This method is synchronized to ensure integrity
	 * trough the different threads.
	 * 
	 * @param aState
	 *            the view state to set.
	 */
	private synchronized void setViewState(final ConsoleViewStateEnum aState) {
		viewState = aState;
		try {
			waitCondition.signal();
		} catch (Exception e) {
			// Nothing to do here..
		}
	}

	@Override
	public void shutdown() {
		LOG.info("Shutting down the view...");
		run = false;
		setViewState(ConsoleViewStateEnum.EXIT);
	}

	@Override
	public void displayMessageForDrawback() {
		displayMessageForDrawback(slotMachine.getDrawback());
	}

	@Override
	public void promptForNewCoinBoxLevels(
			List<CoinBoxLevel> someCurrentCoinBoxLevels) {
		coinBoxLevels = someCurrentCoinBoxLevels;
		setViewState(ConsoleViewStateEnum.ENTERING_COIN_BOX_COIN_LEVEL);
	}

	/**
	 * Executes all necessary actions, which are required in the state
	 * 'EnteringCoinBoxLevels'.
	 */
	public void executeActionsForEnteringCoinBoxLevels() {
		boolean failure = false;
		for (CoinBoxLevel cbl : coinBoxLevels) {

			// TODO: Evtl. auslagern.
			BigDecimal total = cbl.getCoinValue().multiply(
					new BigDecimal(cbl.getCurrentCoinCount()));
			print("view.info.coin.box.content", false, cbl.getCoinValue(),
					cbl.getCurrentCoinCount(), total);

			print("view.info.coin.box.content.new", true, cbl.getCoinValue());

			String input = readFromConsole();

			if (input == null) {
				// Input was null -> common command already executed
				setViewState(ConsoleViewStateEnum.INIT);
				return;
			} else {
				try {
					int coinCount = new Integer(input);

					if (coinCount >= 1 && coinCount <= 100) {
						cbl.setCurrentCoinCount(coinCount);
					} else {
						LOG.info("Coin count for coin box not in range!");
						failure = true;
						print("view.info.coin.box.content.limit", false);

						// Early return for quick failure
						return;
					}
				} catch (NumberFormatException e) {
					LOG.info("Coin count is not a valid number!");
					failure = true;
					print("view.slot.machine.format.invalid", false);

					// Early return for quick failure
					return;
				}

			}

			if (!failure) {
				setViewState(ConsoleViewStateEnum.INIT);
				notifyForCoinBoxLevelEntered(coinBoxLevels);
			}
		}
	}

}
