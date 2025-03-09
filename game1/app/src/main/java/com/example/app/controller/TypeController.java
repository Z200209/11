package com.example.app.controller;

import com.example.app.domain.*;
import com.example.module.entity.Game;
import com.example.module.entity.Type;
import com.example.module.service.GameService;
import com.example.module.service.TypeService;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/game/app/type")
public class TypeController {
    private static final Logger log = LoggerFactory.getLogger(TypeController.class);
    @Resource
    private TypeService typeService;
    @Resource
    private GameService gameService;
    @RequestMapping("/list")
    public List<TypeVO> typeList(@RequestParam(name = "keyword", required=false)String keyword) {
        List<Type> typeList = typeService.getAll(keyword);
        if (typeList.isEmpty()){
            log.info("没有找到类型信息");
        }
        List<TypeVO> typeVOList = new ArrayList<>();
        for (Type type : typeList) {
            TypeVO typeVO = new TypeVO();
            List<ChildrenVO> childrenList = new ArrayList<>();
            for (Type children : typeService.getChildrenList(type.getId())) {
                ChildrenVO childrenListVO = new ChildrenVO();
                childrenListVO.setTypeId(children.getId())
                        .setTypeName(children.getTypeName())
                        .setImage(children.getImage());
                childrenList.add(childrenListVO);
            }
            typeVO.setTypeId(type.getId())
                    .setTypeName(type.getTypeName())
                    .setImage(type.getImage())
                    .setChildrenList(childrenList);
            typeVOList.add(typeVO);
        }
        return typeVOList;
    }

    @RequestMapping("/game/app/childrenList")
    public ChildrenListVO childrenList(@RequestParam(name = "typeId") BigInteger typeId) {
        List<Type> childrenList = typeService.getChildrenList(typeId);
        if (childrenList.isEmpty()){
            log.info("没有找到类型信息");
        }
        List<ChildrenVO> childrenVOList = new ArrayList<>();
        for (Type children : childrenList){
            if (children == null){
                log.info("没有找到类型信息");
                continue;
            }
            ChildrenVO childrenVO = new ChildrenVO();
            childrenVO.setTypeId(children.getId())
                    .setTypeName(children.getTypeName())
                    .setImage(children.getImage());
            childrenVOList.add(childrenVO);
        }

        List<ChildreGameVO> childreGameVOList = new ArrayList<>();
        for (Game game : gameService.getAllGameByTypeId(typeId)){
            if (game == null){
                log.info("没有找到游戏信息");
                continue;
            }
            ChildreGameVO childreGameVO = new ChildreGameVO();
            childreGameVO.setGameId(game.getId())
                    .setGameName(game.getGameName())
                    .setImage(game.getImages().split("\\$")[0])
                    .setTypeName(typeService.getById(game.getTypeId()).getTypeName());
            childreGameVOList.add(childreGameVO);
        }
        if (childrenVOList.isEmpty()){
            log.info("没有找到类型信息");
        }
        return new ChildrenListVO()
                .setChildrenList(childrenVOList)
                .setGameList(childreGameVOList);
    }


}
