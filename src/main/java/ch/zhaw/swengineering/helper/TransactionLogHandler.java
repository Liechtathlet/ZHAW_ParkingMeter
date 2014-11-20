package ch.zhaw.swengineering.helper;

import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import ch.zhaw.swengineering.model.persistence.TransactionLog;
import ch.zhaw.swengineering.model.persistence.TransactionLogEntry;

/**
 * @author Daniel Brun
 * 
 *         The transaction log handler.
 */
@Component
public class TransactionLogHandler {

    @Autowired
    @Qualifier("transactionLog")
    private ConfigurationProvider configurationProvider;

    @Autowired
    @Qualifier("transactionLog")
    private ConfigurationWriter configurationWriter;

    private TransactionLog transactionLog;

    /**
     * Initialize the class.
     */
    @PostConstruct
    public void init() {
        if (configurationProvider != null
                && configurationProvider.get() != null) {
            transactionLog = (TransactionLog) configurationProvider.get();
        }
    }

    /**
     * Gets the transaction log entries.
     * 
     * @return the transaction log entries.
     */
    public List<TransactionLogEntry> getAll() {

        return transactionLog.entries;
    }

    /**
     * Gets the given number of transaction log entries.
     * 
     * @param numberOfEntries
     *            the number of entries to fetch.
     * @return
     */
    public List<TransactionLogEntry> get(int numberOfEntries) {
        int count = transactionLog.entries.size();

        return transactionLog.entries.subList(count - numberOfEntries, count);
    }

    /**
     * Writes the given text to the transaction log.
     * 
     * @param text
     *            the text to write.
     */
    public void write(String text) {
        if (configurationProvider == null
                || configurationProvider.get() == null
                || configurationWriter == null) {
            return;
        }

        TransactionLogEntry entry = new TransactionLogEntry();
        entry.text = text;
        entry.creationTime = new Date();

        transactionLog.entries.add(entry);

        configurationWriter.write(transactionLog);
    }
}
