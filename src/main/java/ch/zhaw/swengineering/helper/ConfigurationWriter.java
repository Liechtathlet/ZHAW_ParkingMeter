package ch.zhaw.swengineering.helper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import java.io.File;
import java.util.logging.Logger;

@Component
public class ConfigurationWriter {
	private Logger logger = Logger.getLogger(ConfigurationWriter.class.toString());

	protected final String className;
	protected final String xml;

	@Autowired
	protected ConfigurationWriter(String xml, String className) {
		this.xml = xml;
		this.className = className;
	}

	public void write(Object content) {
		File f = new File(xml);

		// create JAXB context and instantiate marshaller
		JAXBContext context;
		try {
			context = JAXBContext.newInstance(Class.forName(className));
			context.createMarshaller().marshal(content, f);
		} catch (JAXBException e) {
			logger.warning(String.format("Could not serialize class [%s] to file %s. Error: %s", className, xml, e.getMessage()));
		} catch (ClassNotFoundException e) {
			logger.warning(String.format("Could not find class %s. Error: %s", className, e.getMessage()));
		} catch (Exception e) {
			logger.warning(String.format("Exception caught serializing class [%s] to xml %s. Error: %s", className, xml, e.getMessage()));
		}
	}
}
