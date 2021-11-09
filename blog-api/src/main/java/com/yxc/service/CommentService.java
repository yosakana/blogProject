package com.yxc.service;

import com.yxc.vo.Result;
import com.yxc.vo.params.CommentParam;

public interface CommentService {
    Result findCommentsById(Long id);

    Result saveCommentChange(CommentParam commentParam);
}
