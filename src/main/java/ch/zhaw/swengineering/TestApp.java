package ch.zhaw.swengineering;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan
@EnableAutoConfiguration
public class TestApp {
    public static void main(String[] args) {
	    ConfigurableApplicationContext context = SpringApplication.run(TestApp.class, args);
	    RunLogAndXmlService(context);
    }

	private static void RunLogAndXmlService(ConfigurableApplicationContext context) {
		//org.springframework.context.ApplicationContext context = new AnnotationConfigApplicationContext(TestApp.class);
		LogAndXmlService service = context.getBean(LogAndXmlService.class);
		System.out.println(service.run());
	}
}
