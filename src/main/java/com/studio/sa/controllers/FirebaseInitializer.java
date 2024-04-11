package com.studio.sa.controllers;

import java.io.FileInputStream;
import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.core.io.Resource;

@Component
public class FirebaseInitializer {
	
	@Autowired
	private static ResourceLoader resourceLoader;
	
	 @Value("classpath:solid-silicon-254807-b54d56e6929b.json")
	 private static Resource resource;
	 
    public static void initialize() throws IOException {
        
        FirebaseOptions options = new FirebaseOptions.Builder()
            .setCredentials(GoogleCredentials.fromStream( new FileInputStream(ResourceUtils.getFile("classpath:garmenta.json"))))
            .build();

        FirebaseApp.initializeApp(options);
    }
}

