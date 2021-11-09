package com.yxc.service;

import com.yxc.dao.pojo.SysUser;
import com.yxc.vo.Result;
import com.yxc.vo.UserVo;

public interface SysUserService {

    SysUser findUserById(Long id);


    SysUser findUser(String account, String password);

    Result findUserByToken(String token);

    SysUser findUserByAccount(String account);

    void save(SysUser sysUser);

    UserVo findCommentUserById(Long authorId);
}
