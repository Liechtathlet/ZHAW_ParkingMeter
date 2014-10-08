package ch.zhaw.swengineering.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

@XmlAccessorType(XmlAccessType.FIELD)
public class SecretCode {
	@XmlAttribute
	public String code;
	@XmlAttribute
	public String action;
}
