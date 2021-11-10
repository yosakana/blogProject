package com.yxc.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yxc.dao.dos.Archives;
import com.yxc.dao.pojo.Article;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper                            //自动把表和pojo进行一个关联
public interface ArticleMapper extends BaseMapper<Article> {

    //由于要调用一些year() month() 方法，所以需要通过sql语句的方式执行
    List<Archives> listArchivres();


    //这个IPage是自动对查出来的结果进行一个分页操作
    IPage<Article> listArticle(Page<Article> page,
                               Long categoryId,
                               Long tagId,
                               String year,
                               String month
                                );

}
















