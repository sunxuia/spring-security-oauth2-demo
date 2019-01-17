package net.sunxu.study.cb.client3;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.oauth2.client.EnableOAuth2Sso;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Configuration
// 这个注解需要注解在WebSecurityConfigurerAdapter 的配置类上,
// 否则就会使用默认的安全配置类 org.springframework.boot.autoconfigure.security.oauth2.client.OAuth2SsoDefaultConfiguration
@EnableOAuth2Sso
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.formLogin().disable()
                .antMatcher("/**").authorizeRequests()
                .antMatchers("/**").permitAll()
                .and().addFilterBefore(authenticationTokenFilterBean(), UsernamePasswordAuthenticationFilter.class)
                // 不要创建session, 使用STATELESS 策略之后就会忽略jsessionid, 也不会创建jesssionid 了
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and().csrf().disable()
        ;
    }

    @Autowired
    private JwtProperties jwtProperties;

    @Bean
    public JwtAuthenticationTokenFilter authenticationTokenFilterBean() {
        return new JwtAuthenticationTokenFilter();
    }

    /**
     * 自定义实现的JwtAccessTokenConverter, 用于替换
     * org.springframework.boot.autoconfigure.security.oauth2.resource.ResourceServerTokenServicesConfiguration.JwtTokenServicesConfiguration#jwtTokenEnhancer()
     * 中的实现
     *
     * @return
     */
    @Bean
    public JwtAccessTokenConverter jwtTokenEnhancer(ApplicationContext context) {
        JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
        converter.setSigningKey(jwtProperties.getSecret());
        return converter;
    }
}
