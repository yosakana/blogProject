package com.yxc.service;

import com.yxc.dao.pojo.SysUser;
import com.yxc.vo.Result;
import com.yxc.vo.params.LoginParam;

public interface LoginService {

    public SysUser checkToken(String token);

    public Result login(LoginParam loginParam);

    Result logout(String token);
}
