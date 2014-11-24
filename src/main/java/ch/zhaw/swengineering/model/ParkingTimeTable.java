package ch.zhaw.swengineering.model;

import java.math.BigDecimal;
import java.util.List;

public class ParkingTimeTable {
    private final List<ParkingTimeTableItem> items;
    private final int maxBookingTime;
    private final BigDecimal maxPrice;

    public ParkingTimeTable(
            List<ParkingTimeTableItem> items,
            int maxBookingTime,
            BigDecimal maxPrice) {
        this.items = items;
        this.maxBookingTime = maxBookingTime;
        this.maxPrice = maxPrice;
    }

    public BigDecimal getMaxPrice() {
        return maxPrice;
    }

    public int getMaxBookingTime() {
        return maxBookingTime;
    }

    public List<ParkingTimeTableItem> getItems() {
        return items;
    }

}
