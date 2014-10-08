package ch.zhaw.swengineering.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement(name = "secretCodes")
@XmlAccessorType(XmlAccessType.FIELD)
public class SecretCodes {
	@XmlElement(name = "secretCode")
	public List<SecretCode> secretCodes;
}
