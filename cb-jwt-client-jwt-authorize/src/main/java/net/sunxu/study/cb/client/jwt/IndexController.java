package net.sunxu.study.cb.client.jwt;

import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
public class IndexController {

    @RequestMapping("/")
    public Object index(Principal principal) {
        return "principal is : " + principal;
    }

    @RequestMapping("/authenticated")
    public Object authenticated() {
        return "authenticated";
    }
}
