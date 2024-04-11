package com.studio.sa;

import java.io.IOException;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.studio.sa.controllers.FirebaseInitializer;

@SpringBootApplication
public class SsaApplication {

	public static void main(String[] args) {
		
		SpringApplication.run(SsaApplication.class, args);
		try {
			FirebaseInitializer.initialize();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
