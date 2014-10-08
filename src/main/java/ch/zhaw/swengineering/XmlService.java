package ch.zhaw.swengineering;

import java.io.File;
import java.util.logging.Logger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.springframework.stereotype.Component;

@Component
public class XmlService {
	Logger logger = Logger.getLogger(LogAndXmlService.class.toString());

    public String run() {
	    Log();
	    xmlService();
	    
        return "Hello world!";
    }

	private void xmlService() {
		File f = new File("./logs/transactionLog.xml");

		// create JAXB context and instantiate marshaller
		JAXBContext context = null;
		TransactionLogImpl transactionLog = null;
		try {
			context = JAXBContext.newInstance(TransactionLogImpl.class);
			Unmarshaller m = context.createUnmarshaller();
			transactionLog = (TransactionLogImpl)m.unmarshal(f);
		} catch (JAXBException e) {
			logger.info(String.format("Something went wrong during unmarshalling the transaction log. Error: %s", e.getMessage()));
		}

		logger.info("transactionLog.xml data:");
		for(TransactionLogImpl transactionLogEntry : transactionLog.transactionLogEntries) {
			logger.info(String.format("- %s %s", transactionLogEntry.date, transactionLogEntry.time, transactionLogEntry.message));
		}
	}

	private void Log() {
		logger.info("Test log entry!");
	}
}
