package net.sunxu.study.c1;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.rememberme.InMemoryTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

//spring security 的登录验证
@Configuration
@Order(SecurityProperties.BASIC_AUTH_ORDER)
public class AuthenticationConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    @Qualifier("userDetailService")
    private UserDetailsService userDetailsService;

    //多语言设置
    @Bean
    @Qualifier("basename")
    public String baseName() {
        return "classpath:/messages";
    }


    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
        auth.eraseCredentials(true);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .formLogin()
                .and().authorizeRequests()
                .antMatchers("/login", "/deny", "/images/**", "/js/**", "/css/**", "/fonts/**", "/favicon.ico")
                .permitAll()
                .anyRequest().authenticated()
                .and().exceptionHandling().accessDeniedPage("/deny")
                .and().rememberMe().tokenValiditySeconds(14 * 24 * 60 * 60)
                .tokenRepository(tokenRepository())
                .and().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.NEVER)
        ;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public PersistentTokenRepository tokenRepository() {
        PersistentTokenRepository repository = new InMemoryTokenRepositoryImpl();
        return repository;
    }
}
