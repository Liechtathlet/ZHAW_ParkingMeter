package ch.zhaw.swengineering.model;

import javax.xml.bind.annotation.*;
import java.util.List;

@XmlRootElement(name = "parkingTimes")
@XmlAccessorType(XmlAccessType.FIELD)
public class ParkingTimes {
	@XmlElement(name = "parkingTime")
	public List<ParkingTime> parkingTimes;
}
