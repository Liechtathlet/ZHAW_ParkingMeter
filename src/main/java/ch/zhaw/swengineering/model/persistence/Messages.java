package ch.zhaw.swengineering.model.persistence;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement(name = "messages")
@XmlAccessorType(XmlAccessType.FIELD)
public class Messages {
	@XmlElement(name = "message")
	public List<Message> messages;
}
