package com.example.app.controller;

import com.example.app.annotations.VerifiedUser;
import com.example.app.domain.*;
import com.example.module.entity.Game;
import com.example.module.entity.Type;
import com.example.module.entity.User;
import com.example.module.service.GameService;
import com.example.module.service.TypeService;
import com.example.module.utils.Response;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/**
 * 游戏类型控制器
 */
@Slf4j
@RestController
@RequestMapping("/game/app/type")
public class TypeController {
    
    @Resource
    private TypeService typeService;
    
    @Resource
    private GameService gameService;
    
    /**
     * 获取类型列表
     */
    @RequestMapping("/list")
    public Response<List<TypeVO>> typeList(@VerifiedUser User loginUser,
                                          @RequestParam(name = "keyword", required=false) String keyword) {
        try {
            // 用户验证
            if (loginUser == null) {
                return new Response<>(1002, "用户未登录");
            }
            
            List<Type> typeList = typeService.getAll(keyword);
            if (typeList.isEmpty()) {
                log.info("没有找到类型信息");
                return new Response<>(1001, new ArrayList<>()); // 返回空列表
            }
            
            List<TypeVO> typeVOList = new ArrayList<>();
            for (Type type : typeList) {
                TypeVO typeVO = new TypeVO();
                List<ChildrenVO> childrenList = new ArrayList<>();
                
                // 获取子类型列表
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
            
            return new Response<>(1001, typeVOList); // 返回成功
        } catch (Exception e) {
            log.error("获取类型列表失败: {}", e.getMessage(), e);
            return new Response<>(4004, "处理请求失败");
        }
    }

    /**
     * 获取子类型列表和对应游戏列表
     */
    @RequestMapping("/childrenList")
    public Response<ChildrenListVO> childrenList(@VerifiedUser User loginUser,
                                                @RequestParam(name = "typeId") BigInteger typeId) {
        try {
            // 用户验证
            if (loginUser == null) {
                return new Response<>(1002, "用户未登录");
            }
            
            // 获取子类型列表
            List<Type> childrenList = typeService.getChildrenList(typeId);
            List<ChildrenVO> childrenVOList = new ArrayList<>();
            
            for (Type children : childrenList) {
                if (children == null) {
                    log.info("没有找到类型信息");
                    continue;
                }
                
                ChildrenVO childrenVO = new ChildrenVO();
                childrenVO.setTypeId(children.getId())
                        .setTypeName(children.getTypeName())
                        .setImage(children.getImage());
                childrenVOList.add(childrenVO);
            }
            
            // 获取游戏列表
            List<ChildreGameVO> childreGameVOList = new ArrayList<>();
            for (Game game : gameService.getAllGameByTypeId(typeId)) {
                if (game == null || game.getTypeId() == null || game.getImages() == null || game.getImages().isEmpty()) {
                    log.info("游戏数据不完整，跳过：{}", game.getId());
                    continue;
                }
                
                Type gameType = typeService.getById(game.getTypeId());
                if (gameType == null) {
                    log.info("未找到游戏类型：{}", game.getTypeId());
                    continue;
                }
                
                ChildreGameVO childreGameVO = new ChildreGameVO();
                childreGameVO.setGameId(game.getId())
                        .setGameName(game.getGameName())
                        .setImage(game.getImages().split("\\$")[0])
                        .setTypeName(gameType.getTypeName());
                childreGameVOList.add(childreGameVO);
            }
            
            // 构建返回对象
            ChildrenListVO result = new ChildrenListVO()
                    .setChildrenList(childrenVOList)
                    .setGameList(childreGameVOList);
            
            return new Response<>(1001, result); // 返回成功
        } catch (Exception e) {
            log.error("获取子类型列表失败: {}", e.getMessage(), e);
            return new Response<>(4004, "处理请求失败");
        }
    }
}
