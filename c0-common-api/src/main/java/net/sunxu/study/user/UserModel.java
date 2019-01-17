package net.sunxu.study.user;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Date;
import java.util.List;

@Data
@ToString
@NoArgsConstructor
public class UserModel {
    private  String name;

    private String password;

    private String mailAddress;

    private String portrait;

    private Date createTime;

    private List<String> roleNames;

    private UserState state;

    private String githubId;

    private String weiboId;
}
