package net.sunxu.study.c5;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.oauth2.resource.ResourceServerProperties;
import org.springframework.boot.autoconfigure.security.oauth2.resource.UserInfoTokenServices;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.filter.OAuth2ClientAuthenticationProcessingFilter;
import org.springframework.security.oauth2.client.filter.OAuth2ClientContextFilter;
import org.springframework.security.oauth2.client.token.grant.code.AuthorizationCodeResourceDetails;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableOAuth2Client;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.web.filter.CompositeFilter;

import javax.servlet.Filter;
import java.util.Arrays;
import java.util.List;

@Configuration
@EnableOAuth2Client
public class SocialConfiguration extends WebSecurityConfigurerAdapter {
    @Autowired
    private OAuth2ClientContext oauth2ClientContext;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // @formatter:off
        http.antMatcher("/**").authorizeRequests().antMatchers("/user", "/login**", "/webjars/**", "/error**").permitAll().anyRequest()
                .authenticated().and().exceptionHandling()
                .authenticationEntryPoint(new LoginUrlAuthenticationEntryPoint("/")).and().logout()
                .logoutSuccessUrl("/").permitAll().and().csrf()
                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()).and()
                .addFilterBefore(ssoFilter(), BasicAuthenticationFilter.class);
        // @formatter:on
    }

    //拦截器中添加多个子拦截器
    private Filter ssoFilter() {
        CompositeFilter filter = new CompositeFilter();
        List<Filter> filters = Arrays.asList(getGithubFilter(), getWeiboFilter());
        filter.setFilters(filters);
        return filter;
    }

    //github 的filter 使用spring security 提供的处理方式
    private Filter getGithubFilter() {
        var githubFilter = new OAuth2ClientAuthenticationProcessingFilter("/login/github");
        ClientResources github = github();

        OAuth2RestTemplate oAuth2Tempalate = new OAuth2RestTemplate(github.getClient(), oauth2ClientContext);
        githubFilter.setRestTemplate(oAuth2Tempalate);

        var tokenServices = new UserInfoTokenServices(github.getResource().getUserInfoUri(),
                github.getClient().getClientId());
        tokenServices.setRestTemplate(oAuth2Tempalate);
        githubFilter.setTokenServices(tokenServices);
        return githubFilter;
    }

    //微博获取用户信息时候需要加上uid 的信息, 所以需要更改一些内容
    private Filter getWeiboFilter() {
        var weiboFilter = new OAuth2ClientAuthenticationProcessingFilter("/login/weibo");
        ClientResources weibo = weibo();

        var oAuth2Template = new WeiboOAuth2RestTemplate(weibo.getClient(), weibo.getResource(), oauth2ClientContext);
        weiboFilter.setRestTemplate(oAuth2Template);

        var tokenService = new UserInfoTokenServices(weibo.getResource().getUserInfoUri(),
                weibo.getClient().getClientId());
        tokenService.setRestTemplate(oAuth2Template);
        weiboFilter.setTokenServices(tokenService);
        return weiboFilter;
    }

    @Bean
    @ConfigurationProperties("github")
    public ClientResources github() {
        return new ClientResources();
    }

    @Bean
    @ConfigurationProperties("weibo")
    public ClientResources weibo() {
        return new ClientResources();
    }

    //设置信息
    class ClientResources {

        @NestedConfigurationProperty
        private AuthorizationCodeResourceDetails client = new AuthorizationCodeResourceDetails();

        @NestedConfigurationProperty
        private ResourceServerProperties resource = new ResourceServerProperties();

        public AuthorizationCodeResourceDetails getClient() {
            return client;
        }

        public ResourceServerProperties getResource() {
            return resource;
        }
    }

    @Bean
    public FilterRegistrationBean<OAuth2ClientContextFilter> oauth2ClientFilterRegistration(
            OAuth2ClientContextFilter filter) {
        var registration = new FilterRegistrationBean<OAuth2ClientContextFilter>();
        registration.setFilter(filter);
        registration.setOrder(-100);
        return registration;
    }
}
