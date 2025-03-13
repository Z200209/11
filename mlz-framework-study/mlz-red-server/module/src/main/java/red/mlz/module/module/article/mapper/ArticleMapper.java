package red.mlz.module.module.article.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import red.mlz.module.module.article.entity.Article;

import java.math.BigInteger;
import java.util.List;

/**
 * <p>
 * 文章表 Mapper 接口
 * </p>
 *
 * @author ancientnine
 * @since 2022-10-24
 */
@Mapper
public interface ArticleMapper{
    @Select("select * from article WHERE id=#{id} and is_deleted = 0")
    Article getById(@Param("id") BigInteger id);

    @Select("select * from article WHERE id=#{id}")
    Article extractById(@Param("id") BigInteger id);

    int update(@Param("article") Article article);

    int insert(@Param("article") Article article);

    @Update("update article set is_deleted=1, update_time=#{time} where id=#{id} limit 1")
    void delete(@Param("id") BigInteger id, @Param("time") Integer time);

    List<Article> getArticlesForConsole(@Param("begin") int begin, @Param("size") int size, @Param("orderBy") String orderBy,
                                                      @Param("title") String title);

    int getArticlesTotalForConsole(@Param("title") String title);

    List<Article> getArticleList(@Param("begin") int begin, @Param("size") int size,
                                        @Param("key") String key,@Param("userIds") String userIds);

    @Select("select * from article WHERE is_deleted = 0 order by weight desc limit #{begin}, #{size}")
    List<Article> getAll(@Param("begin") int begin, @Param("size") int size);
}
