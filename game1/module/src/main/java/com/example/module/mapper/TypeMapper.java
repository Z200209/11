
package com.example.module.mapper;

import com.example.module.entity.Type;
import org.apache.ibatis.annotations.*;
import java.math.BigInteger;

@Mapper
public interface TypeMapper {

// 根据ID查询操作
@Select("SELECT * FROM type WHERE id = #{typeId} AND is_deleted=0")
Type getById(BigInteger typeId);

// 根据ID提取操作
@Select("SELECT * FROM type WHERE id = #{typeId}")
Type extractById(BigInteger typeId);

// 插入操作
int insert(@Param("type")Type type);

// 更新操作
int update(@Param("type")Type type);

// 删除操作
@Update("UPDATE type SET update_time = #{updateTime} , is_deleted = 1 WHERE type_id = #{typeId}")
int delete(@Param("typeId") BigInteger typeId,@Param("updateTime") Integer updateTime);

}
