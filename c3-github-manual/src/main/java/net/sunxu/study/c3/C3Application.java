package net.sunxu.study.c3;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.oauth2.client.EnableOAuth2Sso;

@SpringBootApplication
public class C3Application {
    public static void main(String[] args) {
        SpringApplication.run(C3Application.class, args);
    }
}
