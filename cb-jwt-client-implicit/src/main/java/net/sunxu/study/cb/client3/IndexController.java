package net.sunxu.study.cb.client3;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

@Controller
public class IndexController {

    @Autowired
    private JwtProperties jwtProperties;

    @RequestMapping("/")
    public String index() {
        return "index";
    }

    @RequestMapping("/authorization-info")
    @ResponseBody
    public Object view() {
        Map<String, String> res = new HashMap<>(4);
        res.put("clientId", jwtProperties.getClientId());
        res.put("tokenHeaderName", jwtProperties.getTokenHeaderName());
        res.put("tokenPrefix", jwtProperties.getTokenPrefix());
        res.put("authorizationUri", jwtProperties.getUserAuthorizationUri());
        return res;
    }

    @RequestMapping("/principal")
    @ResponseBody
    public Object principal(Principal principal) {
        return principal;
    }
}
