package net.sunxu.study.c7.client;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
public class IndexController {
    /**
     * 获得的principal 中包含有用户信息数据
     * @param principal
     * @return
     */
    @RequestMapping("/")
    public Principal index(Principal principal) {
        return principal;
    }

}
