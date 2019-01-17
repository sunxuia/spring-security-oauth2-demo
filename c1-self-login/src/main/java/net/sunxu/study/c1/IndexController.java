package net.sunxu.study.c1;

import com.google.common.collect.ImmutableMap;
import net.sunxu.study.user.CustomUserDetails;
import net.sunxu.study.user.UserModel;
import net.sunxu.study.user.UserUtils;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.Collection;
import java.util.Map;

@Controller
public class IndexController {
    @RequestMapping("/")
    public ModelAndView index() {
        UserDetails user = (UserDetails) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();
        return new ModelAndView("index", "user", ((CustomUserDetails) user).getUser());
    }

}
