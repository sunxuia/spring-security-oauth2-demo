package net.sunxu.study.user;

import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {
    public void saveUser(UserModel userModel);
}
