package ch.zhaw.swengineering.model;

import java.util.Date;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class CalculateRemainingTime {

	/**
	 * The Logger.
	 */
	private static final Logger LOG = LogManager
			.getLogger(CalculateRemainingTime.class);
	Date now = new Date();
	Date until = new Date();
	long diffMinutesOnly = 0;

	public CalculateRemainingTime(Date endBookingDate) {
		until = endBookingDate;
	}

	public long getDifferenceMinutes() {

		// Calendar cal_now = Calendar.getInstance();
		// Calendar cal_until = Calendar.getInstance();

		long diff = 0;

		if (until != null) {
			diff = until.getTime() - now.getTime();
			diffMinutesOnly = 0;
		} else {
			until = now;
		}
		if (diff >= 0) {
			long diffMinutes = diff / (60 * 1000) % 60;
			long diffHours = diff / (60 * 60 * 1000);
			diffMinutesOnly = diff / (60 * 1000);
		} else {
			diffMinutesOnly = 0;
		}
		LOG.debug("CalculatingRemainingTime:");
		LOG.debug("Input Time now: " + now.getTime());
		LOG.debug("Input Paid until: " + until.getTime());
		LOG.debug("Output CalculateDiffMinutes: " + diffMinutesOnly);

		return diffMinutesOnly;

	}

}
