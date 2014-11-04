package ch.zhaw.swengineering.model;

import java.math.BigDecimal;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

/**
 * @author Daniel Brun
 * 
 *         Class to store the parking time definitions.
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class ParkingTimeDefinition implements Comparable<ParkingTimeDefinition> {

    private BigDecimal pricePerPeriod;
    private Integer durationOfPeriodInMinutes;
    private Integer countOfSuccessivePeriods;
    private int orderId;

    /**
     * Creates a new instance of this class.
     */
    public ParkingTimeDefinition() {
        // Nothing to do here.
    }

    /**
     * Creates a new instance of this class.
     * 
     * @param pricePerPeriod
     *            The price which must be payed per period.
     * @param durationOfPeriodInMinutes
     *            The duration of the period in minutes.
     * @param countOfSuccessivePeriods
     *            The count of successive periods. if the count is set to null
     *            or zero, it will be interpreted as an infinite and linear
     *            definition.
     * @param orderId
     *            The order in which the definitions should be applied (starting
     *            from 1).
     */
    public ParkingTimeDefinition(BigDecimal pricePerPeriod,
            Integer durationOfPeriodInMinutes,
            Integer countOfSuccessivePeriods, int orderId) {
        super();
        this.pricePerPeriod = pricePerPeriod;
        this.durationOfPeriodInMinutes = durationOfPeriodInMinutes;
        this.countOfSuccessivePeriods = countOfSuccessivePeriods;
        this.orderId = orderId;
    }

    /**
     * @return the pricePerPeriod
     */
    public BigDecimal getPricePerPeriod() {
        return pricePerPeriod;
    }

    /**
     * @return the durationOfPeriodInMinutes
     */
    public Integer getDurationOfPeriodInMinutes() {
        return durationOfPeriodInMinutes;
    }

    /**
     * @return the countOfSuccessivePeriods
     */
    public Integer getCountOfSuccessivePeriods() {
        return countOfSuccessivePeriods;
    }

    /**
     * @return the orderId
     */
    public int getOrderId() {
        return orderId;
    }

    /**
     * @param pricePerPeriod
     *            the pricePerPeriod to set
     */
    public void setPricePerPeriod(BigDecimal pricePerPeriod) {
        this.pricePerPeriod = pricePerPeriod;
    }

    /**
     * @param durationOfPeriodInMinutes
     *            the durationOfPeriodInMinutes to set
     */
    public void setDurationOfPeriodInMinutes(Integer durationOfPeriodInMinutes) {
        this.durationOfPeriodInMinutes = durationOfPeriodInMinutes;
    }

    /**
     * @param countOfSuccessivePeriods
     *            the countOfSuccessivePeriods to set
     */
    public void setCountOfSuccessivePeriods(Integer countOfSuccessivePeriods) {
        this.countOfSuccessivePeriods = countOfSuccessivePeriods;
    }

    /**
     * @param orderId
     *            the orderId to set
     */
    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    @Override
    public int compareTo(ParkingTimeDefinition o) {
        if (orderId == o.orderId) {
            return 0;
        } else if (orderId <= o.orderId) {
            return -1;
        } else {
            return 1;
        }
    }
}
