package ch.zhaw.swengineering.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

@XmlAccessorType(XmlAccessType.FIELD)
public class SecretCode {

	@XmlAttribute
	public int number;
	
	/**
	 * Empty constructor for serialization
	 */
	public SecretCode() {
		// Empty constructor for serialization
	}

	/**
	 * Creates a new instance of this class.
	 * 
	 * @param number The secret code.
	 */
	public SecretCode(int number) {
		this.number = number;
	}
	
}
