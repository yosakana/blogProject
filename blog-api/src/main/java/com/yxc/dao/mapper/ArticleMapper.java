package com.yxc.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yxc.dao.pojo.Article;
import org.apache.ibatis.annotations.Mapper;

@Mapper                            //自动把表和pojo进行一个关联
public interface ArticleMapper extends BaseMapper<Article> {


}
