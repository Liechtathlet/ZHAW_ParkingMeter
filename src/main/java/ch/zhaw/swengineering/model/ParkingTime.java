package ch.zhaw.swengineering.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

@XmlAccessorType(XmlAccessType.FIELD)
public class ParkingTime {
	public float startPrice;
	public float endPrice;
	public int durationInMinutes;
}
