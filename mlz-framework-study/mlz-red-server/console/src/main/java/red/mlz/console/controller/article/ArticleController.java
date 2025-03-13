package red.mlz.console.controller.article;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import red.mlz.console.annotations.VerifiedUser;
import red.mlz.console.domain.BaseContentValueVo;
import red.mlz.console.domain.BaseListVo;
import red.mlz.console.domain.article.ArticleInfoVo;
import red.mlz.console.domain.article.ArticleListVo;
import red.mlz.module.module.article.entity.Article;
import red.mlz.module.module.article.service.ArticleService;
import red.mlz.module.module.user.entity.User;
import red.mlz.module.module.user.service.BaseUserService;
import red.mlz.module.utils.BaseUtils;
import red.mlz.module.utils.Response;
import red.mlz.module.utils.SpringUtils;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
@Slf4j
public class ArticleController {
    @Autowired
    private ArticleService articleService;

    @Autowired
    private BaseUserService baseUserService;


    @RequestMapping("/article/list")
    public Response articleList(@VerifiedUser User loginUser,
                                @RequestParam(name = "title", required = false) String title,
                                @RequestParam(name = "page", required = false, defaultValue = "1") int page) {
        if (BaseUtils.isEmpty(loginUser)) {
            return new Response(1002);
        }
        String pageSize = SpringUtils.getProperty("application.pagesize");
        List<Article> articles = articleService.getArticlesForConsole(page, Integer.parseInt(pageSize), title);
        int total = articleService.getArticlesTotalForConsole(title);

        BaseListVo result = new BaseListVo();
        result.setTotal(total);
        result.setPageSize(Integer.valueOf(pageSize));

        List<ArticleListVo> list = new ArrayList<>();

        for (Article article : articles) {
            ArticleListVo entry = new ArticleListVo();
            entry.setArticleId(article.getId());
            entry.setTitle(article.getTitle());
            entry.setCoverImage(article.getCoverImage());
            entry.setCreateTime(BaseUtils.timeStamp2Date(article.getCreateTime()));
            list.add(entry);
        }

        result.setList(list);
        return new Response(1001, result);
    }


    @RequestMapping("/article/info")
    public Response articleInfo(@VerifiedUser User loginUser,
                                @RequestParam(name = "articleId") BigInteger articleId) {
        if (BaseUtils.isEmpty(loginUser)) {
            return new Response(1002);
        }
        Article article = articleService.getById(articleId);
        if (BaseUtils.isEmpty(article)) {
            return new Response(4004);
        }


        ArticleInfoVo entry = new ArticleInfoVo();
        entry.setArticleId(article.getId());
        entry.setUserId(article.getRelateUserId());
        entry.setTitle(article.getTitle());
        entry.setCoverImage(article.getCoverImage());
        entry.setCreateTime(BaseUtils.timeStamp2Date(article.getCreateTime()));
        if(!BaseUtils.isEmpty(article.getTags())){
            entry.setTags(Arrays.asList(article.getTags().split("\\$")));
        }
        try {
            List<BaseContentValueVo> contents = JSON.parseArray(article.getContent(), BaseContentValueVo.class);
            entry.setContent(contents);
        } catch (Exception cause) {
            // ignores
            return new Response(4004);
        }
        entry.setWeight(article.getWeight());

        return new Response(1001, entry);
    }

    @RequestMapping("/article/create")
    public Response articleCreate(@VerifiedUser User loginUser,
                                  @RequestParam(name = "userId") BigInteger relateUserId,
                                  @RequestParam(name = "title") String title,
                                  @RequestParam(name = "coverImage") String coverImage,
                                  @RequestParam(name = "tags", required = false) String tags,
                                  @RequestParam(name = "content") String content,
                                  @RequestParam(name = "weight", required = false) Integer weight) {
        if (BaseUtils.isEmpty(loginUser)) {
            return new Response(1002);
        }
        //parameters check
        title = title.trim();
        if (BaseUtils.isEmpty(title)) {
            return new Response(3051);
        }
        if (!BaseUtils.isEmpty(relateUserId)){
            User user = baseUserService.getById(relateUserId);
            if (BaseUtils.isEmpty(user)){
                return new Response(3052);
            }
        }

        try {
            articleService.editArticle(null,relateUserId,title,coverImage,tags,content,weight);
            return new Response(1001);
        } catch (Exception exception) {
            return new Response(4004);

        }
    }

    @RequestMapping("/article/modify")
    public Response articleModify(@VerifiedUser User loginUser,
                                  @RequestParam(name = "articleId") BigInteger articleId,
                                  @RequestParam(name = "userId") BigInteger relateUserId,
                                  @RequestParam(name = "title") String title,
                                  @RequestParam(name = "coverImage") String coverImage,
                                  @RequestParam(name = "tags", required = false) String tags,
                                  @RequestParam(name = "content") String content,
                                  @RequestParam(name = "weight", required = false) Integer weight) {
        if (BaseUtils.isEmpty(loginUser)) {
            return new Response(1002);
        }
        //parameters check
        title = title.trim();
        if(BaseUtils.isEmpty(title)){
            return new Response(3051);
        }
        if (!BaseUtils.isEmpty(relateUserId)){
            User user = baseUserService.getById(relateUserId);
            if (BaseUtils.isEmpty(user)){
                return new Response(3052);
            }
        }
        Article old = articleService.getById(articleId);
        if(BaseUtils.isEmpty(old)){
            return new Response(4004);
        }

        try {
            articleService.editArticle(articleId,relateUserId,title,coverImage,tags,content,weight);
            return new Response(1001);
        } catch (Exception exception) {
            return new Response(4004);
        }
    }


    @RequestMapping("/article/delete")
    public Response articleDelete(@VerifiedUser User loginUser,
                                  @RequestParam(name = "articleId") BigInteger articleId) {
        if (BaseUtils.isEmpty(loginUser)) {
            return new Response(1002);
        }
        if (BaseUtils.isEmpty(articleId)) {
            return new Response(4004);
        }
        try {
            articleService.delete(articleId);
            return new Response(1001);
        } catch (Exception exception) {
            return new Response(4004);
        }
    }

}
