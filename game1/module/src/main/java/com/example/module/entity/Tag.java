package com.example.module.entity;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigInteger;

@Data
@Accessors(chain = true)
public class Tag {
    // 主键ID
    private BigInteger id;
    // 标签名称
    private String name;
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

    public Tag setId(BigInteger id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public Tag setName(String name) {
        this.name = name;
        return this;
    }

    public Integer getCreateTime() {
        return createTime;
    }

    public Tag setCreateTime(Integer createTime) {
        this.createTime = createTime;
        return this;
    }

    public Integer getUpdateTime() {
        return updateTime;
    }

    public Tag setUpdateTime(Integer updateTime) {
        this.updateTime = updateTime;
        return this;
    }

    public Integer getIsDeleted() {
        return isDeleted;
    }

    public Tag setIsDeleted(Integer isDeleted) {
        this.isDeleted = isDeleted;
        return this;
    }
}
