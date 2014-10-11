package ch.zhaw.swengineering.helper;

import ch.zhaw.swengineering.model.TransactionLog;
import ch.zhaw.swengineering.model.TransactionLogEntry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
public class TransactionLogHandler {

	@Autowired
	@Qualifier("transactionLog")
	private ConfigurationProvider configurationProvider;

	@Autowired
	@Qualifier("transactionLog")
	private ConfigurationWriter configurationWriter;

	public List<TransactionLogEntry> getAll() {
		if (configurationProvider == null || configurationProvider.get() == null) {
			return null;
		}

		// TODO: Casting should be done only once. But it does not work in the
		// constructor because the poperty will not be injected.
		// TODO: db: Create init method with @PostContruct ;-)
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

	public void write(String text) {
		if (configurationProvider == null || configurationProvider.get() == null
				|| configurationWriter == null) {
			return;
		}

		TransactionLogEntry entry = new TransactionLogEntry();
		entry.text = text;
		entry.creationTime = new Date();

		TransactionLog transactionLog = (TransactionLog) configurationProvider.get();
		transactionLog.entries.add(entry);

		configurationWriter.write(transactionLog);
	}
}
