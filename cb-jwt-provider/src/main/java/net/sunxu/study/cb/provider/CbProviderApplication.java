package net.sunxu.study.cb.provider;

import net.sunxu.study.util.ResponseLoggingFilter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

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

    /**
     * cors 设置, 如果使用xhr 访问的话会产生跨域问题.
     *
     * @return
     */
    @Bean
    public FilterRegistrationBean registerCorsFilter() {
        var config = new CorsConfiguration();
        config.addAllowedOrigin("*");
        config.addAllowedMethod(HttpMethod.GET);
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource configurationSource = new UrlBasedCorsConfigurationSource();
        configurationSource.registerCorsConfiguration("/login", config);
        configurationSource.registerCorsConfiguration("/oauth/**", config);
        CorsFilter corsFilter = new CorsFilter(configurationSource);

        FilterRegistrationBean registration = new FilterRegistrationBean(corsFilter);
        registration.setOrder(Ordered.HIGHEST_PRECEDENCE + 1);
        registration.setName("cors filter");
        return registration;
    }
}
