package com.yxc.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

/*
    这个Vo类封装的是各种状态码
    以及前端所需要的数据
*/
@Data
//生成全属性的构造方法
@AllArgsConstructor
public class Result {

    private boolean success;

    private int code;

    private String msg;

    private Object data;

    //articles页面成功
    public static Result success(Object data){
        return new Result(true , 200 , "success" ,  data);
    }

    //articles页面失败
    public static Result fail(int code , String message){
        return new Result(false , code , message , null);
    }
}
