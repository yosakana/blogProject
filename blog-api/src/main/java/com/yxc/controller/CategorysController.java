package com.yxc.controller;

import com.yxc.service.CategoryService;
import com.yxc.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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

    @GetMapping("/detail")
    public Result detail(){
        return categoryService.getAllArticleCategorys();
    }

    @GetMapping("/detail/{id}")
    public Result detailById(@PathVariable("id") Long id){
        return categoryService.categoryById(id);
    }

}
