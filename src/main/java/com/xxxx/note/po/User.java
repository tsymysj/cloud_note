package com.xxxx.note.po;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class User {

    private Integer userId;
    private String uname;
    private String upwd;
    private String nick;//昵称
    private String head;//头像
    private String mood;//用户签名

}
