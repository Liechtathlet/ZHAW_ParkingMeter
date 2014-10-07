package ch.zhaw.swengineering;

import java.io.File;
import java.util.logging.Logger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.springframework.stereotype.Component;

@Component
public class LogAndXmlService {
	Logger logger = Logger.getLogger(LogAndXmlService.class.toString());

    public String run() {
        return "Hello world!";
    }
}
