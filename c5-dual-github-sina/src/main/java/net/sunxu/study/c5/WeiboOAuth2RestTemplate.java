package net.sunxu.study.c5;

import org.springframework.boot.autoconfigure.security.oauth2.resource.ResourceServerProperties;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.resource.OAuth2ProtectedResourceDetails;
import org.springframework.security.oauth2.common.OAuth2AccessToken;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * 微博查询用户信息的接口需要在查询参数中传入uid 或者screen_name 的数据, 所以需要做些更改
 */
public class WeiboOAuth2RestTemplate extends OAuth2RestTemplate {
    private ResourceServerProperties userInfoProperties;

    public WeiboOAuth2RestTemplate(OAuth2ProtectedResourceDetails resource,
                                   ResourceServerProperties resourceConfig,
                                   OAuth2ClientContext context) {
        super(resource, context);
        this.userInfoProperties = resourceConfig;
    }

    @Override
    protected ClientHttpRequest createRequest(URI uri, HttpMethod method) throws IOException {
        if (uri.toString().equals(userInfoProperties.getUserInfoUri())) {
            var accessToken = getAccessToken();
            String newURI = uri.toString() + "?access_token=" + accessToken.getValue() +
                    "&uid=" + accessToken.getAdditionalInformation().get("uid");
            try {
                uri = new URI(newURI);
            } catch (URISyntaxException e) {
                e.printStackTrace();
                throw new IllegalArgumentException("Could not parse URI", e);
            }
        }
        return super.createRequest(uri, method);
    }
}
