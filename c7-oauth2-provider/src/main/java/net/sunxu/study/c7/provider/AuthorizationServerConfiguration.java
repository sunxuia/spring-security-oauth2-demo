package net.sunxu.study.c7.provider;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.*;
import org.springframework.security.oauth2.provider.client.ClientCredentialsTokenGranter;
import org.springframework.security.oauth2.provider.code.AuthorizationCodeServices;
import org.springframework.security.oauth2.provider.code.AuthorizationCodeTokenGranter;
import org.springframework.security.oauth2.provider.endpoint.AuthorizationEndpoint;
import org.springframework.security.oauth2.provider.endpoint.FrameworkEndpointHandlerMapping;
import org.springframework.security.oauth2.provider.implicit.ImplicitTokenGranter;
import org.springframework.security.oauth2.provider.password.ResourceOwnerPasswordTokenGranter;
import org.springframework.security.oauth2.provider.refresh.RefreshTokenGranter;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.security.oauth2.provider.token.store.KeyStoreKeyFactory;

import javax.annotation.PostConstruct;
import java.security.KeyPair;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 认证服务器的配置信息.
 */
@Configuration
@EnableAuthorizationServer
public class AuthorizationServerConfiguration extends AuthorizationServerConfigurerAdapter {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    @Qualifier("userDetailService")
    private UserDetailsService userDetailsService;

    // 客户端的连接信息
    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients.inMemory()
                //在这里定义一个oauth 客户端, id 是ssocilent-1, 密钥是ssosecret, 自动授权, 授权方式, scope 等
                .withClient("ssoclient-1")
                .secret(PasswordEncoderFactories.createDelegatingPasswordEncoder().encode("ssosecret"))
//                .secret("{bcrypt}$2a$10$OVTOAJDSEfiCMVIkOb.0/efSXcS43hJKusQmnLjxWOPKhgukWfAUi")
                .autoApprove(true)
                // 允许的授权方式
                .authorizedGrantTypes(
                        "authorization_code",  // 授权码模式
                        "client_credentials", // 客户端模式(这个可以直接发送restful 请求来获得token)
                        "password", // 密码模式
                        "refresh_token", // refresh token
                        "implicit" // 简化模式
                )
                // scope
                .scopes("openid");
    }

    // springSecurity 授权表达式, 访问tokenkey 时需要经过认证
    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
        security.tokenKeyAccess("permitAll()")
                .checkTokenAccess("isAuthenticated()")
                .allowFormAuthenticationForClients();
    }

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        // 设置access token 的格式
        endpoints
                // 设置accessTokenConverter 为JwtAccessTokenConverter  就会将设置 tokenStore 默认设置为 JwtTokenStore.
                // 其它情况默认是 JwtTokenStore. 所以如下2 行的语句效果等同.
//                .tokenStore(new JwtTokenStore(jwtAccessTokenConverter()))
                // (使用默认的方式就是返回一串hash 值.)
                .accessTokenConverter(jwtAccessTokenConverter())
        //  设置了 token enhancer 就会忽略 access token converter 的设置, 所以这两个值不能同时设置.
//                .tokenEnhancer(tokenEnhancer())
        ;

        // password 认证需要设置. 必须注入 authenticationManager 才会默认添加
        // 密码模式授权的granter ResourceOwnerPasswordTokenGranter.
        // (在org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer.getDefaultTokenGranters)
        endpoints.authenticationManager(authenticationManager);

        // refresh token 认证需要设置. 否则在认证过程中会出现异常.
        endpoints.userDetailsService(userDetailsService);
    }

    // 配置签名方式. 这个会将accessToken中的access token 和refresh token 使用指定的密钥和accessToken 对象的内容转换为jwt,
    // jwt 的payload 中的jti 就是这两个token 原来的值.
    @Bean
    public JwtAccessTokenConverter jwtAccessTokenConverter() {
        // 重载enhance 方法, 添加自定义的数据到access token 和refresh token 中.
        JwtAccessTokenConverter converter = new JwtAccessTokenConverter() {
            @Override
            public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
                Map<String, Object> additionalInfo = new HashMap<>();
                additionalInfo.put("local-user-name", authentication.getName());
                ((DefaultOAuth2AccessToken) accessToken).setAdditionalInformation(additionalInfo);
                // 父类方法返回的对象不是accessToken
                return super.enhance(accessToken, authentication);
            }
        };
        // RSA 加密, 返回给客户端的是公钥
        KeyPair keyPair = new KeyStoreKeyFactory(new ClassPathResource("key.keystore"), "123456".toCharArray())
                .getKeyPair("key");
        converter.setKeyPair(keyPair);
        return converter;
    }

    // 配置额外信息. 这个会在里面添加额外的内容.
    @Bean
    public TokenEnhancer tokenEnhancer() {
        return (accessToken, authentication) -> {
            Map<String, Object> additionalInfo = new HashMap<>();
            additionalInfo.put("local-user-name", authentication.getName());
            ((DefaultOAuth2AccessToken) accessToken).setAdditionalInformation(additionalInfo);
            return accessToken;
        };
    }
}
