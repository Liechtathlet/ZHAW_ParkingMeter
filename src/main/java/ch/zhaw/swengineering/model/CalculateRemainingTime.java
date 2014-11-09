package ch.zhaw.swengineering.model;

import java.util.Calendar;
import java.util.Date;

public class CalculateRemainingTime {

	Date now = new Date();
	Date until = new Date();

	public CalculateRemainingTime(Date endBookingDate) {
		until = endBookingDate;
	}

	public long getDifferenceMinutes() {

		Calendar cal_now = Calendar.getInstance();
		Calendar cal_until = Calendar.getInstance();

		// cal_now.setTime(now);
		// cal_until.setTime(until);
		long diff = 0;
		long diffMinutesOnly = 0;
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

		return diffMinutesOnly;

	}

}
