package com.marcuschiu.example.spring.boot.mastercodesnippet;

import com.marcuschiu.example.spring.boot.mastercodesnippet.configuration.Configuration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Collections;

/**
 * Created by marcus.chiu on 10/1/16.
 * @SpringBootApplication - a convenience annotation that adds all the following:
 *     1. @Configuration - tags the class as a source of bean definitions
 *     2. @EnableAutoConfiguration - tells Spring Boot to start adding beans
 *          based on classpath settings, other beans, and various property settings
 *     3. @EnableWebMvc - normally added for a Spring MVC app, but Spring boot adds
 *          it automatically when it sees 'spring-webmvc' on the classpath.
 *          This flags application as a web application and activates key behaviors
 *          like setting up DispatcherServlet
 *     4. @ComponentScan - tells Spring to look for other components, configurations,
 *          and services in the package this class belongs to, allowing it to find the controllers
 */
@SpringBootApplication
public class MasterCodeSnippetApplication {

	public static void main(String[] args) throws FileNotFoundException {
		File file = ResourceUtils.getFile("classpath:configuration.txt");
		Configuration configuration = new Configuration(file);

		for(String arg:args) {
			System.out.println(arg);
		}

		SpringApplication app = new SpringApplication(MasterCodeSnippetApplication.class);
		app.setDefaultProperties(Collections
				.singletonMap("server.port", "8083"));
		app.run(args);
	}
}
