package com.example.app.controller;

import com.alibaba.fastjson.JSON;
import com.example.app.domain.GameInfoVO;
import com.example.app.domain.GameVO;
import com.example.app.domain.GameListVO;
import com.example.module.entity.Game;
import com.example.module.entity.Type;
import com.example.module.entity.Wp;
import com.example.module.service.GameService;
import com.example.module.service.TypeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.*;

@RestController
@RequestMapping("/game/app")
public class GameController {

    private static final Logger log = LoggerFactory.getLogger(GameController.class);
    @Autowired
    private GameService gameService;
    @Autowired
    private TypeService typeService;

    @RequestMapping("/info")
    public GameInfoVO gameInfo(@RequestParam(name = "gameId") BigInteger gameId) {
        Game game = gameService.getById(gameId);
        if (game == null) {
            log.info("未找到游戏信息：{}", gameId);
            return null;
        }
        Type type = typeService.getById(game.getTypeId());
        if (type == null) {
            log.info("未找到游戏类型：" + game.getTypeId());
            return null;
        }
        String typeName = type.getTypeName();
        if (typeName == null) {
            log.info("未找到游戏类型名称：{}", type.getId());
        }
        String typeImage = type.getImage();
        if (typeImage == null) {
            log.info("未找到游戏类型图片：{}", type.getId());
        }

        GameInfoVO detailVO = new GameInfoVO();
        return detailVO
                .setGameId(game.getId())
                .setTypeName(typeName)
                .setTypeImage(typeImage)
                .setGameName(game.getGameName())
                .setPrice(game.getPrice())
                .setGameIntroduction(game.getGameIntroduction())
                .setGameDate(game.getGameDate())
                .setGamePublisher(game.getGamePublisher())
                .setImages(Arrays.asList(game.getImages().split("\\$"))); // 将图片字符串按 "$" 拆分为列表
    }

        @RequestMapping("/list")
        public GameListVO gameList(@RequestParam(name = "page", defaultValue = "1",required = false) Integer page,
                                   @RequestParam(name = "keyword", required=false) String keyword,
                                   @RequestParam(name = "typeId", required=false) BigInteger typeId,
                                   @RequestParam(name = "wp", required=false)String wp) {
        int currentPageSize = 10;
        Integer currentPage;

        if (wp!=null&& !wp.isEmpty()) {
            if(page!=null){
                return null;
            }
            byte[] bytes = Base64.getDecoder().decode(wp);
            String json = new String(bytes, StandardCharsets.UTF_8);
            Wp reviceWp = JSON.parseObject(json, Wp.class);
            currentPage = reviceWp.getPage();
            if (currentPage ==1){
                return null;
            }
        }
        else {
            currentPage = page;
            if (currentPage!= 1){
                return null;
            }
        }
        List<Game> gameList = gameService.getAllGame(currentPage, currentPageSize, keyword, typeId);
        Wp outputWp = new Wp();
        outputWp.setKeyword(keyword)
                .setTypeId(typeId)
                .setPage(currentPage+1)
                .setPageSize(currentPageSize);

        String encodeWp= Base64.getEncoder().encodeToString(JSON.toJSONString(outputWp).getBytes(StandardCharsets.UTF_8));

        List<GameVO> gameVOList = new ArrayList<>();
        for (Game game : gameList) {
            Type type = typeService.getById(game.getTypeId());
            if (type == null) {
                log.info("未找到游戏类型：" + game.getTypeId());
                continue;
            }
            String typeName = type.getTypeName();

            GameVO gameVO = new GameVO()
                    .setGameId(game.getId())
                    .setGameName(game.getGameName())
                    .setTypeName(typeName)
                    .setImages(game.getImages().split("\\$")[0]);
            gameVOList.add(gameVO);
        }
        return new GameListVO()
                .setGameList(gameVOList)
                .setWp(encodeWp);


        }

}




