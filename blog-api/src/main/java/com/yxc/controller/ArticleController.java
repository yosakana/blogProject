package com.yxc.controller;


import com.yxc.service.ArticleService;
import com.yxc.vo.Result;
import com.yxc.vo.params.PageParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

//JSON数据进行交互
@RestController
@RequestMapping("/articles")
public class ArticleController {

    @Autowired
    private ArticleService articleService;

    /**
     * 首页 list文章列表
     * @param pageParams
     */
    @PostMapping       //把网页的值封装成pageParams对象
    public Result listArticle(@RequestBody PageParams pageParams){
        //这是Web层，从Service层调取方法
        System.out.println(123);
        return articleService.listArticle(pageParams);
    }
}
