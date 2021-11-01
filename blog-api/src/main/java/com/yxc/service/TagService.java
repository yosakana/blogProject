package com.yxc.service;

import com.yxc.vo.Result;
import com.yxc.vo.params.TagVo;

import java.util.List;

public interface TagService {

    List<TagVo> findTagsByArticleId(Long ariticleId);









    Result hots(int limit);
}
