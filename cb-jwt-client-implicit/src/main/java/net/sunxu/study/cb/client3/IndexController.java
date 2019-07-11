package net.sunxu.study.cb.client3;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

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

    /**
     * 获得客户端认证的相关信息. 浏览器根据这些信息拼接出访问认证服务器的url.
     *
     * @return
     */
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

    /**
     * 不用跳转, 允许访问认证服务器获取认证信息, 然后后台自动登录
     *
     * @return
     */
    @RequestMapping("/no-jump")
    public String noJump() {
        return "no-jump";
    }

    /**
     * 访问noJump 之后会访问认证服务器, 向认证服务器传递的跳转地址就是这里.
     *
     * @return
     */
    @RequestMapping("/no-jump-iframe")
    public String noJumpIframe() {
        return "no-jump-iframe";
    }
}
