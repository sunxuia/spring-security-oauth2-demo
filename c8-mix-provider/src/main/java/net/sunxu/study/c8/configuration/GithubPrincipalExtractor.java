package net.sunxu.study.c8.configuration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import net.sunxu.study.user.UserModel;
import net.sunxu.study.user.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.oauth2.resource.PrincipalExtractor;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import javax.naming.Name;
import java.io.IOException;
import java.security.Principal;
import java.util.*;

/**
 * 从github 获得用户信息转换成本地信息, 生成principal
 */
@Component
public class GithubPrincipalExtractor implements PrincipalExtractor {
    @Autowired
    private UserService userService;

    @Override
    public Object extractPrincipal(Map<String, Object> map) {
        //可以在这里加上一段判断用户是否存在的内容
        //在这里保存用户数据
        UserModel userModel = new UserModel();
        userModel.setName(getStr(map, "login"));
        userModel.setPortrait(getStr(map, "avatar_url"));
        userModel.setRoleNames(Arrays.asList("normal"));
        userModel.setGithubId(getStr(map, "id"));
        userService.saveUser(userModel);

        return userModel.getName();
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
