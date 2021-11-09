package com.yxc.vo.params;

import lombok.Data;

@Data
//前端传过来，但需要单独进行对应的实体类
public class LoginParam {


    private String account;


    private String password;


    private String nickname;

}
