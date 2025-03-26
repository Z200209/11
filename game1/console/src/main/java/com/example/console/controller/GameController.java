package com.example.console.controller;

import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.example.module.utils.BaseUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.console.annotations.VerifiedUser;
import com.example.console.domain.DetailVO;
import com.example.console.domain.GameListVO;
import com.example.module.entity.Game;
import com.example.module.entity.Type;
import com.example.module.entity.User;
import com.example.module.service.GameService;
import com.example.module.service.TypeService;
import com.example.module.service.UserService;
import com.example.module.utils.Response;
import lombok.extern.slf4j.Slf4j;

/**
 * 游戏控制器
 */
@Slf4j
@RestController
@RequestMapping("/game/console")
public class GameController {

    @Autowired
    private GameService gameService;
    
    @Autowired
    private TypeService typeService;


    /**
     * 创建游戏
     */
    @PostMapping("/create")
    public Response createGame(
            @VerifiedUser User loginUser,
            @RequestParam(name = "typeId", required = false) BigInteger typeId,
            @RequestParam(name = "gameName") String gameName,
            @RequestParam(name = "price") Float price,
            @RequestParam(name = "gameIntroduction") String gameIntroduction,
            @RequestParam(name = "gameDate") String gameDate,
            @RequestParam(name = "gamePublisher") String gamePublisher,
            @RequestParam(name = "images") String images) {
        
        // 验证用户是否登录
        if (loginUser == null) {
            log.warn("未登录用户尝试创建游戏");
            return new Response(1002);
        }
        
        try {
            gameName = gameName.trim();
            gamePublisher = gamePublisher.trim();

            // 参数验证
            if (gameName.isEmpty()) {
                log.info("游戏名称不能为空字符串");
                return new Response(4005);
            }
            
            if (price < 0) {
                log.info("游戏价格不能为负数");
                return new Response(4005);
            }

            if (gameIntroduction == null) {
                log.info("游戏介绍不能为空字符串");
                return new Response(4005);
            }
            
            // 解析游戏介绍JSON
            JSONObject gameIntroductionObj;
            try {
                gameIntroductionObj = JSON.parseObject(gameIntroduction);
            } catch (Exception e) {
                log.info("游戏介绍JSON解析失败: {}", e.getMessage());
                return new Response(4005);
            }
            
            // 验证blocks数组
            JSONArray blocksArray = gameIntroductionObj.getJSONArray("blocks");
            if (blocksArray == null || blocksArray.isEmpty()) {
                log.info("游戏介绍blocks数组为空");
                return new Response(4005);
            }
            
            // 创建游戏
            BigInteger gameId = gameService.edit(null, gameName, price, gameIntroduction, gameDate, gamePublisher, images, typeId);
            
            return new Response<>(1001, "创建成功，ID: " + gameId);
        } catch (Exception e) {
            log.error("创建游戏失败", e);
            return new Response(1002);
        }
    }

    /**
     * 更新游戏信息
     */
    @RequestMapping("/update")
    public Response updateGame(
            @VerifiedUser User loginUser,
            @RequestParam(name = "gameId") BigInteger gameId,
            @RequestParam(name = "typeId") BigInteger typeId,
            @RequestParam(name = "gameName") String gameName,
            @RequestParam(name = "price") Float price,
            @RequestParam(name = "gameIntroduction") String gameIntroduction,
            @RequestParam(name = "gameDate") String gameDate,
            @RequestParam(name = "gamePublisher") String gamePublisher,
            @RequestParam(name = "images") String images) {
        
        // 验证用户是否登录
        if (loginUser == null) {
            log.warn("未登录用户尝试更新游戏");
            return new Response(1002);
        }
        
        try {
            gameName = gameName.trim();
            gamePublisher = gamePublisher.trim();

            // 参数验证
            if (gameName.isEmpty()) {
                log.info("游戏名称不能为空字符串");
                return new Response(4005);
            }
            
            if (price < 0) {
                log.info("游戏价格不能为负数");
                return new Response(4005);
            }

            if (gameIntroduction == null) {
                log.info("游戏介绍不能为空字符串");
                return new Response(4005);
            }
            
            // 解析游戏介绍JSON
            JSONObject gameIntroductionObj;
            try {
                gameIntroductionObj = JSON.parseObject(gameIntroduction);
            } catch (Exception e) {
                log.info("游戏介绍JSON解析失败: {}", e.getMessage());
                return new Response(4005);
            }
            
            // 验证blocks数组
            JSONArray blocksArray = gameIntroductionObj.getJSONArray("blocks");
            if (blocksArray == null || blocksArray.isEmpty()) {
                log.info("游戏介绍blocks数组为空");
                return new Response(4005);
            }
            
            // 检查游戏是否存在
            Game existingGame = gameService.getById(gameId);
            if (existingGame == null) {
                log.info("未找到游戏: {}", gameId);
                return new Response(4006);
            }
            
            // 更新游戏
            gameService.edit(gameId, gameName, price, gameIntroduction, gameDate, gamePublisher, images, typeId);
            
            return new Response(1001);
        } catch (Exception e) {
            log.error("更新游戏失败", e);
            return new Response(1002);
        }
    }

    /**
     * 获取游戏列表
     */
    @RequestMapping("/list")
    public Response gameList(
            @VerifiedUser User loginUser,
            @RequestParam(name = "page", defaultValue = "1") Integer page,
            @RequestParam(name = "keyword", required = false) String keyword,
            @RequestParam(name = "typeId", required = false) BigInteger typeId) {
        
        // 验证用户是否登录
        if (loginUser == null) {
            log.warn("未登录用户尝试获取游戏列表");
            return new Response(1002);
        }
        
        try {
            int pageSize = 10;
            
            // 获取游戏列表
            List<Game> gameList = gameService.getAllGame(page, pageSize, keyword, typeId);
            Integer total = gameService.getTotalCount(keyword);
            
            // 收集类型ID
            Set<BigInteger> typeIdSet = new HashSet<>();
            for (Game game : gameList) {
                BigInteger tid = game.getTypeId();
                if (tid != null) {
                    typeIdSet.add(tid);
                }
            }
            
            // 获取类型信息
            Map<BigInteger, String> typeMap = new HashMap<>();
            if (!typeIdSet.isEmpty()) {
                List<Type> types = typeService.getTypeByIds(typeIdSet);
                for (Type type : types) {
                    typeMap.put(type.getId(), type.getTypeName());
                }
            }
            
            // 构建游戏列表数据
            List<GameListVO> gameVOList = new ArrayList<>();
            for (Game game : gameList) {
                String typeName = typeMap.get(game.getTypeId());
                String formattedCreateTime = BaseUtils.timeStamp2DateGMT(game.getCreateTime(), "yyyy-MM-dd HH:mm:ss");
                String formattedUpdateTime = BaseUtils.timeStamp2DateGMT(game.getUpdateTime(), "yyyy-MM-dd HH:mm:ss");
                
                GameListVO gameVO = new GameListVO()
                        .setGameId(game.getId())
                        .setGameName(game.getGameName())
                        .setTypeName(typeName)
                        .setPrice(game.getPrice())
                        .setCreateTime(formattedCreateTime)
                        .setUpdateTime(formattedUpdateTime);
                
                gameVOList.add(gameVO);
            }
            
            // 构建最终响应对象
            Map<String, Object> result = new HashMap<>();
            result.put("list", gameVOList);
            result.put("total", total);
            result.put("page", page);
            result.put("pageSize", pageSize);
            
            return new Response(1001, result);
        } catch (Exception e) {
            log.error("获取游戏列表失败: {}", e.getMessage(), e);
            return new Response(1002);
        }
    }



    /**
     * 获取游戏详情
     */
    @RequestMapping("/info")
    public Response gameInfo(
            @VerifiedUser User loginUser,
            @RequestParam(name = "gameId") BigInteger gameId) {
        
        // 验证用户是否登录
        if (loginUser == null) {
            log.warn("未登录用户尝试获取游戏详情");
            return new Response(1002);
        }
        
        try {
            Game game = gameService.getById(gameId);
            if (game == null) {
                log.info("未找到游戏信息：{}", gameId);
                return new Response(1002);
            }
            
            String formattedCreateTime = BaseUtils.timeStamp2DateGMT(game.getCreateTime(), "yyyy-MM-dd HH:mm:ss");
            String formattedUpdateTime = BaseUtils.timeStamp2DateGMT(game.getUpdateTime(), "yyyy-MM-dd HH:mm:ss");
            
            BigInteger typeId = game.getTypeId();
            Type type = typeService.getById(typeId);
            if (type == null) {
                log.info("未找到游戏类型：{}", typeId);
                return new Response(1002);
            }
            
            String typeName = type.getTypeName();
            String typeImage = type.getImage();
            
            DetailVO detailVO = new DetailVO()
                    .setTypeName(typeName)
                    .setTypeImage(typeImage)
                    .setGameId(game.getId())
                    .setGameName(game.getGameName())
                    .setPrice(game.getPrice())
                    .setGameIntroduction(game.getGameIntroduction())
                    .setGameDate(game.getGameDate())
                    .setGamePublisher(game.getGamePublisher())
                    .setCreateTime(formattedCreateTime)
                    .setUpdateTime(formattedUpdateTime);
            
            // 处理图片列表
            if (game.getImages() != null && !game.getImages().isEmpty()) {
                detailVO.setImages(Arrays.asList(game.getImages().split("\\$")));
            }
            
            return new Response(1001, detailVO);
        } catch (Exception e) {
            log.error("获取游戏详情失败", e);
            return new Response(1002);
        }
    }

    /**
     * 删除游戏
     */
    @RequestMapping("/delete")
    public Response deleteGame(
            @VerifiedUser User loginUser,
            @RequestParam(name = "gameId") BigInteger gameId) {
        
        // 验证用户是否登录
        if (loginUser == null) {
            log.warn("未登录用户尝试删除游戏");
            return new Response(1002);
        }
        
        try {
            // 检查游戏是否存在
            Game game = gameService.getById(gameId);
            if (game == null) {
                log.info("未找到游戏: {}", gameId);
                return new Response<>(4004);
            }
            
            // 删除游戏
            int result = gameService.delete(gameId);
            if (result == 1) {
                return new Response(1001);
            } else {
                return new Response(1002);
            }
        } catch (Exception e) {
            log.error("删除游戏失败", e);
            return new Response(1002);
        }
    }
    

}













