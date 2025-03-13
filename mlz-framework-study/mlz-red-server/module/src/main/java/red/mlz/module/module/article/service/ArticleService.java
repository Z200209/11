package red.mlz.module.module.article.service;

import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import red.mlz.module.module.article.entity.Article;
import red.mlz.module.module.article.mapper.ArticleMapper;
import red.mlz.module.module.article.request.ArticleContentDto;
import red.mlz.module.module.user.service.BaseUserService;
import red.mlz.module.utils.BaseUtils;

import javax.annotation.Resource;
import java.math.BigInteger;
import java.util.List;

@Service
public class ArticleService {
    @Resource
    private ArticleMapper mapper;


    private BaseUserService baseUserService;

    @Autowired
    public ArticleService(BaseUserService baseUserService){
        this.baseUserService = baseUserService;
    }

    public Article getById(BigInteger id) {
        return mapper.getById(id);
    }

    public Article extractById(BigInteger id) {
        return mapper.extractById(id);
    }

    public void insert(Article article) {
        mapper.insert(article);
    }

    public int update(Article article) {
        return mapper.update(article);
    }

    public void delete(BigInteger id) {
        mapper.delete(id, BaseUtils.currentSeconds());
    }


    public List<Article> getArticlesForConsole(int page, int pageSize, String title) {
        int begin = (page - 1) * pageSize;
        return mapper.getArticlesForConsole(begin, pageSize, "desc", title);
    }

    public int getArticlesTotalForConsole(String title) {
        return mapper.getArticlesTotalForConsole(title);
    }


    // for app list
    // key  can be  article title
    // or key can be username
    public List<Article> getArticleList(int page, int pageSize, String key) {
        int begin = (page - 1) * pageSize;
        String userIds = null;
        if(!BaseUtils.isEmpty(key)){
            userIds = baseUserService.getUserIdsForSearch(key);
        }
        return mapper.getArticleList(begin, pageSize, key, userIds);
    }

    public BigInteger editArticle(BigInteger articleId,BigInteger relateShopId,String title,String coverImage,String tags,String content,Integer weight){
        try {
            List<ArticleContentDto> checkContents = JSON.parseArray(content, ArticleContentDto.class);
            for(ArticleContentDto checkContent:checkContents){
                if(!ArticleDefine.isArticleContentType(checkContent.getType())){
                    throw new RuntimeException("article content is error");
                }
            }
        } catch (Exception cause) {
            // ignores
            throw new RuntimeException("article content is error");
        }
        if(BaseUtils.isEmpty(title) || BaseUtils.isEmpty(coverImage)){
            throw new RuntimeException("article title or coverImage is error");
        }

        Article article = new Article();
        article.setRelateUserId(relateShopId);
        article.setContent(content);
        article.setCoverImage(coverImage);
        article.setTitle(title);
        article.setIsDeleted(0);
        if(!BaseUtils.isEmpty(weight)) {
            article.setWeight(weight);
        }
        if(BaseUtils.isEmpty(tags)){
            tags = null;
        }
        article.setTags(tags);
        if(BaseUtils.isEmpty(articleId)){
            article.setCreateTime(BaseUtils.currentSeconds());
            insert(article);
        }else{
            Article old = getById(articleId);
            if(BaseUtils.isEmpty(old)){
                throw new RuntimeException("edit article error, old not exist");
            }
            article.setId(articleId);
            update(article);
        }
        return article.getId();
    }

    public List<Article> getAll(int page, int pageSize) {
        int begin = (page - 1) * pageSize;
        return mapper.getAll(begin, pageSize);
    }

}
