package com.yxc.service;

import com.yxc.vo.Result;
import com.yxc.vo.TagVo;

import java.util.List;

public interface TagService {

    List<TagVo> findTagsByArticleId(Long ariticleId);

    Result hots(int limit);
}
