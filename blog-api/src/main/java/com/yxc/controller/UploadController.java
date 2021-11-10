package com.yxc.controller;

import com.yxc.utils.QiniuUtils;
import com.yxc.vo.Result;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequestMapping("/upload")
public class UploadController {

    @Autowired
    private QiniuUtils qiniuUtils;

    @PostMapping      //文件一定要是 MultipartFile 类
    public Result upload(@RequestParam("image") MultipartFile file){
        //获取原始名称 如 aaa.jpg
        String originalFilename = file.getOriginalFilename();

        //但不能上传源文件，要保证文件名称的特殊性                             // 获取 分离器之后的所有字符串
        String fileName = UUID.randomUUID().toString() + "." + StringUtils.substringAfterLast(originalFilename, ".");

        //上传至七牛云
        boolean upload = qiniuUtils.upload(file, fileName);

        if(upload){
            return Result.success(QiniuUtils.url+fileName);
        }else{
            return Result.fail(20001,"上传失败");
        }

    }
}
