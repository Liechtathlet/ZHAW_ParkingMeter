package ch.zhaw.swengineering.model.persistence;

import java.util.Hashtable;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Daniel Brun
 * 
 *         Class to store all available secret codes and their corresponding
 *         action.
 */
@XmlRootElement(name = "secretCodes")
@XmlAccessorType(XmlAccessType.FIELD)
public class SecretCodes {
    @XmlElement
    private Map<Integer, SecretActionEnum> codeMapping;

    /**
     * Creates a new instance of this class.
     */
    public SecretCodes() {
        codeMapping = new Hashtable<Integer, SecretActionEnum>();
    }
    
    /**
     * Creates a new instance of this class.
     * 
     * @param aCodeMapping the code mapping.
     */
    public SecretCodes(final Map<Integer, SecretActionEnum> aCodeMapping) {
        super();
        this.codeMapping = aCodeMapping;
    }

    /**
     * @return the secret codes.
     */
    public final Map<Integer, SecretActionEnum> getCodeMapping() {
        return new Hashtable<Integer, SecretActionEnum>(codeMapping);
    }
 
}
