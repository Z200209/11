package red.mlz.app.controller.game;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;
import red.mlz.app.annotations.VerifiedUser;
import red.mlz.app.domain.game.ChildrenListVO;
import red.mlz.app.domain.game.ChildrenVO;
import red.mlz.app.domain.game.ChildreGameVO;
import red.mlz.app.domain.game.TypeDetailVO;
import red.mlz.app.domain.game.TypeVO;
import red.mlz.module.module.game.entity.Game;
import red.mlz.module.module.game.entity.Type;
import red.mlz.module.module.game.service.GameService;
import red.mlz.module.module.game.service.TypeService;
import red.mlz.module.module.user.entity.User;
import red.mlz.module.utils.BaseUtils;
import red.mlz.module.utils.Response;

@RestController
@RequestMapping("/game")
@Slf4j
public class TypeController {
    @Autowired
    private TypeService typeService;
    
    @Autowired
    private GameService gameService;
    
    @RequestMapping("/type/list")
    public Response typeList(
            @VerifiedUser User loginUser,
            @RequestParam(name = "keyword", required=false) String keyword) {
        
        if (BaseUtils.isEmpty(loginUser)) {
            return new Response(1002); // 用户未登录
        }
        
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
        
        return new Response(1001, typeVOList); // 返回成功
    }

    @RequestMapping("/type/childrenList")
    public Response childrenList(
            @VerifiedUser User loginUser,
            @RequestParam(name = "typeId") BigInteger typeId) {
        
        if (BaseUtils.isEmpty(loginUser)) {
            return new Response(1002); // 用户未登录
        }
        
        try {
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
            
            ChildrenListVO result = new ChildrenListVO()
                    .setChildrenList(childrenVOList)
                    .setGameList(childreGameVOList);
            
            return new Response(1001, result); // 返回成功
        } catch (Exception e) {
            log.error("获取子类型列表失败: {}", e.getMessage(), e);
            return new Response(4004, "链接超时"); // 数据库连接超时或错误
        }
    }
    
    @RequestMapping("/type/info")
    public Response info(
            @VerifiedUser User loginUser,
            @RequestParam(name = "typeId") BigInteger typeId) {
        
        if (BaseUtils.isEmpty(loginUser)) {
            return new Response(1002); // 用户未登录
        }
        
        try {
            Type type = typeService.getById(typeId);
            if (BaseUtils.isEmpty(type)) {
                log.info("没有找到类型信息: {}", typeId);
                return new Response(4004); // 链接超时或资源不存在
            }
            
            TypeDetailVO typeDetailVO = new TypeDetailVO();
            typeDetailVO.setTypeId(type.getId())
                    .setTypeName(type.getTypeName())
                    .setParentId(type.getParentId())
                    .setImage(type.getImage())
                    .setCreateTime(type.getCreateTime())
                    .setUpdateTime(type.getUpdateTime());
            
            return new Response(1001, typeDetailVO); // 返回成功
        } catch (Exception e) {
            log.error("获取类型详情失败: {}", e.getMessage(), e);
            return new Response(4004, "链接超时"); // 数据库连接超时或错误
        }
    }
}
