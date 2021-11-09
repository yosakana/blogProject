package com.yxc.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.util.List;

@Data
public class CommentVo {

    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    private UserVo author;
    private String content;
    private Integer level;
    private String createDate;
    private List<CommentVo> childrens;

    private UserVo toUser;
}
