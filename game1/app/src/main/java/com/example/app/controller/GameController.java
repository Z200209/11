package com.example.app.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.app.annotations.VerifiedUser;
import com.example.app.domain.*;
import com.example.module.entity.Game;
import com.example.module.entity.Type;
import com.example.module.entity.User;
import com.example.module.entity.Wp;
import com.example.module.service.GameService;
import com.example.module.service.TypeService;
import com.example.module.utils.BaseUtils;
import com.example.module.utils.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 游戏控制器
 */
@Slf4j
@RestController
@RequestMapping("/game/app")
public class GameController {

    @Autowired
    private GameService gameService;
    
    @Autowired
    private TypeService typeService;

    /**
     * 获取游戏详情
     */
    @RequestMapping("/info")
    public Response gameInfo(@VerifiedUser User loginUser,
                                        @RequestParam(name = "gameId") BigInteger gameId) {
        // 验证用户是否登录
        if (loginUser == null) {
            log.warn("用户未登录");
            return new Response<>(1002);
        }
        
        log.info("用户 {} 请求游戏详情，gameId: {}", loginUser.getId(), gameId);
        
        Game game = gameService.getById(gameId);
        if (game == null) {
            log.info("未找到游戏信息：{}", gameId);
            return new Response<>(4004);
        }
        
        Type type = null;
        String typeName = null;
        String typeImage = null;
        
        if (game.getTypeId() != null) {
            type = typeService.getById(game.getTypeId());
            if (type != null) {
                typeName = type.getTypeName();
                typeImage = type.getImage();
            }
        }
        
        String gameIntroductionJson = game.getGameIntroduction();
        IntroductionListVO introductionListVO = new IntroductionListVO();
        List<IntroductionListVO.Block> contentBlocks = new ArrayList<>();

        // 解析游戏介绍JSON
        if (gameIntroductionJson != null && !gameIntroductionJson.isEmpty()) {
            try {
                JSONObject gameIntroductionObj = JSON.parseObject(gameIntroductionJson);
                JSONArray blocksArray = gameIntroductionObj.getJSONArray("blocks");
                if (blocksArray != null) {
                    for (int i = 0; i < blocksArray.size(); i++) {
                        JSONObject blockObj = blocksArray.getJSONObject(i);
                        IntroductionListVO.Block contentBlock = new IntroductionListVO.Block();
                        contentBlock.setOrder(blockObj.getInteger("order"));
                        contentBlock.setType(IntroductionType.valueOf(blockObj.getString("type").toUpperCase()));
                        contentBlock.setContent(blockObj.getString("content"));
                        contentBlocks.add(contentBlock);
                    }
                }
            } catch (Exception e) {
                log.error("解析游戏介绍JSON失败: {}", e.getMessage());
            }
        }
        
        introductionListVO.setBlocks(contentBlocks);
        
        GameInfoVO gameInfo = new GameInfoVO()
                .setGameId(game.getId())
                .setTypeName(typeName)
                .setTypeImage(typeImage)
                .setGameName(game.getGameName())
                .setPrice(game.getPrice())
                .setGameIntroduction(introductionListVO)
                .setGameDate(game.getGameDate())
                .setGamePublisher(game.getGamePublisher());
                
        // 将图片字符串按 "$" 拆分为列表
        if (game.getImages() != null && !game.getImages().isEmpty()) {
            gameInfo.setImages(Arrays.asList(game.getImages().split("\\$")));
        }

        return new Response<>(1001, gameInfo); // 返回成功
    }

    /**
     * 获取游戏列表
     */
    @RequestMapping("/list")
    public Response gameList(@VerifiedUser User loginUser,
                                        @RequestParam(name = "keyword", required=false) String keyword,
                                        @RequestParam(name = "typeId", required=false) BigInteger typeId,
                                        @RequestParam(name = "wp", required=false) String wp) {
        // 验证用户是否登录
        if (loginUser == null) {
            log.warn("用户未登录");
            return new Response<>(1002);
        }
        
        log.info("用户 {} 请求游戏列表，keyword: {}, typeId: {}", loginUser.getId(), keyword, typeId);

        int currentPageSize = 10;
        Integer currentPage;
        
        try {
            if (wp != null && !wp.isEmpty()) {
                byte[] bytes = Base64.getUrlDecoder().decode(wp);
                String json = new String(bytes, StandardCharsets.UTF_8);
                Wp receiveWp = JSON.parseObject(json, Wp.class);
                currentPage = receiveWp.getPage();
                
                if (currentPage == 1) {
                    return new Response<>(4005);
                }
                
                currentPageSize = receiveWp.getPageSize();
                keyword = receiveWp.getKeyword();
                typeId = receiveWp.getTypeId();
            } else {
                currentPage = 1;
            }
            
            List<Game> gameList = gameService.getAllGame(currentPage, currentPageSize, keyword, typeId);
            
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
            
            // 构建输出的wp对象
            Wp outputWp = new Wp();
            outputWp.setKeyword(keyword)
                    .setTypeId(typeId)
                    .setPage(currentPage + 1)
                    .setPageSize(currentPageSize);
            
            // 编码wp
            String encodeWp = Base64.getUrlEncoder().encodeToString(JSON.toJSONString(outputWp).getBytes(StandardCharsets.UTF_8));
            
            // 构建游戏列表数据
            List<GameVO> gameVOList = new ArrayList<>();
            for (Game game : gameList) {
                if (game.getTypeId() == null || game.getImages() == null || game.getImages().isEmpty()) {
                    log.info("游戏数据不完整，跳过：{}", game.getId());
                    continue;
                }
                
                String typeName = typeMap.get(game.getTypeId());
                if (typeName == null) {
                    log.info("未找到游戏类型名称：{}", game.getTypeId());
                    continue;
                }
                
                String image = game.getImages().split("\\$")[0];
                
                // 计算图片宽高比
                String regex = ".*_(\\d+)x(\\d+)\\.png";
                Pattern pattern = Pattern.compile(regex);
                Matcher matcher = pattern.matcher(image);
                float ar = 0;
                if (matcher.find()) {
                    int width = Integer.parseInt(matcher.group(1));
                    int height = Integer.parseInt(matcher.group(2));
                    ar = (float) width / height;
                }
                
                ImageVO imageVO = new ImageVO()
                        .setSrc(image)
                        .setAr(ar);
                
                GameVO gameVO = new GameVO()
                        .setGameId(game.getId())
                        .setGameName(game.getGameName())
                        .setTypeName(typeName)
                        .setImage(imageVO);
                
                gameVOList.add(gameVO);
            }
            
            // 构建最终响应对象
            GameListVO result = new GameListVO()
                    .setGameList(gameVOList)
                    .setWp(encodeWp);
            
            return new Response<>(1001, result); // 返回成功
        } catch (Exception e) {
            log.error("获取游戏列表失败: {}", e.getMessage(), e);
            return new Response<>(4004);
        }
    }
}




