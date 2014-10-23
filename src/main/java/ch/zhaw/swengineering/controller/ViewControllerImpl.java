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
import ch.zhaw.swengineering.model.ParkingMeter;
import ch.zhaw.swengineering.model.SecretCodes;
import ch.zhaw.swengineering.view.SimulationView;

/**
 * @author Daniel Brun
 *
 * Controller for the view.
 * 
 * TODO: Het öpert en bessere Name?
 * TODO sl: GuiController? Es wird denn wahrschinli no en RestController geh..
 * TODO: db: Hani mer au überleit, denn isch console au = gui
 */
@Controller
public class ViewControllerImpl implements ViewController, ViewEventListener {

	private static final Logger LOG = LogManager.getLogger(ViewControllerImpl.class);
	
	@Autowired
	private SimulationView view;
	
	@Autowired
	@Qualifier("parkingMeter")
	private ConfigurationProvider parkingMeterProvider;
	private ParkingMeter parkingMeter;
	
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

		//Register event listener.
		view.addViewEventListener(this);
		
		loadData();
		
		//Start simulation view.
		view.startSimulationView();

		try {
			view.showParkingLotMessage();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void parkingLotEntered(ParkingLotEnteredEvent parkingLotEnteredEvent) {
		System.out.println("User entered parking lot number: " + parkingLotEnteredEvent.getParkingLotNumber());
		
		//Step One: Check if it is a parking lot number
		for(ParkingLot parkingLot : parkingMeter.parkingLots){
			if(parkingLot.number == parkingLotEnteredEvent.getParkingLotNumber()){
				//Call next step
			}
		}
		
		//Step Two: Check if it is a secret code.
	}
	
	private void loadData(){
		LOG.info("Loading necessary data...");
		
		LOG.info("Loading ParkingLots...");
		if(parkingMeterProvider != null && parkingMeterProvider.get() != null){
			parkingMeter = (ParkingMeter)parkingMeterProvider.get();
		}
		
		LOG.info("Loading CoinBoxes...");
		if(coinBoxesProvider != null && coinBoxesProvider.get() != null){
			coinBoxes = (CoinBoxes)coinBoxesProvider.get();
		}
		
		LOG.info("Loading SecretCodes...");
		if(secretCodesProvider != null && secretCodesProvider.get() != null){
			secretCodes = (SecretCodes)secretCodesProvider.get();
		}
	}
}
