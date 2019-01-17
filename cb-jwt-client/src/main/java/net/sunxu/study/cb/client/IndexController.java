package net.sunxu.study.cb.client;

import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
public class IndexController {

    @RequestMapping("/")
    public Object index(Principal principal) {
        var auth2Authentication = (OAuth2Authentication) principal;
        var details = (OAuth2AuthenticationDetails) auth2Authentication.getDetails();
        return details.getTokenValue();
    }
}
