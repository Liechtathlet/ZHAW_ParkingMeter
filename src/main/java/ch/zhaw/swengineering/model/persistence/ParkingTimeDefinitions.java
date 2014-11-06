package ch.zhaw.swengineering.model.persistence;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Daniel Brun
 * 
 *         Class to store the parking time definitions.
 */
@XmlRootElement(name = "parkingTimeDefinitions")
@XmlAccessorType(XmlAccessType.FIELD)
public class ParkingTimeDefinitions {

    @XmlElement(name = "parkingTimeDefinition")
    private List<ParkingTimeDefinition> parkingTimeDefinitions;

    /**
     * Creates a new instance of this class.
     */
    public ParkingTimeDefinitions() {
        parkingTimeDefinitions = new ArrayList<ParkingTimeDefinition>();
    }

    /**
     * @return the parkingTimeDefinitions
     */
    public List<ParkingTimeDefinition> getParkingTimeDefinitions() {
        return parkingTimeDefinitions;
    }

    /**
     * @param parkingTimeDefinitions
     *            the parkingTimeDefinitions to set
     */
    public void setParkingTimeDefinitions(
            List<ParkingTimeDefinition> parkingTimeDefinitions) {
        this.parkingTimeDefinitions = parkingTimeDefinitions;
    }
}
