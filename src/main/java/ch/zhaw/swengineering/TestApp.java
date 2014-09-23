package ch.zhaw.swengineering;

import ch.zhaw.swengineering.examle.LogAndXmlService;
import ch.zhaw.swengineering.examle.RestService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan
public class TestApp {
    public static void main(String[] args) {
	    org.springframework.context.ApplicationContext context = new AnnotationConfigApplicationContext(TestApp.class);
	    RunLogAndXmlService(context);
	    //RunRestService(context);
    }

	private static void RunLogAndXmlService(ApplicationContext context) {
		LogAndXmlService service = context.getBean(LogAndXmlService.class);
		System.out.println(service.run());
	}

	private static void RunRestService(ApplicationContext context) {
		RestService service = context.getBean(RestService.class);
		service.run();
	}
}
