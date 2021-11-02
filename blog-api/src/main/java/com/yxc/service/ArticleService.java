package com.yxc.service;

import com.yxc.vo.Result;
import com.yxc.vo.params.PageParams;

public interface ArticleService {
    /**
     * 首页文章展示
     * @param pageParams
     * @return
     */
    Result listArticle(PageParams pageParams);

    /**
     * 首页热门文章
     * @param limit
     * @return
     */
    Result findHotArticles(int limit);

    /**
     * 首页最新文章
     * @param limit
     * @return
     */
    Result newArticles(int limit);

    /**
     * 文章归档
     * @return
     */
    Result listArchives();
}
