package com.example.module.mapper;

import com.example.module.entity.Game;
import com.example.module.entity.GameDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.math.BigInteger;
import java.util.List;

@Mapper
public interface GameMapper  {
    @Select("SELECT g.*, t.type_name,t.image AS typeImage FROM game g LEFT JOIN type t ON g.type_id = t.id WHERE g.id = #{id} AND g.is_deleted = 0")
    GameDTO getById(BigInteger id);

    @Select("select * from game where id = #{id} ")
    Game extractById(BigInteger id);

    int insert(@Param("game") Game game);

    int update(@Param("game") Game game);

    @Update("update game set is_deleted = 1, update_time=#{time} where id = #{id} limit 1")
    int delete(@Param("id") BigInteger id, @Param("time") Integer time);


    List<GameDTO> getAll(@Param("offset") Integer offset, @Param("pageSize") Integer pageSize , @Param("keyword") String keyword, @Param("typeId") BigInteger typeId);

    int getTotalCount(@Param("keyword") String keyword);

    @Select("SELECT id from game where type_id = #{type_id} and is_deleted = 0")
    List<BigInteger> isExistByTypeId(@Param("type_id") BigInteger type_id);
}
