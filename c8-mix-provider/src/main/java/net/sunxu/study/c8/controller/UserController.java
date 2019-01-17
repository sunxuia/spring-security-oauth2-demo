package net.sunxu.study.c8.controller;

import net.sunxu.study.user.CustomUserDetails;
import net.sunxu.study.user.UserModel;
import net.sunxu.study.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.security.Principal;

@Controller
public class UserController {
    @Autowired
    private UserService userService;

    @RequestMapping("/")
    public ModelAndView login(Principal principal) {
        return new ModelAndView("login", "principal", principal);
    }

    @RequestMapping({"/user", "/me"})
    @ResponseBody
    public UserModel user(Principal principal) {
        String userName = principal.getName();
        UserModel userModel = ((CustomUserDetails) userService.loadUserByUsername(userName)).getUser();
        return userModel;
    }
}
