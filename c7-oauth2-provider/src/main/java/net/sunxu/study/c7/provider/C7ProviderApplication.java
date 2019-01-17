package net.sunxu.study.c7.provider;

import net.sunxu.study.util.ResponseLoggingFilter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.Ordered;

@SpringBootApplication
@ComponentScan({"net.sunxu.study.c7.provider", "net.sunxu.study.user"})
public class C7ProviderApplication {
    public static void main(String[] args) {
        SpringApplication.run(C7ProviderApplication.class, args);
    }

    @Bean
    public FilterRegistrationBean registerLoggingFilter() {
        FilterRegistrationBean registration = new FilterRegistrationBean(loggingFilter());
        registration.setOrder(Ordered.HIGHEST_PRECEDENCE);
        registration.setName("logging filter");
        return registration;
    }

    @Bean
    public ResponseLoggingFilter loggingFilter() {
        return new ResponseLoggingFilter();
    }
}
