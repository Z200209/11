package red.mlz.app.controller.article;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import red.mlz.app.domain.BaseContentValueVo;
import red.mlz.app.domain.BaseListVo;
import red.mlz.app.domain.article.ArticleInfoVo;
import red.mlz.app.domain.article.ArticleListVo;
import red.mlz.app.domain.article.ArticleWpVo;
import red.mlz.module.module.article.entity.Article;
import red.mlz.module.module.article.service.ArticleService;
import red.mlz.module.module.user.entity.User;
import red.mlz.module.module.user.service.BaseUserService;
import red.mlz.module.utils.BaseUtils;
import red.mlz.module.utils.Response;
import red.mlz.module.utils.SpringUtils;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@RestController
@Slf4j
public class ArticleController {

    @Autowired
    private ArticleService articleService;

    @Autowired
    private BaseUserService baseUserService;

    @RequestMapping("/article/list")
    public Response articleList(@RequestParam(name = "wp", required = false) String wp,
                                @RequestParam(name = "key", required = false) String key) {
        ArticleWpVo baseWp = new ArticleWpVo();
        if(!BaseUtils.isEmpty(wp)){
            byte[] bytes = Base64.getUrlDecoder().decode(wp.getBytes(StandardCharsets.UTF_8));
            String realWp = new String(bytes, StandardCharsets.UTF_8);
            try{
                baseWp = JSON.parseObject(realWp, ArticleWpVo.class);
            }catch (Exception e){
                return new Response(4004);
            }
        }else{
            baseWp.setPage(1);
            baseWp.setKey(key);
        }

        String pageSize = SpringUtils.getProperty("application.pagesize");
        List<Article> articleList = articleService.getArticleList(baseWp.getPage(), Integer.parseInt(pageSize),baseWp.getKey());

        BaseListVo result = new BaseListVo();
        result.setIsEnd(Integer.parseInt(pageSize) > articleList.size());
        baseWp.setPage(baseWp.getPage()+1);
        String jsonWp = JSONObject.toJSONString(baseWp);
        byte[] encodeWp = Base64.getUrlEncoder().encode(jsonWp.getBytes(StandardCharsets.UTF_8));
        result.setWp(new String(encodeWp, StandardCharsets.UTF_8).trim());

        List<ArticleListVo> list = new ArrayList<>();
        if (articleList.size() > 0) {

            List<BigInteger> userIds = articleList.stream().map(Article::getRelateUserId).collect(Collectors.toList());
            List<User> userList = baseUserService.getByIds(userIds);
            Map<BigInteger, String> userNameMap = userList.stream().collect(Collectors.toMap(User::getId, User::getUsername));

            for(Article article:articleList){
                BigInteger userId = article.getRelateUserId();
                if (userNameMap.containsKey(userId)) {
                    ArticleListVo entry = new ArticleListVo();
                    entry.setArticleId(article.getId());
                    entry.setImage(article.getCoverImage());
                    entry.setTitle(article.getTitle());
                    entry.setAuthorName(userNameMap.get(userId));
                    list.add(entry);
                }
            }
        }
        result.setList(list);

        return new Response(1001,result);
    }

    @RequestMapping("/article/info")
    public Response articleInfo(@RequestParam(name = "articleId") BigInteger articleId) {
        Article article = articleService.getById(articleId);
        if (BaseUtils.isEmpty(article)) {
            return new Response(4004);
        }


        ArticleInfoVo entry = new ArticleInfoVo();

        User author = baseUserService.getById(article.getRelateUserId());
        if(!BaseUtils.isEmpty(author)){
            entry.setAuthorName(author.getUsername());
        }
        entry.setTitle(article.getTitle());
        entry.setCreateTime(BaseUtils.timeStamp2Date(article.getCreateTime()));
        try {
            List<BaseContentValueVo> contents = JSON.parseArray(article.getContent(), BaseContentValueVo.class);
            entry.setContent(contents);
        } catch (Exception cause) {
            // ignores
            return new Response(4004);
        }

        return new Response(1001, entry);
    }
}
