package net.sunxu.study.c8;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan({"net.sunxu.study.user", "net.sunxu.study.c8"})
public class C8ProviderApplication {
    public static void main(String[] args) {
        SpringApplication.run(C8ProviderApplication.class, args);
    }
}
