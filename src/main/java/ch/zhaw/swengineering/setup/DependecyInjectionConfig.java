package ch.zhaw.swengineering.setup;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.BufferedReader;
import java.io.InputStreamReader;

@Configuration
public class DependecyInjectionConfig {

	@Bean
	public BufferedReader bufferedReader() {
		return new BufferedReader(new InputStreamReader(System.in));
	}
}
