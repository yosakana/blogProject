package com.yxc.service;

import com.yxc.dao.pojo.SysUser;
import com.yxc.vo.Result;

public interface SysUserService {

    SysUser findUserById(Long id);


    SysUser findUser(String account, String password);

    Result findUserByToken(String token);
}
