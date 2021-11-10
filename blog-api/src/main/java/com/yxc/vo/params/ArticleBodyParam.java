package com.yxc.vo.params;

import lombok.Data;

@Data
public class ArticleBodyParam {

    //获取内容 文字
    private String content;

    //获取内容 样式 + 文字
    private String contentHtml;
}
