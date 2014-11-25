package ch.zhaw.swengineering.controller;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;  
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import ch.zhaw.swengineering.business.ParkingMeter;
import ch.zhaw.swengineering.helper.TransactionLogHandler;
import ch.zhaw.swengineering.model.CoinBoxLevel;
import ch.zhaw.swengineering.model.persistence.ParkingLot;
import ch.zhaw.swengineering.model.persistence.ParkingTimeDefinition;
import ch.zhaw.swengineering.model.persistence.TransactionLogEntry;
import ch.zhaw.swengineering.slotmachine.controller.IntelligentSlotMachineBackendInteractionInterface;

/**
 * Created by slang on 23.11.14.
 */
@Path("/")
@org.springframework.web.bind.annotation.RestController
public class RestController {

    /**
     * The Logger.
     */
    private static final Logger LOG = LogManager
            .getLogger(RestController.class);

    @Autowired
    private TransactionLogHandler transactionLog;

    @Autowired
    private ParkingMeter parkingMeter;

    @Autowired
    private IntelligentSlotMachineBackendInteractionInterface slotMachine;

    /**
     * Displays transaction logs. If count is a valid integer greater than 0, it
     * returns the appropriate. If count is not valid integer or lower than 1,
     * it returns all transaction logs.
     * 
     * @param countString
     *            Number of transaction logs to display.
     * @return Transaction log entries.
     */
    @GET
    @Path("/transactionLog")
    @RequestMapping("/transactionLog")
    public List<TransactionLogEntry> transactionLogNEntries(
            @RequestParam(value = "count", defaultValue = "") String countString) {
        int count = 0;
        try {
            count = Integer.parseInt(countString);
            return transactionLog.get(count);
        } catch (Exception e) {
            LOG.warn("Could not parse requested entry count!", e);
        }
        return transactionLog.getAll();
    }

    /**
     * Returns the transaction logs of the last 24 hours.
     * 
     * @return Transaction log entries.
     */
    @GET
    @Path("/transactionLog/24Hours")
    @RequestMapping("/transactionLog/24Hours")
    public List<TransactionLogEntry> transactionLog24Hours() {
        return transactionLog.getLast24Hours();
    }

    /**
     * Displays all parking lots, till when they are paid and their remaining
     * time.
     * 
     * @return Parking lot data.
     */
    @GET
    @Path("/parkingLots")
    @RequestMapping("/parkingLots")
    public List<ParkingLot> parkingLots() {
        return parkingMeter.getParkingLots();
    }

    /**
     * Displays all coin boxes with their current and maximum coin counts.
     * 
     * @return Coin boxes data.
     */
    @GET
    @Path("/coinBoxes")
    @RequestMapping("/coinBoxes")
    public List<CoinBoxLevel> coinBoxes() {
        return slotMachine.getCurrentCoinBoxLevel();
    }

    /**
     * Displays the parking time definitions.
     * 
     * @return Parking time definitions.
     */
    @GET
    @Path("/parkingTimeDefinitions")
    @RequestMapping("/parkingTimeDefinitions")
    public List<ParkingTimeDefinition> parkingTimeDefinitions() {
        return parkingMeter.getParkingTimeDefinitions();
    }
}
