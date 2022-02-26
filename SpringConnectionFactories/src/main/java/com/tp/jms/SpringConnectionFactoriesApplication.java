package com.tp.jms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;


@SpringBootApplication
public class SpringConnectionFactoriesApplication extends SpringBootServletInitializer  {
	
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(SpringConnectionFactoriesApplication.class);
    }


	public static void main(String[] args) {
		SpringApplication.run(SpringConnectionFactoriesApplication.class, args);
	}

}
