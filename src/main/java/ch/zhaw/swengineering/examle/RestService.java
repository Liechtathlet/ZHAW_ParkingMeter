package ch.zhaw.swengineering.examle;

import java.io.IOException;
import java.util.logging.Logger;
import org.springframework.stereotype.Component;

import org.glassfish.grizzly.http.server.HttpServer;
import com.sun.jersey.api.container.grizzly2.GrizzlyServerFactory;
import com.sun.jersey.api.core.PackagesResourceConfig;
import com.sun.jersey.api.core.ResourceConfig;

@Component
public class RestService {
	Logger logger = Logger.getLogger("ch.zhawswengineering.example.RestService");

    public void run() {
	    runServer();
    }

	private void runServer() {
		// HttpServer server = HttpServer.createSimpleServer();
		// create jersey-grizzly server
		ResourceConfig rc = new PackagesResourceConfig("my.resources");
		HttpServer server = null;

		try {
			server = GrizzlyServerFactory.createHttpServer(
					"http://localhost:8080", rc);
		} catch (IOException e) {
			logger.info(String.format("Error starting REST server. Message: %s", e.getMessage()));
			return;
		}

		try {
			server.start();
			System.out.println("Press any key to stop the server...");
			System.in.read();
		} catch (Exception e) {
			System.err.println(e);
		}
	}
}
