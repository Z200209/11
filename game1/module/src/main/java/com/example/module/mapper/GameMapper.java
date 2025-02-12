
package com.example.module.mapper;

import com.example.module.entity.Game;
import org.apache.ibatis.annotations.*;
import java.math.BigInteger;

@Mapper
public interface GameMapper {

// 根据ID查询操作
@Select("SELECT * FROM game WHERE id = #{gameId} AND is_deleted=0")
Game getById(BigInteger gameId);

// 根据ID提取操作
@Select("SELECT * FROM game WHERE id = #{gameId}")
Game extractById(BigInteger gameId);

// 插入操作
int insert(@Param("game")Game game);

// 更新操作
int update(@Param("game")Game game);

// 删除操作
@Update("UPDATE game SET update_time = #{updateTime} , is_deleted = 1 WHERE game_id = #{gameId}")
int delete(@Param("gameId") BigInteger gameId,@Param("updateTime") Integer updateTime);

}
