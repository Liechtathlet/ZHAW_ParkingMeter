/**
 * 
 */
package ch.zhaw.swengineering.view.console;

import java.io.BufferedReader;
import java.io.IOException;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import ch.zhaw.swengineering.event.ParkingLotEnteredEvent;
import ch.zhaw.swengineering.event.ViewEventListener;
import ch.zhaw.swengineering.helper.MessageProvider;
import ch.zhaw.swengineering.view.SimulationView;

/**
 * @author Daniel Brun
 *
 * Console implementation of the interface {@link ch.zhaw.swengineering.view.SimulationView}
 */
public class ConsoleSimulationView extends SimulationView {

	private static final Logger LOG = LogManager.getLogger(ConsoleSimulationView.class);

	@Autowired
	private MessageProvider messageProvider;

	@Autowired
	private BufferedReader reader;

	private void notifyParkingLotNumber(int parkingLotNumber) {
		ParkingLotEnteredEvent event = new ParkingLotEnteredEvent(this, parkingLotNumber);
		for(ViewEventListener listener : eventListeners){
			listener.parkingLotEntered(event);
		}
	}
	
	@Override
	public void run() {
		// Nothing to do here for command line.
		// Maybe some info text should be printed out.
	}

	@Override
	public void showParkingLotMessage() throws IOException {
		int parkingLotNumber = getParkingLotNumber();
		notifyParkingLotNumber(parkingLotNumber);
	}

	public int getParkingLotNumber() throws IOException {
		System.out.print(messageProvider.get("parking lot number").trim() + " ");

		String line = reader.readLine();
		return Integer.parseInt(line);
	}
}
