package ch.zhaw.swengineering.view.data;

import java.util.List;

import ch.zhaw.swengineering.model.CoinBoxLevel;

/**
 * @author Daniel Brun
 * 
 *         Class to store data which is required in the different processes in
 *         the view.
 */
public class ViewDataStore {

    private Integer parkingLotNumber;
    private List<CoinBoxLevel> currentCoinBoxLevels;

    /**
     * Creates a new instance of this class.
     */
    public ViewDataStore() {
        parkingLotNumber = null;
        currentCoinBoxLevels = null;
    }

    /**
     * @return the parkingLotNumber
     */
    public Integer getParkingLotNumber() {
        return parkingLotNumber;
    }

    /**
     * @param parkingLotNumber
     *            the parkingLotNumber to set
     */
    public void setParkingLotNumber(Integer parkingLotNumber) {
        this.parkingLotNumber = parkingLotNumber;
    }

    /**
     * @return the currentCoinBoxLevels
     */
    public List<CoinBoxLevel> getCurrentCoinBoxLevels() {
        return currentCoinBoxLevels;
    }

    /**
     * @param currentCoinBoxLevels the currentCoinBoxLevels to set
     */
    public void setCurrentCoinBoxLevels(List<CoinBoxLevel> currentCoinBoxLevels) {
        this.currentCoinBoxLevels = currentCoinBoxLevels;
    }
}
