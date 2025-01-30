package com.projetjee.gc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaRepositories("com.projetjee.gc.dao")
@SpringBootApplication
public class GestionCommercielleApplication {

	public static void main(String[] args) {
		SpringApplication.run(GestionCommercielleApplication.class, args);
	}

}
