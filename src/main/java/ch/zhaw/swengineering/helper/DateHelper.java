package ch.zhaw.swengineering.helper;

import java.util.Date;

/**
 * @author Daniel Brun
 * 
 *         Helper class for date operations.
 */
public final class DateHelper {

    /**
     * Hide default constructor.
     */
    private DateHelper() {
        // Hide constructor
    }

    /**
     * Calculates the time difference between the first date and the second
     * date.
     * 
     * @param date1
     *            the first date parameter.
     * @param date2
     *            the second date parameter.
     * @return the difference in days (if necessary), hours and minutes.
     */
    public static String calculateFormattedTimeDifference(final Date date1,
            final Date date2) {
        AssertHelper.isNotNull(date1, "date1");
        AssertHelper.isNotNull(date2, "date2");
        
        String result = null;
        boolean negative = false;
        long diff = 0;

        if (date1.before(date2)) {
            negative = true;
        }

        if (negative) {
            result = "-";
            diff = date2.getTime() - date1.getTime();
        } else {
            result = "+";
            diff = date1.getTime() - date2.getTime();
        }

        long days = (int) (diff / 1000 / 60 / 60 / 24);
        long hours = (int) ((diff / 1000 / 60 / 60) - (days * 24));
        long minutes = (int) ((diff / 1000 / 60) 
                - (days * 24 * 60) - (hours * 60));

        if (days > 0) {
            result += String.format("%02d:%02d:%02d", days, hours, minutes);
        } else {
            result += String.format("%02d:%02d", hours, minutes);
        }

        return result;
    }
}
