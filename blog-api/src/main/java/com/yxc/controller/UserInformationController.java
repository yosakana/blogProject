package com.yxc.controller;

import com.yxc.service.SysUserService;
import com.yxc.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserInformationController {

    @Autowired
    private SysUserService sysUserService;

    //因为是存储在Header中的信息
    @GetMapping("/currentUser")
    public Result currentUser(@RequestHeader("Authorization") String token){
        return sysUserService.findUserByToken(token);
    }
}
