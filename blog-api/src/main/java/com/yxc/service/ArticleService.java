package com.yxc.service;

import com.yxc.vo.Result;
import com.yxc.vo.params.PageParams;

public interface ArticleService {
    Result listArticle(PageParams pageParams);
}
