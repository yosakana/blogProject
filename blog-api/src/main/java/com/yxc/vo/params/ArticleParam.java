package com.yxc.vo.params;

import com.yxc.vo.CategoryVo;
import com.yxc.vo.TagVo;
import lombok.Data;

import java.util.List;

@Data
//前端传来的值，暂时进行一个保存
public class ArticleParam {

    private String title;

    private Long id;

    private ArticleBodyParam body;

    private CategoryVo category;

    private String summary;

    private List<TagVo> tags;

}
