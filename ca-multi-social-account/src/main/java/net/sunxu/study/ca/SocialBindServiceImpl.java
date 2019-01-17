package net.sunxu.study.ca;

import net.sunxu.study.user.CustomUserDetails;
import net.sunxu.study.user.UserModel;
import net.sunxu.study.user.UserUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.print.attribute.standard.PrinterURI;
import java.util.HashMap;
import java.util.Map;

@Service
public class SocialBindServiceImpl implements SocialBindService {
    private static class SocialBind {
        String type;
        String socialId;
        String localUserId;
    }

    // 使用map 来存储用户数据
    private Map<String, SocialBind> store = new HashMap<>();

    @Override
    public void bindSocial(UserModel user, String socialType, String socialId) {
        String key = socialType + "-" + user.getName();
        SocialBind bind = new SocialBind();
        bind.type = socialType;
        bind.socialId = socialId;
        bind.localUserId = user.getName();
        store.put(key, bind);

        if (socialType == "github") {
            user.setGithubId(socialId);
        } else if (socialType == "weibo") {
            user.setWeiboId(socialId);
        }
    }

    @Override
    public void unbindSocial(String socialType) {
        UserModel user = (UserModel) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String key = socialType + "-" + user.getName();
        store.remove(key);

        if (socialType == "github") {
            user.setGithubId("");
        } else if (socialType == "weibo") {
            user.setWeiboId("");
        }
    }
}
