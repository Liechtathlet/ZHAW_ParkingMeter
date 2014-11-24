package ch.zhaw.swengineering.model;

import java.math.BigDecimal;
import java.util.List;

public class ParkingTimeTable {
    private List<ParkingTimeTableItem> items;
    private int maxBookingTime;
    private BigDecimal maxPrice;

    public ParkingTimeTable(List<ParkingTimeTableItem> items,
            int maxBookingTime, BigDecimal maxPrice) {
        this.items = items;
        this.maxBookingTime = maxBookingTime;
        this.maxPrice = maxPrice;
    }

    /**
     * Empty constructor for serialization.
     */
    public ParkingTimeTable() {
        super();
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
