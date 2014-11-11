package ch.zhaw.swengineering.helper;

import static org.junit.Assert.assertEquals;

import java.util.Calendar;
import java.util.Date;

import org.junit.Test;

/**
 * @author Daniel Brun
 * 
 *         Tests the class 'DateHelper'
 */
public class DateHelperTest {

    /**
     * Test method for
     * {@link DateHelper#calculateFormattedTimeDifference(java.util.Date, java.util.Date)}
     * 
     * Test: Parameter one is null Expected Result: IllegalArgumentException .
     */
    @Test(expected = IllegalArgumentException.class)
    public final void testCalculateFormattedTimeDifferenceNullParamOne() {
        DateHelper.calculateFormattedTimeDifference(null, null);
    }

    /**
     * Test method for
     * {@link DateHelper#calculateFormattedTimeDifference(java.util.Date, java.util.Date)}
     * 
     * Test: Parameter two is null Expected Result: IllegalArgumentException .
     */
    @Test(expected = IllegalArgumentException.class)
    public final void testCalculateFormattedTimeDifferenceNullParamTwo() {
        DateHelper.calculateFormattedTimeDifference(new Date(), null);
    }

    /**
     * Test method for
     * {@link DateHelper#calculateFormattedTimeDifference(java.util.Date, java.util.Date)}
     * 
     * Test: Tests a positive time difference.
     * Expected Result: Positive time difference .
     */
    @Test
    public final void testCalculateFormattedTimeDifferencePositive() {
        Calendar calOne = Calendar.getInstance();
        Calendar calTwo = Calendar.getInstance();

        calOne.set(Calendar.AM_PM, Calendar.AM);
        calTwo.set(Calendar.AM_PM, Calendar.AM);

        // Test hour and minute.
        calOne.set(2014, 10, 2, 12, 30, 0);
        calTwo.set(2014, 10, 2, 10, 15, 0);

        String result = DateHelper.calculateFormattedTimeDifference(
                calOne.getTime(), calTwo.getTime());

        assertEquals("+02:15", result);

        // Test minute
        calOne.set(2014, 10, 2, 12, 30, 0);
        calTwo.set(2014, 10, 2, 12, 15, 0);

        result = DateHelper.calculateFormattedTimeDifference(calOne.getTime(),
                calTwo.getTime());

        assertEquals("+00:15", result);

        // Test day, hour and minute
        calOne.set(2014, 10, 3, 12, 30, 0);
        calTwo.set(2014, 10, 2, 11, 15, 0);

        result = DateHelper.calculateFormattedTimeDifference(calOne.getTime(),
                calTwo.getTime());

        assertEquals("+01:01:15", result);
    }

    /**
     * Test method for
     * {@link DateHelper#calculateFormattedTimeDifference(java.util.Date, java.util.Date)}
     * 
     * Test: Tests a negative time difference .
     * Expected Result: negative time difference .
     */
    @Test
    public final void testCalculateFormattedTimeDifferenceNegative() {
        Calendar calOne = Calendar.getInstance();
        Calendar calTwo = Calendar.getInstance();

        calOne.set(Calendar.AM_PM, Calendar.AM);
        calTwo.set(Calendar.AM_PM, Calendar.AM);
        
        // Test hour and minute
        calOne.set(2014, 10, 2, 10, 15, 0);
        calTwo.set(2014, 10, 2, 12, 30, 0);

        String result = DateHelper.calculateFormattedTimeDifference(
                calOne.getTime(), calTwo.getTime());

        assertEquals("-02:15", result);

        // Test minute
        calOne.set(2014, 10, 2, 12, 15, 0);
        calTwo.set(2014, 10, 2, 12, 30, 0);

        result = DateHelper.calculateFormattedTimeDifference(calOne.getTime(),
                calTwo.getTime());

        assertEquals("-00:15", result);

        // Test day, hour and minute
        calOne.set(2014, 10, 2, 11, 15, 0);
        calTwo.set(2014, 10, 3, 12, 30, 0);

        result = DateHelper.calculateFormattedTimeDifference(calOne.getTime(),
                calTwo.getTime());

        assertEquals("-01:01:15", result);
    }
}
