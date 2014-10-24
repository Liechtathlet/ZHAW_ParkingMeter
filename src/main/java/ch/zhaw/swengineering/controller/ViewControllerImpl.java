/**
 * 
 */
package ch.zhaw.swengineering.controller;

import java.io.IOException;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;

import ch.zhaw.swengineering.event.ParkingLotEnteredEvent;
import ch.zhaw.swengineering.event.ViewEventListener;
import ch.zhaw.swengineering.helper.ConfigurationProvider;
import ch.zhaw.swengineering.model.CoinBoxes;
import ch.zhaw.swengineering.model.ParkingLot;
import ch.zhaw.swengineering.model.SecretCodes;
import ch.zhaw.swengineering.view.SimulationView;

/**
 * @author Daniel Brun
 * 
 *         Controller for the view.
 * 
 */
@Controller
public class ViewControllerImpl implements ViewController, ViewEventListener {

	private static final Logger LOG = LogManager
			.getLogger(ViewControllerImpl.class);

	@Autowired
	private SimulationView view;

	@Autowired
	private ParkingMeterController parkingMeterController;

	@Autowired
	@Qualifier("coinBoxes")
	private ConfigurationProvider coinBoxesProvider;
	private CoinBoxes coinBoxes;

	@Autowired
	@Qualifier("secretCodes")
	private ConfigurationProvider secretCodesProvider;
	private SecretCodes secretCodes;

	@Override
	public void start() {
		LOG.info("Starting controller...");

		// Register event listener.
		view.addViewEventListener(this);

		loadData();

		// Start simulation view.
		view.startSimulationView();

		try {
			view.showParkingLotMessage();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void parkingLotEntered(ParkingLotEnteredEvent parkingLotEnteredEvent) {
		LOG.debug("User entered parking lot number: "
				+ parkingLotEnteredEvent.getParkingLotNumber());

		// Step One: Check if it is a parking lot number
		if (parkingMeterController.isParkingLot(parkingLotEnteredEvent
				.getParkingLotNumber())) {

		}
	}

	private void loadData() {
		LOG.info("Loading data...");

		LOG.info("Loading CoinBoxes...");
		if (coinBoxesProvider != null && coinBoxesProvider.get() != null) {
			coinBoxes = (CoinBoxes) coinBoxesProvider.get();
		}

		LOG.info("Loading SecretCodes...");
		if (secretCodesProvider != null && secretCodesProvider.get() != null) {
			secretCodes = (SecretCodes) secretCodesProvider.get();
		}
	}
}
