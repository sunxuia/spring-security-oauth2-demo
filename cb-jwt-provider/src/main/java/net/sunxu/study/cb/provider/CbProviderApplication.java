package net.sunxu.study.cb.provider;

import net.sunxu.study.util.ResponseLoggingFilter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.Ordered;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.security.Principal;

@SpringBootApplication
@ComponentScan({"net.sunxu.study", "net.sunxu.study.user"})
public class CbProviderApplication {
    public static void main(String[] args) {
        SpringApplication.run(CbProviderApplication.class, args);
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
