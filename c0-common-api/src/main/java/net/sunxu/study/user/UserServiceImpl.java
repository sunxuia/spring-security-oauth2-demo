package net.sunxu.study.user;

import com.google.common.collect.ConcurrentHashMultiset;
import com.google.common.collect.ImmutableList;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.awt.image.RenderedImage;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Qualifier("userDetailService")
public class UserServiceImpl implements UserService {
    private ConcurrentHashMap<String, UserModel> repo = new ConcurrentHashMap<>();

    @PostConstruct
    private void initialData() {
        UserModel user = new UserModel();
        user.setCreateTime(new Date());
        user.setMailAddress("test@test.com");
        user.setName("admin");
        user.setPassword("{bcrypt}" + BCrypt.hashpw("123456", BCrypt.gensalt()));
        user.setState(UserState.NORMAL);
        user.setRoleNames(ImmutableList.of("admin", "user"));
        repo.put(user.getName(), user);
    }

    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
        UserModel user = repo.get(userName);
        if (user == null) {
            throw new UsernameNotFoundException("user " + userName + " not found");
        }
        return new CustomUserDetails(user);
    }

    @Override
    public void saveUser(UserModel userModel) {
        repo.put(userModel.getName(), userModel);
    }
}
