package edu.poly.thtechnology;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import edu.poly.thtechnology.config.StorageProperties;
import edu.poly.thtechnology.service.StorageService;


@SpringBootApplication
@EnableConfigurationProperties(StorageProperties.class)
public class ThTechnologyApplication {

	public static void main(String[] args) {
		SpringApplication.run(ThTechnologyApplication.class, args);
	}
	
	@Bean
	CommandLineRunner init(StorageService storageService) {
		return (args->{
			storageService.init();
		});
	}

}
