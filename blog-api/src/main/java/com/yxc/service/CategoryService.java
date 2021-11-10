package com.yxc.service;

import com.yxc.vo.CategoryVo;
import com.yxc.vo.Result;

public interface CategoryService {

    CategoryVo findCategoryById(Long id);

    Result getAllCategorys();

    Result getAllArticleCategorys();

    Result categoryById(Long id);
}
