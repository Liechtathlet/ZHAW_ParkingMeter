package ch.zhaw.swengineering.model;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "parkingMeter")
@XmlAccessorType(XmlAccessType.FIELD)
public class ParkingMeter {
	@XmlElement(name = "parkingLot")
	public List<ParkingLot> parkingLots;
	
	/**
	 * Constructor for serialization and initialization.
	 */
	public ParkingMeter(){
		parkingLots = new ArrayList<ParkingLot>();
	}
}
