package ch.zhaw.swengineering.model;

import static org.junit.Assert.assertEquals;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;

public class CalculateRemainingTimeTest {
	private Date now = new Date();
	private Date nowDate = new Date();
	private Date until = new Date();
	private Date endBookingDate1 = new Date();
	private Date endBookingDate2 = new Date();
	private Date endBookingDate3 = new Date();
	private Date endBookingDate4 = new Date();

	public CalculateRemainingTimeTest() {

	}

	@Before
	public void setUp() {

		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
				"dd.MM.yyyy HH:mm");
		String inputEndBookingDate1 = "11.11.2014 18:00";
		String inputEndBookingDate2 = "11.11.2014 21:00";
		String inputEndBookingDate3 = "11.11.2014 20:00";
		String inputEndBookingDate4 = "12.11.2014 01:30";
		String inputNowDate = "11.11.2014 20:00";
		try {
			endBookingDate1 = simpleDateFormat.parse(inputEndBookingDate1);
			endBookingDate2 = simpleDateFormat.parse(inputEndBookingDate2);
			endBookingDate3 = simpleDateFormat.parse(inputEndBookingDate3);
			endBookingDate4 = simpleDateFormat.parse(inputEndBookingDate4);
			nowDate = simpleDateFormat.parse(inputNowDate);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Test
	public void whenUntilTimeIsSmallerThanNowTime() {
		// initialize variables
		now = nowDate;
		until = endBookingDate1;

		// run
		CalculateRemainingTime calculateRemainingTime = new CalculateRemainingTime(
				until);
		calculateRemainingTime.getDifferenceMinutes();

		// check the result
		assertEquals((long) 0, calculateRemainingTime.diffMinutesOnly);
	}

	@Test
	public void whenUntilTimeIsGreatherThanNowTime() {
		// initialize variables
		now = nowDate;
		until = endBookingDate2;

		// run
		CalculateRemainingTime calculateRemainingTime = new CalculateRemainingTime(
				until);
		calculateRemainingTime.now = now;
		calculateRemainingTime.getDifferenceMinutes();

		// check the result
		assertEquals((long) 60, calculateRemainingTime.diffMinutesOnly);

	}

	@Test
	public void whenUntilTimeIsEqualsThanNowTime() {
		// initialize variables
		now = nowDate;
		until = endBookingDate3;

		// run
		CalculateRemainingTime calculateRemainingTime = new CalculateRemainingTime(
				until);
		calculateRemainingTime.now = now;
		calculateRemainingTime.getDifferenceMinutes();

		// check the result
		assertEquals((long) 0, calculateRemainingTime.diffMinutesOnly);

	}

	@Test
	public void whenUntilTimeIsTomorrowAndNowTimeToday() {
		// initialize variables
		now = nowDate;
		until = endBookingDate4;

		// run
		CalculateRemainingTime calculateRemainingTime = new CalculateRemainingTime(
				until);
		calculateRemainingTime.now = now;
		calculateRemainingTime.getDifferenceMinutes();

		// check the result
		assertEquals((long) 330, calculateRemainingTime.diffMinutesOnly);

	}

}
