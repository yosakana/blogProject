package com.yxc.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yxc.dao.pojo.Tag;
import com.yxc.vo.params.TagVo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface TagMapper extends BaseMapper<Tag> {

    //根据文章id查询标签列表
    List<Tag> findTagsByArticleId(Long ariticleId);

    //查询最热标签 dao底层还用不到Vo对象，由Service层来操作
    List<Long> findHotTagIds(int limit);

    //通过tagId寻找tagName
    List<Tag> findTagsByTagIds(List<Long> hotTagIds);
}
