package com.example.app.controller;

import com.alibaba.fastjson.JSON;
import com.example.app.domain.GameListVO;
import com.example.app.domain.GameVO;
import com.example.module.entity.GameListDTO;
import com.example.module.entity.Wp;
import com.example.module.service.GameBakservice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.*;
@RestController
@RequestMapping("/game/app/bak")
public class GameBakController {
    @Autowired
    private GameBakservice gameService;

    @RequestMapping("/list")
    public GameListVO gameList(@RequestParam(name = "keyword", required=false) String keyword,
                               @RequestParam(name = "typeId", required=false) BigInteger typeId,
                               @RequestParam(name = "wp", required=false)String wp) {
        int currentPageSize = 10;
        Integer currentPage;

        if (wp!=null&& !wp.isEmpty()) {
            byte[] bytes = Base64.getDecoder().decode(wp);
            String json = new String(bytes, StandardCharsets.UTF_8);
            Wp reviceWp = JSON.parseObject(json, Wp.class);
            currentPage = reviceWp.getPage();
            if (currentPage ==1){
                return null;
            }
            currentPageSize = reviceWp.getPageSize();
            keyword = reviceWp.getKeyword();
            typeId = reviceWp.getTypeId();
        }
        else {
            currentPage = 1;
        }

        List<GameListDTO> gameList = gameService.getAllGame(currentPage, currentPageSize, keyword, typeId);
        Wp outputWp = new Wp();
        outputWp.setKeyword(keyword)
                .setTypeId(typeId)
                .setPage(currentPage+1)
                .setPageSize(currentPageSize);

        String encodeWp= Base64.getEncoder().encodeToString(JSON.toJSONString(outputWp).getBytes(StandardCharsets.UTF_8));

        List<GameVO> gameVOList = new ArrayList<>();
        for (GameListDTO game : gameList) {
            GameVO gameVO = new GameVO()
                    .setGameId(game.getId())
                    .setGameName(game.getGameName())
                    .setTypeName(game.getTypeName())
                    .setImages(game.getImages().split("\\$")[0]);
            gameVOList.add(gameVO);
        }
        return new GameListVO()
                .setGameList(gameVOList)
                .setWp(encodeWp);
    }
}
