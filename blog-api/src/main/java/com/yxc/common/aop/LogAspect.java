package com.yxc.common.aop;

import com.alibaba.fastjson.JSON;
import com.yxc.utils.HttpContextUtil;
import com.yxc.utils.IpUtils;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;

/*
    切面日志
 */

@Component
@Aspect
@Slf4j
public class LogAspect {

    //注解也可以作为一个切点
    @Pointcut("@annotation(com.yxc.common.aop.LogAnnotation)")
    public void logPointCut() {

    }

    //针对切点进行增强（Around等于Before和AfterReturning）
    @Around("logPointCut()")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        long beginTime = System.currentTimeMillis();

        //执行切点方法 (执行之前就跟 @Before 差不多)
        Object result = point.proceed();
        //方法执行之后 也就是AfterReturning

        //执行时长(毫秒)
        long time = System.currentTimeMillis() - beginTime;

        //保存日志   这个time是整个程序执行的时间
        recordLog(point, time);

        //这个Result其实是当前方法执行的结果，可以对其进行修改
        //根据需求来定，当然需求没有任何需要修改结果的部分，就这样返回
        return result;

    }

    private void recordLog(ProceedingJoinPoint point, long time) {
        //先获取其方法签名 point.getSignature , 需要强转成 MethodSignature
        MethodSignature signature = (MethodSignature)point.getSignature();

        //通过signature获取切点所执行的方法
        Method method = signature.getMethod();

        //再通过方法来获取 自己写的 注解
        LogAnnotation logAnnotation = method.getAnnotation(LogAnnotation.class);

        log.info("=====================log start================================");
        log.info("module:{}",logAnnotation.module());
        log.info("operation:{}",logAnnotation.operator());

        //请求的方法名
        String className = point.getTarget().getClass().getName();
        String methodName = signature.getName();
        log.info("request method:{}",className+"."+methodName+"()");

        //请求的参数
        Object[] args = point.getArgs();
        String params = JSON.toJSONString(args[0]);
        log.info("params:{}",params);


        //获取Request 设置IP地址
        HttpServletRequest httpServletRequest = HttpContextUtil.getHttpServletRequest();
        log.info("ip:{}", IpUtils.getIpAddress(httpServletRequest));

        //打印执行时间
        log.info("excute time : {} ms" , time);
        log.info("=====================log end================================");
    }


}
