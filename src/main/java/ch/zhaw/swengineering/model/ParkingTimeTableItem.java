package ch.zhaw.swengineering.model;

import java.math.BigDecimal;

public class ParkingTimeTableItem {
    private final BigDecimal amount;
    private final long minutes;

    public ParkingTimeTableItem(BigDecimal amount, long minutes) {
        this.amount = amount;
        this.minutes = minutes;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public long getMinutes() {
        return minutes;
    }
}
