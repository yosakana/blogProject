package com.yxc.service.imp;

import com.yxc.dao.mapper.TagMapper;
import com.yxc.dao.pojo.Tag;
import com.yxc.service.TagService;
import com.yxc.vo.Result;
import com.yxc.vo.params.TagVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Service
public class TagServiceImp implements TagService {

    @Autowired
    private TagMapper tagMapper;

    @Override
    public List<TagVo> findTagsByArticleId(Long articleId) {
        //mybatis plus无法进行多表查询
        //此处采用原生Mybatis进行操作
        //TODO
        List<Tag> tags = tagMapper.findTagsByArticleId(articleId);

        return copyList(tags);
    }

    private List<TagVo> copyList(List<Tag> tags) {

        List<TagVo> tagVoList = new ArrayList<TagVo>();

        Iterator<Tag> tagIterator = tags.iterator();
        while(tagIterator.hasNext()){
            Tag tag = tagIterator.next();
            tagVoList.add(copy(tag));
        }
        return tagVoList;
    }

    private TagVo copy(Tag tag) {
        TagVo tagVo = new TagVo();
        BeanUtils.copyProperties(tag , tagVo);
        return tagVo;
    }


    @Override
    public Result hots(int limit) {
        return null;
    }
}
