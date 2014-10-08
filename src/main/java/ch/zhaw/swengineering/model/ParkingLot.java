package ch.zhaw.swengineering.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlValue;
import java.util.Date;

@XmlAccessorType(XmlAccessType.FIELD)
public class ParkingLot {

	@XmlAttribute
	public int number;

	@XmlAttribute
	public Date paidUntil;
}
