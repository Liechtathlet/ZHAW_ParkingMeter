package ch.zhaw.swengineering;

import java.sql.Time;
import java.util.Date;

public class TransactionLogImpl implements TransactionLog {
	public Date date;
	public Time time;
	public String message;
	private TransactionLogImpl transactionLog;
	public TransactionLogImpl[] transactionLogEntries;
	
	public TransactionLogImpl(Date date, Time time, String message) {
		transactionLog = new TransactionLogImpl(date, time, message);
	}

	@Override
	public Date getDate() {
		return date;
	}

	@Override
	public Date setDate(Date date) {
		return date;
	}

	@Override
	public Time getTime() {
		return time;
	}

	@Override
	public Time setTime(Time time) {
		return null;
	}

	@Override
	public String getMessage() {
		return message;
	}

	@Override
	public String setMessage(String message) {
		return message;
	}

	public TransactionLogImpl getTransactionLog() {
		return transactionLog;
	}

	public void setTransactionLog(TransactionLogImpl transactionLog) {
		this.transactionLog = transactionLog;
	}
}
