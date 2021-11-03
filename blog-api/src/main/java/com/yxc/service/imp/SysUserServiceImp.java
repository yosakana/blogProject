package com.yxc.service.imp;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.yxc.dao.mapper.SysUserMapper;
import com.yxc.dao.pojo.SysUser;
import com.yxc.service.LoginService;
import com.yxc.service.SysUserService;
import com.yxc.vo.ErrorCode;
import com.yxc.vo.LoginUserVo;
import com.yxc.vo.Result;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SysUserServiceImp implements SysUserService {

    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private LoginService loginService;

    @Override
    public SysUser findUserById(Long id) {
        SysUser sysUser = sysUserMapper.selectById(id);
        if (sysUser == null){
            sysUser = new SysUser();
            sysUser.setNickname("无名字");
        }
        return sysUser;
    }

    /**
     * 登录功能（通过前端传来的账号密码查找数据库）
     * @param account
     * @param password
     * @return
     */
    @Override
    public SysUser findUser(String account, String password) {
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<SysUser>();
        //这个是 字段 = 值 的条件
        wrapper.eq(SysUser::getAccount,account);
        wrapper.eq(SysUser::getPassword,password);
        //这个是只select这些字段
        wrapper.select(SysUser::getAccount , SysUser::getId , SysUser::getAvatar , SysUser::getNickname);

        SysUser sysUser = sysUserMapper.selectOne(wrapper);
        return sysUser;
    }

    /**
     * 通过token查询用户信息
     * @param token
     * @return
     */
    @Override
    public Result findUserByToken(String token) {
        /*
            1.token的合法性校验
                是否为空，解析是否成功，redis是否存在
            2.如果校验失败 返回错误
            3.成功，返回对应的结果 LoginUserVo
        */

        //通过这个方法 返回正确的SysUser信息
        SysUser sysUser = loginService.checkToken(token);
        if(sysUser == null){
             return Result.fail(ErrorCode.TOKEN_ERROR.getCode(), ErrorCode.TOKEN_ERROR.getMsg());
        }

        LoginUserVo loginUserVo = new LoginUserVo();
        BeanUtils.copyProperties(sysUser,loginUserVo);

        return Result.success(loginUserVo);
    }
}
