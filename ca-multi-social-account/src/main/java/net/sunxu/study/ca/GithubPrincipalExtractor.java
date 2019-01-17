package net.sunxu.study.ca;

import net.sunxu.study.user.CustomUserDetails;
import net.sunxu.study.user.UserModel;
import net.sunxu.study.user.UserService;
import net.sunxu.study.user.UserUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.oauth2.resource.PrincipalExtractor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Map;

/**
 * 从github 获得用户信息转换成本地信息, 生成principal
 */
@Component
public class GithubPrincipalExtractor implements PrincipalExtractor {
    @Autowired
    private UserService userService;
    @Autowired
    private SocialBindService socialBindService;

    @Override
    public Object extractPrincipal(Map<String, Object> map) {
        UserModel user = null;
        try {
            user = (UserModel) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        } catch (RuntimeException err) {
        }

        if (user == null) {
            // 新增用户
            UserModel userModel = new UserModel();
            userModel.setName(getStr(map, "login"));
            userModel.setPortrait(getStr(map, "avatar_url"));
            userModel.setRoleNames(Arrays.asList("normal"));
            userModel.setGithubId(getStr(map, "id"));
            userService.saveUser(userModel);

            // 绑定社交账号
            socialBindService.bindSocial(userModel, "github", (String) map.get("login"));
            return userModel;
        } else {
            // 绑定社交账号
            socialBindService.bindSocial(user, "github", (String) map.get("login"));
            return user;
        }
    }

    private String getStr(Map<String, Object> map, String key) {
        Object val = map.get(key);
        if (val == null) {
            return "";
        } else {
            return val.toString();
        }
    }
}
