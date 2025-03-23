package red.mlz.console.controller.game;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

import red.mlz.console.annotations.VerifiedUser;
import red.mlz.console.domain.game.DetailVO;
import red.mlz.console.domain.game.GameVO;
import red.mlz.console.domain.game.ListVO;
import red.mlz.module.module.game.entity.Game;
import red.mlz.module.module.game.entity.Type;
import red.mlz.module.module.game.service.GameService;
import red.mlz.module.module.game.service.TypeService;
import red.mlz.module.module.user.entity.User;
import red.mlz.module.module.user.service.BaseUserService;
import red.mlz.module.utils.BaseUtils;
import red.mlz.module.utils.Response;

@RestController
@RequestMapping("/game")
@Slf4j
public class GameController {

    @Autowired
    private GameService gameService;
    
    @Autowired
    private TypeService typeService;
    
    @Autowired
    private BaseUserService baseUserService;

    @RequestMapping("/create")
    public Response createGame(
            @VerifiedUser User loginUser,
            @RequestParam(name = "typeId",required = false) BigInteger typeId,
            @RequestParam(name = "gameName") String gameName,
            @RequestParam(name = "price") Float price,
            @RequestParam(name = "gameIntroduction") String gameIntroduction,
            @RequestParam(name = "gameDate") String gameDate,
            @RequestParam(name = "gamePublisher") String gamePublisher,
            @RequestParam(name = "images") String images) {
        if (BaseUtils.isEmpty(loginUser)) {
            return new Response(1002); // 用户未登录
        }
        gameName = gameName.trim();
        gamePublisher = gamePublisher.trim();

        if (gameName.isEmpty()){
            log.info("游戏名称不能为空字符串");
            return new Response(4004, "游戏名称不能为空");
        }
        if (price < 0){
            log.info("游戏价格不能为负数");
            return new Response(4004, "游戏价格不能为负数");
        }
        if (gameIntroduction.isEmpty()){
            log.info("游戏介绍不能为空字符串");
            return new Response(4004, "游戏介绍不能为空");
        }

        try {
            BigInteger insertId = gameService.edit(null, gameName, price, gameIntroduction, gameDate, gamePublisher, images, typeId);
            return new Response(1001, "成功, ID: " + insertId);
        } catch (RuntimeException e) {
            log.info(e.getMessage());
            return new Response(4004, "创建失败");
        }
    }

    @RequestMapping("/update")
    public Response updateGame(
            @VerifiedUser User loginUser,
            @RequestParam(name = "typeId" ) BigInteger typeId,
            @RequestParam(name = "gameId") BigInteger gameId,
            @RequestParam(name = "gameName") String gameName,
            @RequestParam(name = "price") Float price,
            @RequestParam(name = "gameIntroduction") String gameIntroduction,
            @RequestParam(name = "gameDate") String gameDate,
            @RequestParam(name = "gamePublisher") String gamePublisher,
            @RequestParam(name = "images") String images) {
            
        if (BaseUtils.isEmpty(loginUser)) {
            return new Response(1002); // 用户未登录
        }
        
        gameName = gameName.trim();
        gamePublisher = gamePublisher.trim();

        if (gameName.isEmpty()) {
            log.info("游戏名称不能为空字符串");
            return new Response(4004, "游戏名称不能为空");
        }
        if (price < 0){
            log.info("游戏价格不能为负数");
            return new Response(4004, "游戏价格不能为负数");
        }
        if (gameIntroduction.isEmpty()) {
            log.info("游戏介绍不能为空字符串");
            return new Response(4004, "游戏介绍不能为空");
        }
        
        try {
            gameService.edit(gameId, gameName, price, gameIntroduction, gameDate, gamePublisher, images, typeId);
            return new Response(1001, "成功, ID: " + gameId);
        } catch (RuntimeException e) {
            log.info(e.getLocalizedMessage());
            return new Response(4004, "更新失败");
        }
    }

    @RequestMapping("/delete")
    public Response deleteGame(
            @VerifiedUser User loginUser,
            @RequestParam(name = "gameId") BigInteger gameId) {

            
        if (BaseUtils.isEmpty(loginUser)) {
            return new Response(1002); // 用户未登录
        }
        
        try {
            int result = gameService.delete(gameId);
            return result == 1 
                ? new Response(1001, "删除成功") 
                : new Response(4004, "删除失败");
        } catch (Exception e) {
            log.info(e.getLocalizedMessage());
            return new Response(4004, "删除失败");
        }
    }
    
    private String formatDate(long timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date date = new Date(timestamp * 1000);
        return sdf.format(date);
    }
    
    @RequestMapping("/info")
    public Response gameInfo(
            @VerifiedUser User loginUser,
            @RequestParam(name = "gameId") BigInteger gameId) {
            
        if (BaseUtils.isEmpty(loginUser)) {
            return new Response(1002); // 用户未登录
        }
        
        Game game = gameService.getById(gameId);
        if (game == null){
            log.info("未找到游戏信息：{}", gameId);
            return new Response(4004, "未找到游戏信息");
        }
        
        String formattedCreateTime = formatDate(game.getCreateTime());
        String formattedUpdateTime = formatDate(game.getUpdateTime());
        BigInteger typeId = game.getTypeId();
        Type type = typeService.getById(typeId);
        
        if (type == null){
            log.info("未找到游戏类型：{}", typeId);
            return new Response(4004, "未找到游戏类型");
        }
        
        String typeName = type.getTypeName();
        if (typeName == null){
            log.info("未找到游戏类型名称：{}", typeId);
            typeName = "";
        }
        
        String typeImage = type.getImage();
        if (typeImage == null){
            log.info("未找到游戏类型图片：{}", typeId);
            typeImage = "";
        }

        DetailVO detailVO = new DetailVO();
        detailVO
            .setTypeName(typeName)
            .setTypeImage(typeImage)
            .setGameId(game.getId())
            .setGameName(game.getGameName())
            .setPrice(game.getPrice())
            .setGameIntroduction(game.getGameIntroduction())
            .setGameDate(game.getGameDate())
            .setGamePublisher(game.getGamePublisher())
            .setImages(Arrays.asList(game.getImages().split("\\$")))
            .setCreateTime(formattedCreateTime)
            .setUpdateTime(formattedUpdateTime);
            
        return new Response(1001, detailVO);
    }

    @RequestMapping("/list")
    public Response gameList(
            @VerifiedUser User loginUser,
            @RequestParam(name = "page", defaultValue = "1") Integer page,
            @RequestParam(name = "keyword", required=false) String keyword,
            @RequestParam(name = "typeId", required=false) BigInteger typeId) {
            
        if (BaseUtils.isEmpty(loginUser)) {
            return new Response(1002); // 用户未登录
        }
        
        int pageSize = 10;
        List<Game> gameList = gameService.getAllGame(page, pageSize, keyword, typeId);
        Integer total = gameService.getTotalCount(keyword);
        
        Set<BigInteger> typeIdSet = new HashSet<>();
        for (Game game : gameList) {
            BigInteger tid = game.getTypeId();
            if (tid != null) {
                typeIdSet.add(tid);
            }
        }
        
        List<Type> types = new ArrayList<>();
        if (!typeIdSet.isEmpty()) {
            types = typeService.getTypeByIds(typeIdSet);
        }
        
        Map<BigInteger, String> typeMap = new HashMap<>();
        for (Type type : types) {
            typeMap.put(type.getId(), type.getTypeName());
        }
        
        if (total == null) {
            log.info("查询数据错误");
            return new Response(4004, "查询数据错误");
        }
        
        List<GameVO> gameVOList = new ArrayList<>();
        for (Game game : gameList) {
            String typeName = typeMap.get(game.getTypeId());
            if (typeName == null) {
                log.info("未找到游戏类型名称：{}", game.getTypeId());
                continue;
            }
            
            GameVO gameVO = new GameVO()
                    .setTypeId(game.getTypeId())
                    .setGameId(game.getId())
                    .setGameName(game.getGameName())
                    .setTypeName(typeName)
                    .setImage(game.getImages().split("\\$")[0]);
            gameVOList.add(gameVO);
        }

        ListVO results = new ListVO()
                .setGameList(gameVOList)
                .setTotal(total)
                .setPageSize(pageSize);

        return new Response(1001, results);
    }
}













