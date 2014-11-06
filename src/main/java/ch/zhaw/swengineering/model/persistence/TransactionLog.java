package ch.zhaw.swengineering.model.persistence;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement(name = "transactionLog")
@XmlAccessorType(XmlAccessType.FIELD)
public class TransactionLog {
	@XmlElement(name = "entry")
	public List<TransactionLogEntry> entries;
}
