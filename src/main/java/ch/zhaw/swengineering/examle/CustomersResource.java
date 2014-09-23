package ch.zhaw.swengineering.examle;


import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Path("/customers")
public class CustomersResource {

	@GET
	@Produces("text/xml")
	@Path("all")
	public String getCustomers() {
		return "<customers><customer><name>asdf</name><firstname>fdsa</firstname></customer></customers>";
	}
}
