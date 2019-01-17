package net.sunxu.study.ca;

import javafx.scene.input.PickResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.security.Principal;

@Controller
public class IndexController {
    @Autowired
    private SocialBindService socialBindService;

    @RequestMapping("/")
    public String index() {
        return "index";
    }

    @RequestMapping("/user")
    @ResponseBody
    public Principal user(Principal principal) {
        return principal;
    }

    @RequestMapping("/unbind/{socialType}")
    public String unbindGithub(@PathVariable("socialType") String socialType) {
        socialBindService.unbindSocial(socialType);
        return "redirect:/";
    }
}
