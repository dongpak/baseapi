/**
 * 
 */
package com.churchclerk.demoapi;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * 
 * @author dongp
 *
 */
@ComponentScan({"com.churchclerk"})
@SpringBootApplication
public class DemoApiApplication {

	/**
	 *
	 * @param args
	 */
	public static void main(String[] args) {
		SpringApplication.run(DemoApiApplication.class, args);
	}

}
