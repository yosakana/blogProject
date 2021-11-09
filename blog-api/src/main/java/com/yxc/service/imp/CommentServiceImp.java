package com.yxc.service.imp;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yxc.dao.mapper.CommentMapper;
import com.yxc.dao.pojo.Comment;
import com.yxc.dao.pojo.SysUser;
import com.yxc.service.CommentService;
import com.yxc.service.SysUserService;
import com.yxc.service.UserThreadLocal;
import com.yxc.vo.CommentVo;
import com.yxc.vo.Result;
import com.yxc.vo.UserVo;
import com.yxc.vo.params.CommentParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class CommentServiceImp implements CommentService {

    @Autowired
    private CommentMapper commentMapper;
    @Autowired
    private SysUserService sysUserService;

    @Override
    public Result findCommentsById(Long id) {
        //通过文章id查询文章的所有评论
        LambdaQueryWrapper<Comment> lambdaQueryWrapper = new LambdaQueryWrapper();
        lambdaQueryWrapper.eq(Comment::getArticleId , id);
        List<Comment> comments = commentMapper.selectList(lambdaQueryWrapper);

        List<CommentVo> commentVoList = copyList(comments);

        System.out.println(commentVoList);

        return Result.success(commentVoList);
    }

    @Override
    public Result saveCommentChange(CommentParam commentParam) {

        //通过ThreadLocal获取当前登录的用户
        SysUser sysUser = UserThreadLocal.get();

        Comment comment = new Comment();

        comment.setContent(commentParam.getContent());
        comment.setCreateDate(new Date().getTime());
        comment.setArticleId(commentParam.getArticleId());
        comment.setToUid(commentParam.getToUserId());
        comment.setAuthorId(sysUser.getId());


        //这个设计的评论层级最多只有level 1和2
        Long parent = commentParam.getParent();
        if(parent == null || parent == 0){
            comment.setLevel(1);
            comment.setParentId(0L);
        }else{
            comment.setLevel(2);
            comment.setParentId(parent);
        }

        Long toUserId = commentParam.getToUserId();
        //                       条件     ?  true : false
        comment.setToUid(toUserId == null ? 0 : toUserId);

        //将数据插入
        this.commentMapper.insert(comment);

        return Result.success(null);
    }

    //遍历comments集合
    public List<CommentVo> copyList(List<Comment> comments){

        List<CommentVo> list = new ArrayList<CommentVo>();

        for (Comment comment : comments) {
            list.add(copy(comment));
        }

        return list;
    }

    //逐个将Comment对象转换成CommentVo对象
    public CommentVo copy(Comment comment){

        CommentVo commentVo = new CommentVo();


        UserVo author = sysUserService.findCommentUserById(comment.getAuthorId());

        commentVo.setId(comment.getId());
        commentVo.setContent(comment.getContent());
        commentVo.setLevel(comment.getLevel());
        commentVo.setAuthor(author);

        if(comment.getCreateDate() != null){
            Date date = new Date(comment.getCreateDate());
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            String formattedDate = sdf.format(date);
            commentVo.setCreateDate(formattedDate);
        }

        //查询评论的评论
        List<CommentVo> commentByParentId = findCommentByParentId(comment.getId());
        commentVo.setChildrens(commentByParentId);

        if(comment.getLevel() > 1){
            Long id = comment.getAuthorId();
            UserVo commentUserById = sysUserService.findCommentUserById(id);
            commentVo.setToUser(commentUserById);
        }

        return commentVo;
    }

    public List<CommentVo> findCommentByParentId(Long id){
        LambdaQueryWrapper<Comment> lambdaQueryWrapper = new LambdaQueryWrapper();
        lambdaQueryWrapper.eq(Comment::getParentId , id);
        List<Comment> comments = commentMapper.selectList(lambdaQueryWrapper);
        List<CommentVo> commentVoList = copyList(comments);

        return commentVoList;
    }

}
