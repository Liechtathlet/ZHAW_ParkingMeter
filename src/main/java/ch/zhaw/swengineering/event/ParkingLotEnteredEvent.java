package ch.zhaw.swengineering.event;

import java.util.EventObject;

public class ParkingLotEnteredEvent extends EventObject {

	private final int parkingLotNumber;

	public ParkingLotEnteredEvent(Object source, int parkingLotNumber) {
		super(source);
		this.parkingLotNumber = parkingLotNumber;
	}

	public int getParkingLotNumber() {
		return parkingLotNumber;
	}
}
