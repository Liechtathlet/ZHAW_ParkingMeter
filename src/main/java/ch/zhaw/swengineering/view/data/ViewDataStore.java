package ch.zhaw.swengineering.view.data;

/**
 * @author Daniel Brun
 * 
 *         Class to store data which is required in the different processes in
 *         the view.
 */
public class ViewDataStore {

    private Integer parkingLotNumber;
    
    /**
     * Creates a new instance of this class.
     */
    public ViewDataStore() {
        parkingLotNumber = null;
    }

    /**
     * @return the parkingLotNumber
     */
    public Integer getParkingLotNumber() {
        return parkingLotNumber;
    }

    /**
     * @param parkingLotNumber the parkingLotNumber to set
     */
    public void setParkingLotNumber(Integer parkingLotNumber) {
        this.parkingLotNumber = parkingLotNumber;
    }
    
}
