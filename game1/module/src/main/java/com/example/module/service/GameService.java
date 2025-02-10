package com.example.module.service;


import com.example.module.entity.Game;
import com.example.module.entity.GameDTO;
import com.example.module.entity.Type;
import com.example.module.mapper.GameMapper;
import com.example.module.mapper.TypeMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.List;

@Slf4j
@Service
public class GameService {

    @Resource
    private GameMapper mapper;
    @Resource
    private TypeMapper typeMapper;
    public GameDTO getById(BigInteger id) {
        return mapper.getById(id);
    }
    public Game extractById(BigInteger id) {
        return mapper.extractById(id);
    }
    public int insert(Game game) {
        return mapper.insert(game);
    }
    public int update(Game game) {
        return mapper.update(game);
    }
    public int delete(BigInteger id) {
        int time = (int) (System.currentTimeMillis() / 1000);
        if (id == null){
            throw new RuntimeException("id 不能为空");
        }
        return mapper.delete(id, time);
    }


    public List<GameDTO> getAllGame(Integer page, Integer pageSize, String keyword, BigInteger typeId) {
        
        return mapper.getAll((page-1) * pageSize, pageSize, keyword,typeId);
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



    public Integer getTotalCount(String keyword) {
        return mapper.getTotalCount(keyword);
    }

    public void updateTypeIdByOldId(BigInteger oldid, BigInteger newid) {
        int time = (int) (System.currentTimeMillis() / 1000);
        if(oldid == null){
            throw new RuntimeException("oldid 不能为空");
        }
        mapper.updateTypeIdByOldId(oldid, newid, time);
    }

}
