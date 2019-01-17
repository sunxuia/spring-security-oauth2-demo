package net.sunxu.study.cb.client3;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.util.Base64;
import java.util.Map;

@Data
@Configuration
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {

    private String tokenHeaderName;

    private String tokenPrefix;

    private String clientId;

    private String clientSecret;

    private String keyUri;

    private Integer expiration;

    private String alg;

    private String secret;

    private String userAuthorizationUri;

    // 从认证服务器获得加密算法和密钥
    @PostConstruct
    public void init() {
        RestTemplate keyUriRestTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        byte[] token = Base64.getEncoder()
                .encode((clientId + ":" + clientSecret).getBytes());
        headers.add("Authorization", "Basic " + new String(token));
        HttpEntity<Void> request = new HttpEntity<>(headers);
        var ret = keyUriRestTemplate
                .exchange(keyUri, HttpMethod.GET, request, Map.class).getBody();
        alg = (String) ret.get("alg");
        secret = (String) ret.get("value");
        // 密码要进行base64 编码之后使用
        secret = new String(Base64.getEncoder().encode(secret.getBytes()));
    }
}
