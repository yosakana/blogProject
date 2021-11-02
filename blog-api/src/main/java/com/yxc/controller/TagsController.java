package com.yxc.controller;

import com.yxc.dao.mapper.TagMapper;
import com.yxc.service.TagService;
import com.yxc.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/tags")
public class TagsController {

    //Web层请求Service层
    @Autowired
    private TagService tagService;

    //有一个专门返回数据和成功与否的vo实体类
    @GetMapping("/hot")
    public Result hot(){

        //默认查询6个最热标签
        int limit = 6;
        Result hots = tagService.hots(6);

        return hots;
    }
}
