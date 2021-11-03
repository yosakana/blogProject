package com.yxc.controller;

import com.yxc.service.LoginService;
import com.yxc.vo.Result;
import com.yxc.vo.params.LoginParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LoginController {

    @Autowired
    private LoginService loginService;

    @PostMapping("/login")
                    //@RequstBody做一个数据的封装
    public Result login(@RequestBody LoginParam loginParam){

        return loginService.login(loginParam);
    }
}
