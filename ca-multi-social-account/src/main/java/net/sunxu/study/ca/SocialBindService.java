package net.sunxu.study.ca;

import net.sunxu.study.user.UserModel;

public interface SocialBindService {
    void bindSocial(UserModel userModel,  String socialType, String socialId);

    void unbindSocial(String socialType);
}
