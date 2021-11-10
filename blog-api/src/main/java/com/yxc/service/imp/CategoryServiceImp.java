package com.yxc.service.imp;

import com.yxc.dao.mapper.CategoryMapper;
import com.yxc.dao.pojo.Category;
import com.yxc.service.CategoryService;
import com.yxc.vo.CategoryVo;
import com.yxc.vo.Result;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CategoryServiceImp implements CategoryService {
    @Autowired
    private CategoryMapper categoryMapper;

    @Override
    public CategoryVo findCategoryById(Long id){
        Category category = categoryMapper.selectById(id);
        CategoryVo categoryVo = new CategoryVo();
        BeanUtils.copyProperties(category,categoryVo);
        return categoryVo;
    }

    @Override
    public Result getAllCategorys() {

        List<CategoryVo> list = new ArrayList<>();

        List<Category> categories = categoryMapper.selectList(null);

        List<CategoryVo> categoryVos = copyList(categories);

        return Result.success(categoryVos);
    }

    @Override
    public Result getAllArticleCategorys() {
        List<Category> categories = categoryMapper.selectList(null);
        return Result.success(categories);
    }

    @Override
    public Result categoryById(Long id) {
        Category category = categoryMapper.selectById(id);
        return Result.success(category);
    }

    public List<CategoryVo> copyList(List<Category> categories){
        List<CategoryVo> list = new ArrayList<>();

        for (Category category : categories) {
            list.add(copy(category));
        }

        return list;

    }

    public CategoryVo copy(Category category){
        CategoryVo categoryVo = new CategoryVo();

        BeanUtils.copyProperties(category , categoryVo);

        return categoryVo;
    }
}
