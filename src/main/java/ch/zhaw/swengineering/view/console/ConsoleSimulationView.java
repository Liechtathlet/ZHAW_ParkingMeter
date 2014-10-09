/**
 * 
 */
package ch.zhaw.swengineering.view.console;

import java.util.Scanner;

import org.springframework.stereotype.Component;

import ch.zhaw.swengineering.view.SimulationViewActionHandler;

/**
 * @author Daniel Brun
 *
 * Console implementation of the interface {@link SimulationViewActionHandler}
 */
@Component
public class ConsoleSimulationView implements SimulationViewActionHandler, Runnable{

	private Thread consoleThread;
	private boolean run;
	
	@Override
	public void startSimulationGui() {
		
		if(consoleThread == null){
			run = true;
			
			//Create and start thread
			consoleThread = new Thread(this);
			consoleThread.start();
		}else{
			//TODO: Throw exception. 
		}

	}
	
	@Override
	public void run() {
		Scanner consoleScanner = new Scanner(System.in);
		//Read console input
		while(run){
			System.out.println("Parkplatznummer: >");
			String userInputLine = consoleScanner.nextLine();
			
			System.out.println("Input: " + userInputLine);
			//TODO: hand over to controller.
		}
	}

	

}
