package ch.zhaw.swengineering;

import org.springframework.stereotype.Component;

import ch.zhaw.swengineering.model.Customer;
import ch.zhaw.swengineering.model.CustomersConfig;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

@Component
public class LogAndXmlService {
	Logger logger = Logger.getLogger(LogAndXmlService.class.toString());

    public String run() {
	    Log();
	    XmlConfig();
	    
        return "Hello world!";
    }

	private void XmlConfig() {
		File f = new File("./src/main/config/customers.xml");

		// create JAXB context and instantiate marshaller
		JAXBContext context = null;
		CustomersConfig config = null;
		try {
			context = JAXBContext.newInstance(CustomersConfig.class);
			Unmarshaller m = context.createUnmarshaller();
			config = (CustomersConfig)m.unmarshal(f);
		} catch (JAXBException e) {
			logger.info(String.format("Something went wrong during unmarshaling the example config. Error: %s", e.getMessage()));
		}

		logger.info("customers.xml data:");
		for(Customer customer : config.customers) {
			logger.info(String.format("- %s %s", customer.firstname, customer.name));
		}
	}

	private void Log() {
		logger.info("Test log entry!");
	}
}
