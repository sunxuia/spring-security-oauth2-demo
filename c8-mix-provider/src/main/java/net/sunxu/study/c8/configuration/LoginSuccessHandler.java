package net.sunxu.study.c8.configuration;

import net.sunxu.study.user.CustomUserDetails;
import net.sunxu.study.user.UserModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static net.sunxu.study.user.UserUtils.getIpAddress;

public class LoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        UserModel userInfo = ((CustomUserDetails) authentication.getPrincipal()).getUser();
        logger.info("用户[{}]成功登录[{}], IP[{}]", userInfo.getName(), request.getContextPath(), getIpAddress(request));

        super.onAuthenticationSuccess(request, response, authentication);
    }
}
