package com.yxc.service.imp;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yxc.dao.mapper.ArticleMapper;
import com.yxc.dao.pojo.Article;
import com.yxc.dao.pojo.SysUser;
import com.yxc.service.ArticleService;
import com.yxc.service.SysUserService;
import com.yxc.service.TagService;
import com.yxc.vo.Result;
import com.yxc.vo.params.ArticleVo;
import com.yxc.vo.params.PageParams;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

@Service
public class ArticleServiceImp implements ArticleService {

    @Autowired
    private ArticleMapper articleMapper;
    @Autowired
    private TagService tagService;
    @Autowired
    private SysUserService sysUserService;


    /**
     * 分页查询 文章列表
     *
     * @param pageParams
     * @return
     */
    @Override
    public Result listArticle(PageParams pageParams) {
        /**
         * 1. 分页查询 article数据库表
         * 根据前端返回的页数，和每页的条数进行Page对象的设置
         */
        Page<Article> page = new Page<>(pageParams.getPage(), pageParams.getPageSize());

        //是否置顶进行排序
        LambdaQueryWrapper<Article> queryWrapper = new LambdaQueryWrapper<>();
        //order by weight desc,createTime desc
        queryWrapper.orderByDesc(Article::getWeight, Article::getCreateDate);

        //通过Page对象设置的页数和条数，以及条件构造器进行查询,获得分页好的Page对象
        Page<Article> articlePage = articleMapper.selectPage(page, queryWrapper);

        //获取全部对象
        List<Article> records = articlePage.getRecords();


        //不能直接返回Article集合，里面的数据有一部分是Web不需要的，所以需要通过
        //ArticleVo进行返回，因为Vo对象的本质是前端所需要的数据
        List<ArticleVo> articleVoList = copyList(records , true, true);



        return Result.success(articleVoList);

    }

    private List<ArticleVo> copyList(List<Article> records , boolean isTag , boolean isAuthor){
        //TODO
        //此处可以对迭代进行优化
        List<ArticleVo> list = new ArrayList<>();

        Iterator<Article> recordsIterator = records.iterator();


        while(recordsIterator.hasNext()){
            Article article = recordsIterator.next();
            list.add(copy(article,isTag,isAuthor));
        }

        System.out.println("copy之后："+list.toString());

        return list;
    }


    //这里有问题
    private ArticleVo copy(Article article , boolean isTag , boolean isAuthor){
        ArticleVo articleVo = new ArticleVo();
        BeanUtils.copyProperties(article , articleVo);

        //转换article获取的Long时间至String
        Date date = new Date(article.getCreateDate());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String formatedDate = sdf.format(date);

        articleVo.setCreateDate(formatedDate);



        /* 并不是所有的接口，都需要标签，作者信息 */
        if(isTag){
            Long articleId = article.getId();
            articleVo.setTags(tagService.findTagsByArticleId(articleId));
        }
        if(isAuthor){
            SysUser sysUser = sysUserService.findUserById(article.getAuthorId());
            String nickname = sysUser.getNickname();
            //防止出现空指针，imp层添加了判断条件
            articleVo.setAuthor(nickname);
        }

        //返回null了啊！！！！！！！！！ return null;
        return articleVo;
    }

}
