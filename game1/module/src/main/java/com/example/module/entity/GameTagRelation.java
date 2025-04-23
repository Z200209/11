package com.example.module.entity;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigInteger;

@Data
@Accessors(chain = true)
public class GameTagRelation {
    // 主键ID
    private BigInteger id;
    // 游戏ID
    private BigInteger gameId;
    // 标签ID
    private BigInteger tagId;
    // 创建时间
    private Integer createTime;
    // 更新时间
    private Integer updateTime;
    // 是否删除 1-是 0-否
    private Integer isDeleted;
    
    // 手动添加 getter 和 setter 方法，避免编译错误
    public BigInteger getId() {
        return id;
    }

    public GameTagRelation setId(BigInteger id) {
        this.id = id;
        return this;
    }

    public BigInteger getGameId() {
        return gameId;
    }

    public GameTagRelation setGameId(BigInteger gameId) {
        this.gameId = gameId;
        return this;
    }

    public BigInteger getTagId() {
        return tagId;
    }

    public GameTagRelation setTagId(BigInteger tagId) {
        this.tagId = tagId;
        return this;
    }

    public Integer getCreateTime() {
        return createTime;
    }

    public GameTagRelation setCreateTime(Integer createTime) {
        this.createTime = createTime;
        return this;
    }

    public Integer getUpdateTime() {
        return updateTime;
    }

    public GameTagRelation setUpdateTime(Integer updateTime) {
        this.updateTime = updateTime;
        return this;
    }

    public Integer getIsDeleted() {
        return isDeleted;
    }

    public GameTagRelation setIsDeleted(Integer isDeleted) {
        this.isDeleted = isDeleted;
        return this;
    }
} 