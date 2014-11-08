package ch.zhaw.swengineering.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;

import ch.zhaw.swengineering.helper.ConfigurationProvider;
import ch.zhaw.swengineering.model.AllParkingCharge;
import ch.zhaw.swengineering.model.ParkingLotBooking;
import ch.zhaw.swengineering.model.persistence.ParkingLot;
import ch.zhaw.swengineering.model.persistence.ParkingMeter;
import ch.zhaw.swengineering.model.persistence.ParkingTimeDefinition;
import ch.zhaw.swengineering.model.persistence.ParkingTimeDefinitions;
import ch.zhaw.swengineering.model.persistence.SecretActionEnum;
import ch.zhaw.swengineering.model.persistence.SecretCodes;

/**
 * @author Daniel Brun Implementation of the {@link ParkingMeterController}
 */
@Controller
public class ParkingMeterControllerImpl implements ParkingMeterController {

	/**
	 * The Logger.
	 */
	private static final Logger LOG = LogManager
			.getLogger(ParkingMeterControllerImpl.class);

	/**
	 * ConfigurationProvider for the 'ParkingMeter'.
	 */
	@Autowired
	@Qualifier("parkingMeter")
	private ConfigurationProvider parkingMeterProvider;

	@Autowired
	@Qualifier("secretCodes")
	private ConfigurationProvider secretCodesProvider;

	@Autowired
	@Qualifier("parkingTimeDef")
	private ConfigurationProvider parkingTimeDefinitionProvider;

	/**
	 * The ParkingMeter.
	 */
	private ParkingMeter parkingMeter;

	private SecretCodes secretCodes;

	private ParkingTimeDefinitions definitions;

	/**
	 * Initializes the class after the properties have been injected.
	 */
	@PostConstruct
	public final void init() {
		LOG.info("Initialize ParkingMeter Controller");

		LOG.info("Load ParkingLots");
		if (parkingMeterProvider != null && parkingMeterProvider.get() != null) {
			parkingMeter = (ParkingMeter) parkingMeterProvider.get();
		}

		LOG.info("Loading SecretCodes...");
		if (secretCodesProvider != null && secretCodesProvider.get() != null) {
			secretCodes = (SecretCodes) secretCodesProvider.get();
			validateSecretCodes();
		}

		LOG.info("Loading ParkingTimeDefinitions...");
		if (parkingTimeDefinitionProvider != null
				&& parkingTimeDefinitionProvider.get() != null) {
			definitions = (ParkingTimeDefinitions) parkingTimeDefinitionProvider
					.get();
			validateParkingTimeDefinitions();
			sortParkingTimeDefinitions();
		}
	}

	@Override
	public ParkingLot getParkingLot(final int aNumber) {
		ParkingLot parkingLot = null;

		// TODO: Not efficient...
		for (ParkingLot pl : parkingMeter.parkingLots) {
			if (pl.number == aNumber) {
				parkingLot = pl;
				break;
			}
		}

		return parkingLot;
	}

	public void callAllBookedParkingLots() {
		ArrayList<ParkingLot> parkingLots = new ArrayList<ParkingLot>();
		parkingLots = (ArrayList<ParkingLot>) parkingMeter.parkingLots;

		AllParkingCharge allParkingCharge = new AllParkingCharge(parkingLots);

	}

	@Override
	public SecretActionEnum getSecretAction(int secretKey) throws Exception {

		if (secretCodes == null) {
			// /TODO: Please change...no proper error
			throw new Exception();
		}

		// TODO: What the hell?
		for (Object o : secretCodes.getCodeMapping().entrySet()) {
			Map.Entry secretCodeEntry = (Map.Entry) o;
			if (secretCodeEntry.getKey().equals(secretKey)) {
				return (SecretActionEnum) secretCodeEntry.getValue();
			}
		}

		throw new IllegalArgumentException();
	}

	@Override
	public void persistBooking(ParkingLotBooking aBooking) {
		// TODO Auto-generated method stub

	}

	@Override
	public ParkingLotBooking calculateBookingForParkingLot(int aParkingLot,
			BigDecimal someInsertedMoney) {

		ParkingLotBooking booking = new ParkingLotBooking(aParkingLot,
				someInsertedMoney);

		BigDecimal leftoverMoney = someInsertedMoney;
		boolean minimumBooking = false;
		int bookingInMinutes = 0;

		// Loop over definitions...
		for (ParkingTimeDefinition def : definitions
				.getParkingTimeDefinitions()) {

			int periodCount = leftoverMoney
					.divideToIntegralValue(def.getPricePerPeriod())
					.toBigInteger().intValue();

			if (periodCount == 0) {
				booking.setDrawbackMoney(leftoverMoney);
				booking.setNotEnoughMoney(!minimumBooking);
				break;
			} else {
				// At least one period is covered.
				minimumBooking = true;

				int countOfSuccessivePeriods = def
						.getCountOfSuccessivePeriods().intValue();

				// Limit period count
				if (countOfSuccessivePeriods != 0
						&& periodCount > countOfSuccessivePeriods) {
					periodCount = countOfSuccessivePeriods;
				}

				// Calculate
				bookingInMinutes += def.getDurationOfPeriodInMinutes()
						.intValue() * periodCount;
				leftoverMoney = leftoverMoney.subtract(def.getPricePerPeriod()
						.multiply(new BigDecimal(periodCount)));
			}
		}

		// Do final calculations.
		if (minimumBooking) {
			booking.setDrawbackMoney(leftoverMoney);
			booking.setChargedMoney(someInsertedMoney.subtract(leftoverMoney));

			Calendar cal = Calendar.getInstance();
			booking.setPaidFrom(cal.getTime());

			cal.add(Calendar.MINUTE, bookingInMinutes);

			booking.setPaidTill(cal.getTime());
		}

		return booking;
	}

	/**
	 * Validates if multiple codes are stored for an action.
	 */
	public final void validateSecretCodes() {
		Map<Integer, SecretActionEnum> mapping = secretCodes.getCodeMapping();

		Collection<SecretActionEnum> values = mapping.values();
		for (SecretActionEnum actionEnum : SecretActionEnum.values()) {
			int count = 0;
			for (SecretActionEnum actionEnumToCompare : values) {
				if (actionEnumToCompare.equals(actionEnum)) {
					count++;
				}
			}

			if (count > 1) {
				throw new IllegalArgumentException(
						"A secret action can only be mapped "
								+ "once to a secret code!");
			}
		}

	}

	/**
	 * Validates the loaded parking time definitions.
	 */
	public final void validateParkingTimeDefinitions() {
		if (definitions.getParkingTimeDefinitions() == null
				|| definitions.getParkingTimeDefinitions().size() == 0) {
			throw new IllegalArgumentException(
					"At least one parking time definition must be configured.");
		}
	}

	/**
	 * Executes a sort on the parking time definitions.
	 */
	private void sortParkingTimeDefinitions() {
		Collections.sort(definitions.getParkingTimeDefinitions());
	}
}
