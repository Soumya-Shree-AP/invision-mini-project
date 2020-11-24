package com.miniProject;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import com.miniProject.configuration.FileStoragePojo;

@SpringBootApplication
@EnableConfigurationProperties({
    FileStoragePojo.class
})
public class SpringBootSecurityJwtApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringBootSecurityJwtApplication.class, args);
	}

}
