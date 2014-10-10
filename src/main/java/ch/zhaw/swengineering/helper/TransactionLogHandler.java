package ch.zhaw.swengineering.helper;

import ch.zhaw.swengineering.model.TransactionLog;
import ch.zhaw.swengineering.model.TransactionLogEntry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TransactionLogHandler {

	@Autowired
	@Qualifier("transactionLog")
	private ConfigurationProvider configurationProvider;

	public List<TransactionLogEntry> getAll() {
		if (configurationProvider == null || configurationProvider.get() == null) {
			return null;
		}

		// TODO: Casting should be done only once. But it does not work in the
		// constructor because the poperty will not be injected.
		TransactionLog transactionLog = (TransactionLog) configurationProvider.get();
		return transactionLog.entries;
	}

	public List<TransactionLogEntry> get(int numberOfEntries) {
		if (configurationProvider == null || configurationProvider.get() == null) {
			return null;
		}

		// TODO: Casting should be done only once. But it does not work in the
		// constructor because the poperty will not be injected.
		TransactionLog transactionLog = (TransactionLog) configurationProvider.get();
		int count = transactionLog.entries.size();
		return transactionLog.entries.subList(count - numberOfEntries, count);
	}
}
