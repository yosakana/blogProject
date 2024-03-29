package com.yxc.controller;


import com.yxc.common.aop.LogAnnotation;
import com.yxc.service.ArticleService;
import com.yxc.service.CommentService;
import com.yxc.vo.ArticleVo;
import com.yxc.vo.Result;
import com.yxc.vo.params.ArticleParam;
import com.yxc.vo.params.PageParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

//JSON数据进行交互
@RestController
@RequestMapping("/articles")
public class ArticleController {

    @Autowired
    private ArticleService articleService;
    @Autowired
    private CommentService commentService;

    /**
     * 首页 list文章列表
     * @param pageParams
     */
    @PostMapping       //把网页的值封装成pageParams对象
    @LogAnnotation(module="文章",operator="获取文章列表")
    public Result listArticle(@RequestBody PageParams pageParams){
        //这是Web层，从Service层调取方法
        System.out.println(123);
        return articleService.listArticle(pageParams);
    }

    /**
     * 首页最热文章模块
     * @return
     */
    @PostMapping("/hot")
    public Result hotArticle(){
        int limit = 5;
        return articleService.findHotArticles(limit);
    }

    /**
     * 首页最新文章模块
     * @return
     */
    @PostMapping("/new")
    public Result newArticles(){
        int limit = 5;
        return articleService.newArticles(limit);
    }

    /**
     * 文章归档（返回年月日，有几篇）
     * @return
     */
    @PostMapping("/listArchives")
    public Result listArchives(){
        return articleService.listArchives();
    }

    @PostMapping("view/{id}")
    public Result findArticleById(@PathVariable("id") Long id) {
        ArticleVo articleVo = articleService.findArticleById(id);

        return Result.success(articleVo);
    }

    @PostMapping("/publish")
    public Result publish(@RequestBody ArticleParam articleParam){
        return articleService.publish(articleParam);
    }

}
