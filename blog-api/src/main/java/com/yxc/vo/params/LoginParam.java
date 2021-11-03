package com.yxc.vo.params;

import lombok.Data;

@Data
//前端传过来的，但数据库没有对应属性的数据
public class LoginParam {


    private String account;


    private String password;

}
