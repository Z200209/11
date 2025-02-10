package com.example.module.mapper;

import com.example.module.entity.Type;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.math.BigInteger;
import java.util.List;

@Mapper
public interface TypeMapper {
    @Select("select * from type where id = #{id} and is_deleted = 0 ")
    Type getById(BigInteger id);

    @Select("select * from type where id = #{id} ")
    Type extractById(BigInteger id);

    int insert(@Param("type") Type type);
    int update(@Param("type") Type type);

    @Update("update type set is_deleted = 1, update_time=#{time} where id = #{id} limit 1")
    int delete(@Param("id")BigInteger id,@Param("time") Integer time);

    List<Type> getAll(@Param("offset") Integer offset, @Param("pageSize") Integer pageSize ,@Param("keyword") String keyword);

    int getTotalCount(@Param("keyword") String keyword);




}
