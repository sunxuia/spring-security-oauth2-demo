package net.sunxu.study.cb.client.jwt;

import ch.qos.logback.classic.gaffer.PropertyUtil;
import org.springframework.beans.PropertyAccessorUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.oauth2.client.EnableOAuth2Sso;
import org.springframework.boot.autoconfigure.security.oauth2.client.OAuth2SsoDefaultConfiguration;
import org.springframework.boot.autoconfigure.security.oauth2.resource.JwtAccessTokenConverterConfigurer;
import org.springframework.boot.autoconfigure.security.oauth2.resource.JwtAccessTokenConverterRestTemplateCustomizer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Base64;
import java.util.Map;

@Configuration
// 这个注解需要注解在WebSecurityConfigurerAdapter 的配置类上,
// 否则就会使用默认的安全配置类 org.springframework.boot.autoconfigure.security.oauth2.client.OAuth2SsoDefaultConfiguration
@EnableOAuth2Sso
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.formLogin().disable()
                // 除了/ 和/favicon.ico 以外的请求都需要认证. (oauth2 认证的url 不会在这里被拦截, 因为是直接在filter 中实现的)
                .antMatcher("/**").authorizeRequests()
                .antMatchers("/", "/favicon.ico").permitAll()
                .anyRequest().authenticated()
                // 添加jwt 验证的filter
                .and().addFilterBefore(authenticationTokenFilterBean(), UsernamePasswordAuthenticationFilter.class)
                // 客户端仍然需要session 来保存认证用的state.
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
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

    /**
     * OAuth2 认证成功(获得access token并设置环境) 后会发布AuthenticationSuccessEvent 事件.
     * 在这个时候将access token 的值设置到 authorization 中.
     *
     * @param event 事件
     */
    @EventListener
    public void onAuthenticationSuccess(AuthenticationSuccessEvent event) {
        var response = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
        var auth2Authentication = (OAuth2Authentication) event.getAuthentication();
        var details = (OAuth2AuthenticationDetails) auth2Authentication.getDetails();

        response.setHeader(jwtProperties.getTokenHeaderName(), jwtProperties.getTokenPrefix() + details.getTokenValue());
    }
}
