package net.sunxu.study.cb.client3;

import net.sunxu.study.util.ResponseLoggingFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;
import org.springframework.http.HttpMethod;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.regex.Pattern;

@SpringBootApplication
public class CbClient3Application {
    public static void main(String[] args) {
        SpringApplication.run(CbClient3Application.class, args);
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
     * cors 设置, 在使用xhr 的时候认证服务返回重定向到这里的响应, 浏览器以此发起的请求的Origin 是"null" 因此需要对此进行处理.
     *
     * @return
     */
    @Bean
    public FilterRegistrationBean registerCorsFilter() {
        var config = new CorsConfiguration();
        config.addAllowedOrigin("null");
        config.addAllowedMethod(HttpMethod.GET);
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource configurationSource = new UrlBasedCorsConfigurationSource();
        configurationSource.registerCorsConfiguration("/no-jump", config);
        CorsFilter corsFilter = new CorsFilter(configurationSource);

        FilterRegistrationBean registration = new FilterRegistrationBean(corsFilter);
        registration.setOrder(Ordered.HIGHEST_PRECEDENCE + 1);
        registration.setName("cors filter");
        return registration;
    }
}
