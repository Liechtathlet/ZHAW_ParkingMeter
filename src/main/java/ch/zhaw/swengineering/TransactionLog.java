package ch.zhaw.swengineering;

import org.springframework.stereotype.Component;

import java.sql.Time;
import java.util.Date;

public interface TransactionLog {
	
	public Date getDate();
	public Date setDate(Date date);
	public Time getTime();
	public Time setTime(Time time);
	public String getMessage();
	public String setMessage(String message);
}
