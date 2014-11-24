package ch.zhaw.swengineering.model;

import java.math.BigDecimal;

public class ParkingTimeTableItem {
    private BigDecimal amount;
    private long minutes;

    public ParkingTimeTableItem(BigDecimal amount, long minutes) {
        this.amount = amount;
        this.minutes = minutes;
    }

    /**
     * Empty constructor for serialization.
     */
    public ParkingTimeTableItem() {
        super();
    }
    
    public BigDecimal getAmount() {
        return amount;
    }

    public long getMinutes() {
        return minutes;
    }
}
