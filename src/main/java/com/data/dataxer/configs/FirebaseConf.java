package com.data.dataxer.configs;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;

@Configuration
public class FirebaseConf {

    @Bean
    public FirebaseAuth firebaseAuth() throws IOException {
        ClassPathResource classPathResource = new ClassPathResource("dataxer-dev-firebase.json");

        FirebaseOptions options = new FirebaseOptions.Builder()
                .setCredentials(GoogleCredentials.fromStream(classPathResource.getInputStream()))
                .setDatabaseUrl("https://poto-dev.firebaseio.com").build();

        FirebaseApp.initializeApp(options);

        return FirebaseAuth.getInstance();
    }
}
