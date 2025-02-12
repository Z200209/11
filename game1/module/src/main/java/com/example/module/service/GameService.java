package com.example.module.service;

import com.example.module.entity.Game;
import com.example.module.entity.Type;
import com.example.module.mapper.GameMapper;
import com.example.module.mapper.TypeMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class GameService {

@Resource
private GameMapper mapper;
@Resource
private TypeMapper typeMapper;

/**
* 根据ID获取实体
*
* @param id 实体ID
* @return 实体对象
*/
public Game getById(BigInteger id) {
if (id == null) {
throw new IllegalArgumentException("ID 不能为空");
}
return mapper.getById(id);
}

/**
* 提取特定的实体信息（如果有特殊用途）
*
* @param id 实体ID
* @return 实体对象
*/
public Game extractById(BigInteger id) {
if (id == null) {
throw new IllegalArgumentException("ID 不能为空");
}
return mapper.extractById(id);
}

/**
* 插入新的实体记录
*
* @param entity 实体对象
* @return 影响的行数
*/
public int insert(Game entity) {
if (entity == null) {
throw new IllegalArgumentException("Game 实体不能为空");
}
return mapper.insert(entity);
}

/**
* 更新实体记录
*
* @param entity 实体对象
* @return 影响的行数
*/
public int update(Game entity) {
if (entity == null) {
throw new IllegalArgumentException("Game 实体不能为空");
}
return mapper.update(entity);
}

/**
* 删除实体记录（逻辑删除）
*
* @param id 实体ID
* @return 影响的行数
*/
public int delete(BigInteger id) {
if (id == null) {
throw new IllegalArgumentException("ID 不能为空");
}
int time = (int) (System.currentTimeMillis() / 1000);
return mapper.delete(id, time);
}


    public BigInteger edit (BigInteger id, String gameName, Float price, String gameIntroduction, String gameDate, String gamePublisher, String images, BigInteger typeId) {
        if (gameName == null || gameName.isEmpty()) {
            throw new RuntimeException("gameName 不能为空");
        }
        if (price == null || price < 0) {
            throw new RuntimeException("price 不能为空");
        }
        if (gameIntroduction == null || gameIntroduction.isEmpty()) {
            throw new RuntimeException("gameIntroduction 不能为空");
        }
        if (gameDate == null || gameDate.isEmpty()) {
            throw new RuntimeException("gameDate 不能为空");
        }
        if (gamePublisher == null || gamePublisher.isEmpty()) {
            throw new RuntimeException("gamePublisher 不能为空");
        }
        if (images == null || images.isEmpty()) {
            throw new RuntimeException("images 不能为空");
        }
        if(typeId != null) {
            Type type = typeMapper.getById(typeId);
            if (type == null || type.getIsDeleted() == 1) {
                throw new IllegalArgumentException("分类不存在或已被删除");
            }
        }

        int time = (int) (System.currentTimeMillis() / 1000);
        Game game = new Game();
        game.setGameName(gameName);
        game.setPrice(price);
        game.setGameIntroduction(gameIntroduction);
        game.setGameDate(gameDate);
        game.setGamePublisher(gamePublisher);
        game.setImages(images);
        game.setUpdateTime(time);
        game.setTypeId(typeId);

        if (id == null){
            game.setCreateTime(time);
            game.setIsDeleted(0);
            int result =  insert(game);
            if (result == 0){
                throw new RuntimeException("插入失败");
            }

        }
        else {
            game.setId(id);
            int result = update(game);
            if (result == 0){
                throw new RuntimeException("id不存在");
            }

        }
        return game.getId();

    }


}