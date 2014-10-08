package ch.zhaw.swengineering.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement(name = "parkingMeter")
@XmlAccessorType(XmlAccessType.FIELD)
public class ParkingMeter {
	@XmlElement(name = "parkingLot")
	public List<ParkingLot> parkingLots;
}
