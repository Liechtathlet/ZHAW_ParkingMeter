package ch.zhaw.swengineering.event;

import java.util.EventObject;

/**
 * @author Daniel Brun
 * 
 *         Event which is thrown if the action was aborted by the user. TODO:
 *         Evtl. Rename Event
 */
public class MoneyInsertedEvent extends EventObject {

    /**
     * GUID.
     */
    private static final long serialVersionUID = 7257553615509542001L;

    private int parkingLotNumber;

    /**
     * Creates a new instance of this class.
     * 
     * @param source
     *            the source object.
     * @param aParkingLotNumber
     *            The parking lot number.
     */
    public MoneyInsertedEvent(final Object source, int aParkingLotNumber) {
        super(source);
        this.parkingLotNumber = aParkingLotNumber;
    }

    /**
     * @return the parkingLotNumber
     */
    public int getParkingLotNumber() {
        return parkingLotNumber;
    }

}
