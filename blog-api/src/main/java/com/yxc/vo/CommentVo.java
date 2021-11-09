package com.yxc.vo;

import lombok.Data;

import java.util.List;

@Data
public class CommentVo {
    private Long id;
    private UserVo author;
    private String content;
    private Integer level;
    private String createDate;
    private List<CommentVo> childrens;

    private UserVo toUser;
}
