package net.sunxu.study.c1;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.thymeleaf.spring5.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.extras.springsecurity4.dialect.SpringSecurityDialect;

@SpringBootApplication
@ComponentScan("net.sunxu.study.user")
@ComponentScan("net.sunxu.study.c1")
public class C1Application {
    public static void main(String[] args) {
        SpringApplication.run(C1Application.class, args);
    }

//    @Autowired
//    private SpringResourceTemplateResolver templateResolver;
//
//    @Bean
//    public SpringTemplateEngine templateEngine() {
//        SpringTemplateEngine engine = new SpringTemplateEngine();
//        engine.setTemplateResolver(templateResolver);
//        engine.addDialect(new SpringSecurityDialect());
//        return engine;
//    }
}
