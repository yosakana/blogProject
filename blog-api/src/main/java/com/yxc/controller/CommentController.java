package com.yxc.controller;

import com.yxc.service.CommentService;
import com.yxc.vo.Result;
import com.yxc.vo.params.CommentParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/comments")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @GetMapping("/article/{id}")
    public Result comments(@PathVariable("id")Long id){
        return commentService.findCommentsById(id);
    }


    /*
        加入到拦截器中，没登录不准评论
     */
    @PostMapping("/create/change")
    public Result createComment(@RequestBody CommentParam commentParam){
        return commentService.saveCommentChange(commentParam);
    }







}
