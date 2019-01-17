package net.sunxu.study.cb.client.jwt;

import net.sunxu.study.util.ResponseLoggingFilter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.Ordered;
import org.springframework.security.web.context.AbstractSecurityWebApplicationInitializer;

import javax.servlet.Filter;

// @EnableOAuth2Sso 注解移动到了SecurityConfig 类上.
@SpringBootApplication
public class CbClientJwtApplication {
    public static void main(String[] args) {
        SpringApplication.run(CbClientJwtApplication.class, args);
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
