package net.sunxu.study.ca;

import org.springframework.boot.autoconfigure.security.oauth2.resource.ResourceServerProperties;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.http.AccessTokenRequiredException;
import org.springframework.security.oauth2.client.resource.OAuth2ProtectedResourceDetails;
import org.springframework.security.oauth2.client.resource.UserRedirectRequiredException;
import org.springframework.security.oauth2.client.token.AccessTokenProvider;
import org.springframework.security.oauth2.client.token.AccessTokenProviderChain;
import org.springframework.security.oauth2.client.token.AccessTokenRequest;
import org.springframework.security.oauth2.client.token.grant.client.ClientCredentialsAccessTokenProvider;
import org.springframework.security.oauth2.client.token.grant.code.AuthorizationCodeAccessTokenProvider;
import org.springframework.security.oauth2.client.token.grant.implicit.ImplicitAccessTokenProvider;
import org.springframework.security.oauth2.client.token.grant.password.ResourceOwnerPasswordAccessTokenProvider;
import org.springframework.security.oauth2.common.OAuth2AccessToken;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;

/**
 * 微博查询用户信息的接口需要在查询参数中传入uid 或者screen_name 的数据, 所以需要做些更改
 */
public class WeiboOAuth2RestTemplate extends OAuth2RestTemplate {
    private ResourceServerProperties userInfoProperties;
    private final OAuth2ProtectedResourceDetails resource;

    public WeiboOAuth2RestTemplate(OAuth2ProtectedResourceDetails resource,
                                   ResourceServerProperties resourceConfig,
                                   OAuth2ClientContext context) {
        super(resource, context);
        this.resource = resource;
        this.userInfoProperties = resourceConfig;
        super.setAccessTokenProvider(accessTokenProvider);
    }

    /**
     * 添加了这些代码以获取 access token
     * 和父类不同的是取消了从context 中获取access_token 而是重新请求
     *
     * @return
     * @throws UserRedirectRequiredException
     */
    @Override
    public OAuth2AccessToken getAccessToken() throws UserRedirectRequiredException {
        OAuth2ClientContext context = getOAuth2ClientContext();
        try {
            OAuth2AccessToken accessToken = acquireAccessToken(context);
            return accessToken;
        } catch (UserRedirectRequiredException e) {
            context.setAccessToken(null); // No point hanging onto it now
            String stateKey = e.getStateKey();
            if (stateKey != null) {
                Object stateToPreserve = e.getStateToPreserve();
                if (stateToPreserve == null) {
                    stateToPreserve = "NONE";
                }
                context.setPreservedState(stateKey, stateToPreserve);
            }
            throw e;
        }
    }

    @Override
    protected OAuth2AccessToken acquireAccessToken(OAuth2ClientContext oauth2Context) throws UserRedirectRequiredException {
        AccessTokenRequest accessTokenRequest = oauth2Context.getAccessTokenRequest();
        // Transfer the preserved state from the (longer lived) context to the current request.
        String stateKey = accessTokenRequest.getStateKey();
        if (stateKey != null) {
            accessTokenRequest.setPreservedState(oauth2Context.removePreservedState(stateKey));
        }

        OAuth2AccessToken accessToken = null;
        accessToken = accessTokenProvider.obtainAccessToken(resource, accessTokenRequest);
        if (accessToken == null || accessToken.getValue() == null) {
            throw new IllegalStateException(
                    "Access token provider returned a null access token, which is illegal according to the contract.");
        }
        oauth2Context.setAccessToken(accessToken);
        return accessToken;
    }

    @Override
    public void setAccessTokenProvider(AccessTokenProvider accessTokenProvider) {
        this.accessTokenProvider = accessTokenProvider;
        super.setAccessTokenProvider(accessTokenProvider);
    }

    private AccessTokenProvider accessTokenProvider = new AccessTokenProviderChain(Arrays.<AccessTokenProvider>asList(
            new AuthorizationCodeAccessTokenProvider(), new ImplicitAccessTokenProvider(),
            new ResourceOwnerPasswordAccessTokenProvider(), new ClientCredentialsAccessTokenProvider()));

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
