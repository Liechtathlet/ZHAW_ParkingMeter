package ch.zhaw.swengineering;

import javax.xml.bind.annotation.*;
import java.util.List;

@XmlRootElement(name = "customers")
@XmlAccessorType(XmlAccessType.FIELD)
public class CustomersConfig {
	@XmlElement(name = "customer")
	public List<Customer> customers;
}
