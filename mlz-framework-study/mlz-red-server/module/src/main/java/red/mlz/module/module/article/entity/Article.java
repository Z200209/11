package red.mlz.module.module.article.entity;

import lombok.Data;
import lombok.experimental.Accessors;
import red.mlz.module.utils.BaseUtils;

import java.math.BigInteger;

/**
 * <p>
 * 文章表
 * </p>
 *
 * @author ancientnine
 * @since 2022-10-24
 */
@Data
@Accessors(chain = true)
public class Article{

    private BigInteger id;

    private BigInteger relateUserId;

    /**
     * 标题
     */
    private String title;

    /**
     * 封面
     */
    private String coverImage;

    /**
     * 内容
     */
    private String content;

    /**
     * 标签
     */
    private String tags;

    /**
     * 权重
     */
    private Integer weight;

    private Integer createTime;

    private Integer updateTime= BaseUtils.currentSeconds();

    private Integer isDeleted;


}
