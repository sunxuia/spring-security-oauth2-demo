package net.sunxu.study.cb.provider;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan({"net.sunxu.study", "net.sunxu.study.user"})
public class CbProviderApplication {
    public static void main(String[] args) {
        SpringApplication.run(CbProviderApplication.class, args);
    }
}
