package com.yxc.service.imp;

import com.yxc.dao.mapper.SysUserMapper;
import com.yxc.dao.pojo.SysUser;
import com.yxc.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SysUserServiceImp implements SysUserService {

    @Autowired
    private SysUserMapper sysUserMapper;

    @Override
    public SysUser findUserById(Long id) {
        SysUser sysUser = sysUserMapper.selectById(id);
        if (sysUser == null){
            sysUser = new SysUser();
            sysUser.setNickname("无名字");
        }
        return sysUser;
    }
}
