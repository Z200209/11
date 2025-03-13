package red.mlz.app.controller.game;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;

import red.mlz.app.annotations.VerifiedUser;
import red.mlz.app.domain.game.GameInfoVo;
import red.mlz.app.domain.game.GameListResponseVo;
import red.mlz.app.domain.game.GameListVo;
import red.mlz.app.domain.game.GameWpVo;
import red.mlz.app.domain.game.ImageVo;
import red.mlz.module.module.game.entity.Game;
import red.mlz.module.module.game.entity.Type;
import red.mlz.module.module.game.service.GameService;
import red.mlz.module.module.game.service.TypeService;
import red.mlz.module.module.user.entity.User;
import red.mlz.module.utils.BaseUtils;
import red.mlz.module.utils.Response;

@RestController
public class GameController {
    
    private static final Logger logger = LoggerFactory.getLogger(GameController.class);

    @Autowired
    private GameService gameService;
    
    @Autowired
    private TypeService typeService;

    @RequestMapping("/game/info")
    public Response gameInfo(@VerifiedUser User loginUser,
                            @RequestParam(name = "gameId") BigInteger gameId) {
        if (BaseUtils.isEmpty(loginUser)) {
            return new Response(1002); // 用户未登录
        }
        
        Game game = gameService.getById(gameId);
        if (BaseUtils.isEmpty(game)) {
            logger.info("未找到游戏信息：{}", gameId);
            return new Response(4004); // 链接超时或资源不存在
        }
        
        Type type = null;
        String typeName = null;
        String typeImage = null;
        
        if (!BaseUtils.isEmpty(game.getTypeId())) {
            type = typeService.getById(game.getTypeId());
            if (!BaseUtils.isEmpty(type)) {
                typeName = type.getTypeName();
                typeImage = type.getImage();
            }
        }

        GameInfoVo gameInfo = new GameInfoVo();
        gameInfo.setGameId(game.getId())
                .setTypeName(typeName)
                .setTypeImage(typeImage)
                .setGameName(game.getGameName())
                .setPrice(game.getPrice())
                .setGameIntroduction(game.getGameIntroduction())
                .setGameDate(game.getGameDate())
                .setGamePublisher(game.getGamePublisher());
        
        // 将图片字符串按 "$" 拆分为列表
        if (!BaseUtils.isEmpty(game.getImages())) {
            gameInfo.setImages(Arrays.asList(game.getImages().split("\\$")));
        }

        return new Response(1001, gameInfo); // 返回成功
    }

    @RequestMapping("/game/list")
    public Response gameList(@VerifiedUser User loginUser,
                           @RequestParam(name = "keyword", required = false) String keyword,
                           @RequestParam(name = "typeId", required = false) BigInteger typeId,
                           @RequestParam(name = "wp", required = false) String wp) {
        if (BaseUtils.isEmpty(loginUser)) {
            return new Response(1002); // 用户未登录
        }
        
        int currentPageSize = 10;
        Integer currentPage;

        GameWpVo baseWp = new GameWpVo();
        
        if (wp!=null&& !wp.isEmpty()) {
            try {
                byte[] bytes = Base64.getUrlDecoder().decode(wp);
                String json = new String(bytes, StandardCharsets.UTF_8);
                baseWp = JSON.parseObject(json, GameWpVo.class);
                currentPage = baseWp.getPage();
                currentPageSize = baseWp.getPageSize();
                keyword = baseWp.getKeyword();
                typeId = baseWp.getTypeId();
            } catch (Exception e) {
                logger.error("解析wp参数失败: {}", e.getMessage());
                return new Response(4004); // 参数解析错误
            }
        } else {
            currentPage = 1;
        }
        
        try {
            List<Game> gameList = gameService.getAllGame(currentPage, currentPageSize, keyword, typeId);
            
            // 收集类型ID
            Set<BigInteger> typeIdSet = new HashSet<>();
            for (Game game : gameList) {
                BigInteger tid = game.getTypeId();
                if (!BaseUtils.isEmpty(tid)) {
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
            GameWpVo outputWp = new GameWpVo();
            outputWp.setKeyword(keyword)
                    .setTypeId(typeId)
                    .setPage(currentPage + 1)
                    .setPageSize(currentPageSize);
            
            // 编码wp
            String encodeWp = Base64.getUrlEncoder().encodeToString(JSON.toJSONString(outputWp).getBytes(StandardCharsets.UTF_8));
            
            // 构建游戏列表数据
            List<GameListVo> gameVoList = new ArrayList<>();
            for (Game game : gameList) {
                String typeName = typeMap.get(game.getTypeId());
                if (BaseUtils.isEmpty(typeName)) {
                    logger.info("未找到游戏类型名称：{}", game.getTypeId());
                    continue;
                }
                
                if (BaseUtils.isEmpty(game.getImages())) {
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
                
                ImageVo imageVo = new ImageVo();
                imageVo.setSrc(image)
                       .setAr(ar);
                
                GameListVo gameVo = new GameListVo();
                gameVo.setGameId(game.getId())
                      .setGameName(game.getGameName())
                      .setTypeName(typeName)
                      .setImage(imageVo);
                
                gameVoList.add(gameVo);
            }
            
            // 构建最终响应对象
            GameListResponseVo result = new GameListResponseVo();
            result.setGameList(gameVoList)
                  .setWp(encodeWp);
            
            return new Response(1001, result); // 返回成功
        } catch (Exception e) {
            logger.error("获取游戏列表失败: {}", e.getMessage(), e);
            return new Response(4004, "链接超时"); // 数据库连接超时或错误
        }
    }
}




