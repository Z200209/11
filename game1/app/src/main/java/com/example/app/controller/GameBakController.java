package com.example.app.controller;

import com.alibaba.fastjson.JSON;
import com.example.app.domain.GameListVO;
import com.example.app.domain.GameVO;
import com.example.module.entity.listDTO;
import com.example.module.entity.Wp;
import com.example.module.service.GameBakservice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.File;
import java.io.IOException;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
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

        List<listDTO> gameList = gameService.getAllGame(currentPage, currentPageSize, keyword, typeId);
        Wp outputWp = new Wp();
        outputWp.setKeyword(keyword)
                .setTypeId(typeId)
                .setPage(currentPage+1)
                .setPageSize(currentPageSize);

        String encodeWp= Base64.getEncoder().encodeToString(JSON.toJSONString(outputWp).getBytes(StandardCharsets.UTF_8));

        List<GameVO> gameVOList = new ArrayList<>();
        for (listDTO game : gameList) {
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


    //上传文件

    @Value("${file.path}")
    private String dirPath;

    @RequestMapping(value = "upload")
    public Map<String, String> upload(MultipartFile file) throws IOException {
        if(!dirPath.endsWith(File.separator)) {
            dirPath += File.separator;
        }

        String prefix = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
        String fileName = UUID.randomUUID() + prefix;

        // 完整的本地保存路径
        String localPath = dirPath + fileName;

        // 保存文件到本地
        Files.copy(
                file.getInputStream(),
                Paths.get(localPath),
                StandardCopyOption.REPLACE_EXISTING
        );

        // 生成访问URL
        String accessUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/uploads/")  // 需要与资源映射路径一致
                .path(fileName)
                .toUriString();

        return Map.of(
                "localPath", localPath,
                "accessUrl", accessUrl,
                "message", "文件保存位置: " + localPath
        );
    }

}
