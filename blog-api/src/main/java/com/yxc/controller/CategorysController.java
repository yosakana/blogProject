package com.yxc.controller;

import com.yxc.service.CategoryService;
import com.yxc.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/categorys")
public class CategorysController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping
    public Result getCategorys(){
        return categoryService.getAllCategorys();
    }
}
