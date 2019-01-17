package net.sunxu.study.c7.provider;

import net.sunxu.study.user.CustomUserDetails;
import net.sunxu.study.user.UserModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
public class UserController {
    @Autowired
    @Qualifier("userDetailService")
    private UserDetailsService userDetailsService;

    /**
     * 客户端将通过这个接口来获取用户信息
     * 参考端口获取方式 org.springframework.boot.autoconfigure.security.oauth2.resource.UserInfoTokenServices#getMap(java.lang.String, java.lang.String)
     * 可以得知返回的内容需要是一个可被解析为Map 的类型 (restTemplate.getForEntity(path, Map.class).getBody())
     * @param principal
     * @return
     */
    @RequestMapping({"/user", "/me"})
    public UserModel user(Principal principal) {
        UserModel userModel = ((CustomUserDetails) userDetailsService.loadUserByUsername(principal.getName())).getUser();
        return userModel;
    }
}
