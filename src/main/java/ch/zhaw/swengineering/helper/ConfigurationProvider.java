package ch.zhaw.swengineering.helper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import java.io.File;
import java.util.logging.Logger;

@Component
public class ConfigurationProvider {

	private Logger logger = Logger.getLogger(ConfigurationProvider.class.toString());

	protected final Object content;
	protected final String className;
	protected final String xml;

	@Autowired
	protected ConfigurationProvider(String xml, String className) {
		this.xml = xml;
		this.className = className;

		content = load();
	}

	public Object get() {
		return content;
	}

	private Object load() {
		File f = new File(xml);

		// create JAXB context and instantiate marshaller
		JAXBContext context = null;
		try {
			context = JAXBContext.newInstance(Class.forName(className));
			return context.createUnmarshaller().unmarshal(f);
		} catch (JAXBException e) {
			logger.warning(String.format("Could not deserialize xml [%s] to class %s. Error: %s", xml, className, e.getMessage()));
		} catch (ClassNotFoundException e) {
			logger.warning(String.format("Could not find class %s. Error: %s", className, e.getMessage()));
		}

		return null;
	}
}
