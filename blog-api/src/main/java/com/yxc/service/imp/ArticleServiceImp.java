package com.yxc.service.imp;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yxc.dao.dos.Archives;
import com.yxc.dao.mapper.ArticleBodyMapper;
import com.yxc.dao.mapper.ArticleMapper;
import com.yxc.dao.mapper.ArticleTagMapper;
import com.yxc.dao.pojo.Article;
import com.yxc.dao.pojo.ArticleBody;
import com.yxc.dao.pojo.ArticleTag;
import com.yxc.dao.pojo.SysUser;
import com.yxc.service.*;
import com.yxc.vo.*;
import com.yxc.vo.params.ArticleParam;
import com.yxc.vo.params.PageParams;
import org.joda.time.DateTime;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class ArticleServiceImp implements ArticleService {

    @Autowired
    private ArticleMapper articleMapper;
    @Autowired
    private TagService tagService;
    @Autowired
    private SysUserService sysUserService;
    @Autowired
    private ArticleTagMapper articleTagMapper;
    @Autowired
    private ArticleBodyMapper articleBodyMapper;

    /**
     * 最新版的文章列表获取操作
     *
     * @param pageParams
     * @return
     */
    @Override
    public Result listArticle(PageParams pageParams) {
        Page<Article> page = new Page<>();
        IPage<Article> articleIPage = articleMapper.listArticle(page,
                pageParams.getCategoryId(),
                pageParams.getTagId(),
                pageParams.getYear(),
                pageParams.getMonth());

        List<Article> records = articleIPage.getRecords();

        return Result.success(copyList(records , true , true));

    }


    /**
     * 分页查询 文章列表
     *
     * @param pageParams
     * @return
     */
    public Result list1Article(PageParams pageParams) {
        /**
         * 1. 分页查询 article数据库表
         * 根据前端返回的页数，和每页的条数进行Page对象的设置
         */
        Page<Article> page = new Page<>(pageParams.getPage(), pageParams.getPageSize());

        //是否置顶进行排序
        LambdaQueryWrapper<Article> queryWrapper = new LambdaQueryWrapper<>();

        //在类别分类里的文章列表展示
        if (pageParams.getCategoryId() != null) {
            queryWrapper.eq(Article::getCategoryId, pageParams.getCategoryId());
        }

        List<Long> articleIdList = new ArrayList<>();
        //在标签类别里的文章列表展示
        if (pageParams.getTagId() != null) {
            LambdaQueryWrapper<ArticleTag> tagQueryWrapper = new LambdaQueryWrapper();
            tagQueryWrapper.eq(ArticleTag::getTagId, pageParams.getTagId());
            List<ArticleTag> articleTags = articleTagMapper.selectList(tagQueryWrapper);

            for (ArticleTag articleTag : articleTags) {
                articleIdList.add(articleTag.getArticleId());
            }

            if (articleIdList.size() > 0) {
                queryWrapper.in(Article::getId, articleIdList);
            }

        }


        //order by weight desc,createTime desc
        queryWrapper.orderByDesc(Article::getWeight, Article::getCreateDate);

        //通过Page对象设置的页数和条数，以及条件构造器进行查询,获得分页好的Page对象
        Page<Article> articlePage = articleMapper.selectPage(page, queryWrapper);

        //获取全部对象
        List<Article> records = articlePage.getRecords();


        //不能直接返回Article集合，里面的数据有一部分是Web不需要的，所以需要通过
        //ArticleVo进行返回，因为Vo对象的本质是前端所需要的数据
        List<ArticleVo> articleVoList = copyList(records, true, true, false, false);


        return Result.success(articleVoList);

    }


    /**
     * 首页最热文章模块实现
     *
     * @param limit 查询几条
     * @return
     */
    @Override
    public Result findHotArticles(int limit) {

        LambdaQueryWrapper<Article> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        //sql语句的 条件构造器 将select结果按观看数降序排列
        lambdaQueryWrapper.orderByDesc(Article::getViewCounts);

        //获取模块所需要的id和标题
        lambdaQueryWrapper.select(Article::getId, Article::getTitle);

        //无视任何条件直接拼到sql语句的最后（有SQL注入的风险）
        /* 要加空格否则会连在一起 */
        lambdaQueryWrapper.last("limit " + limit);

        // select id,tittle from ms_tittle order by view_counts limit #{limit}
        List<Article> articles = articleMapper.selectList(lambdaQueryWrapper);


        //传给前端的时候，统一转成Vo对象进行传递
        return Result.success(copyList(articles, false, false, false, false));
    }

    /**
     * 首页最新文章模块
     *
     * @param limit
     * @return
     */
    @Override
    public Result newArticles(int limit) {
        //条件构造器的泛型一定要设置
        LambdaQueryWrapper<Article> lambdaQueryWrapper = new LambdaQueryWrapper();
        lambdaQueryWrapper.orderByDesc(Article::getCreateDate);
        lambdaQueryWrapper.select(Article::getId, Article::getTitle);

        List<Article> articles = articleMapper.selectList(lambdaQueryWrapper);

        return Result.success(copyList(articles, false, false, false, false));

    }

    /**
     * 文章归档
     *
     * @return
     */
    @Override
    public Result listArchives() {
        List<Archives> listArchivres = articleMapper.listArchivres();
        return Result.success(listArchivres);
    }

    @Autowired
    private ThreadService threadService;

    @Override
    public ArticleVo findArticleById(Long id) {
        Article article = articleMapper.selectById(id);
        threadService.updateViewCount(articleMapper, article);
        return copy(article, true, true, true, true);
    }

    @Override
    @Transactional
    public Result publish(ArticleParam articleParam) {
        Article article = new Article();

        //用户必须是登录的状态 否则空指针，也就是要加入登录拦截中
        SysUser sysUser = UserThreadLocal.get();
        Long id = sysUser.getId();

        article.setAuthorId(id);

        //插入之后，mybatisPlus会回写一个文章id
        /**
         * 相当重要的一步
         */
        article.setCreateDate(new Date().getTime());
        article.setSummary(articleParam.getSummary());
        article.setTitle(articleParam.getTitle());
        article.setViewCounts(0);
        article.setCommentCounts(0);
        article.setCategoryId(articleParam.getCategory().getId());
        //热度文章（普通文章）
        article.setWeight(Article.Article_Common);
        article.setBodyId(-1L);

        this.articleMapper.insert(article);

        //有一个  文章<-->标签 的多对多 的表，先进行这个表的操作
        List<TagVo> tags = articleParam.getTags();
        if (tags != null) {
            for (TagVo tag : tags) {
                ArticleTag articleTag = new ArticleTag();
                articleTag.setArticleId(article.getId());
                articleTag.setTagId(tag.getId());
                this.articleTagMapper.insert(articleTag);
            }
        }

        //还有一个 文章body的表 ，因为有主外键的关系存在 ，所以需要先生成articleId
        ArticleBody articleBody = new ArticleBody();
        articleBody.setArticleId(article.getId());
        articleBody.setContent(articleParam.getBody().getContent());
        articleBody.setContentHtml(articleParam.getBody().getContentHtml());
        this.articleBodyMapper.insert(articleBody);

        //article还需要一个bodyId
        article.setBodyId(articleBody.getId());

        this.articleMapper.updateById(article);

        //TODO 这个地方有问题
        Map<String, String> map = new HashMap<>();
        map.put("id", article.getId().toString());


        //返回的形式{"id":12232323},会自动解析成json
        return Result.success(map);
    }


    private List<ArticleVo> copyList(List<Article> records, boolean isTag, boolean isAuthor, boolean isBody) {
        List<ArticleVo> articleVoList = new ArrayList<>();
        for (Article record : records) {
            articleVoList.add(copy(record, isTag, isAuthor, isBody, false));
        }
        return articleVoList;
    }

    private List<ArticleVo> copyList(List<Article> records, boolean isTag, boolean isAuthor, boolean isBody, boolean isCategory) {
        List<ArticleVo> articleVoList = new ArrayList<>();
        for (Article record : records) {
            articleVoList.add(copy(record, isTag, isAuthor, isBody, isCategory));
        }
        return articleVoList;
    }

    private ArticleVo copy(Article article, boolean isTag, boolean isAuthor, boolean isBody, boolean isCategory) {
        System.out.println(article);
        ArticleVo articleVo = new ArticleVo();
        BeanUtils.copyProperties(article, articleVo);

        articleVo.setCreateDate(new DateTime(article.getCreateDate()).toString("yyyy-MM-dd HH:mm"));
        //并不是所有的接口 都需要标签 ，作者信息
        if (isTag) {
            Long articleId = article.getId();
            articleVo.setTags(tagService.findTagsByArticleId(articleId));
        }
        if (isAuthor) {
            Long authorId = article.getAuthorId();
            articleVo.setAuthor(sysUserService.findUserById(authorId).getNickname());
        }
        if (isBody) {
            ArticleBodyVo articleBody = findArticleBody(article.getId());
            articleVo.setBody(articleBody);
        }
        if (isCategory) {
            CategoryVo categoryVo = findCategory(article.getCategoryId());
            articleVo.setCategory(categoryVo);
        }
        return articleVo;
    }

    @Autowired
    private CategoryService categoryService;

    private CategoryVo findCategory(Long categoryId) {
        return categoryService.findCategoryById(categoryId);
    }


    private ArticleBodyVo findArticleBody(Long articleId) {
        LambdaQueryWrapper<ArticleBody> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ArticleBody::getArticleId, articleId);
        ArticleBody articleBody = articleBodyMapper.selectOne(queryWrapper);
        ArticleBodyVo articleBodyVo = new ArticleBodyVo();
        articleBodyVo.setContent(articleBody.getContent());
        return articleBodyVo;
    }


    private List<ArticleVo> copyList(List<Article> records, boolean isTag, boolean isAuthor) {
        //TODO
        //此处可以对迭代进行优化
        List<ArticleVo> list = new ArrayList<>();

        Iterator<Article> recordsIterator = records.iterator();


        while (recordsIterator.hasNext()) {
            Article article = recordsIterator.next();

            System.out.println(article);

            list.add(copy(article, isTag, isAuthor));
        }


        return list;
    }


    private ArticleVo copy(Article article, boolean isTag, boolean isAuthor) {
        ArticleVo articleVo = new ArticleVo();
        BeanUtils.copyProperties(article, articleVo);

        //转换article获取的Long时间至String

        /*
            这个方法有个弊端，如果是不需要创建时间的模块调用它的话会出现空指针问题
            所以要加个判断
        */
        if (article.getCreateDate() != null) {

            Date date = new Date(article.getCreateDate());
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            String formatedDate = sdf.format(date);

            articleVo.setCreateDate(formatedDate);

        }

        /* 并不是所有的接口，都需要标签，作者信息 */
        if (isTag) {
            Long articleId = article.getId();
            articleVo.setTags(tagService.findTagsByArticleId(articleId));
        }
        if (isAuthor) {
            SysUser sysUser = sysUserService.findUserById(article.getAuthorId());
            String nickname = sysUser.getNickname();
            //防止出现空指针，imp层添加了判断条件
            articleVo.setAuthor(nickname);
        }

        //返回null了啊！！！！！！！！！ return null;
        return articleVo;
    }


}
