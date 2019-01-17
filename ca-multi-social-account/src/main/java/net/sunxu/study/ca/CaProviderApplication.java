package net.sunxu.study.ca;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan({"net.sunxu.study.user", "net.sunxu.study.ca"})
public class CaProviderApplication {
    public static void main(String[] args) {
        SpringApplication.run(CaProviderApplication.class, args);
    }
}
