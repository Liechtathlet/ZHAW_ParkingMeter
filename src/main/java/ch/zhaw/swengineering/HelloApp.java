package ch.zhaw.swengineering;

import ch.zhaw.swengineering.examle.HelloService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan
public class HelloApp {
    public static void main(String[] args) {
	    ApplicationContext context = new AnnotationConfigApplicationContext(HelloApp.class);
        HelloService helloService = context.getBean(HelloService.class);
        System.out.println(helloService.sayHello());
    }
}
