package com.yxc.blogapi;

import com.yxc.utils.JWTUtils;
import org.apache.commons.codec.digest.DigestUtils;

public class test {
    public static void main(String[] args) {
        String s = DigestUtils.md5Hex("admin" + "mszlu!@#");
        System.out.println(s);


        String token = JWTUtils.createToken(Long.parseLong(1+""));
        System.out.println(token);
    }
}
