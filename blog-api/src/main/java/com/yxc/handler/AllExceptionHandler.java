package com.yxc.handler;

import com.yxc.vo.Result;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/* 统一异常处理 */

//对加了@Controller注解的方法进行拦截处理Aop的实现
@ControllerAdvice
public class AllExceptionHandler {
    //进行异常处理，处理Exception.class的异常
    @ExceptionHandler(Exception.class)
    @ResponseBody //返回json数据，不加是返回页面
    public Result doException(Exception ex){
        ex.printStackTrace();
        return Result.fail(-999 , "系统异常");
    }
}
