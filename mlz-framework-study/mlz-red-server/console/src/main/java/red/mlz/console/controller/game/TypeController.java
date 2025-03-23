package red.mlz.console.controller.game;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import red.mlz.console.annotations.VerifiedUser;
import red.mlz.console.domain.game.ChildrenListVO;
import red.mlz.console.domain.game.TypeDetailVO;
import red.mlz.console.domain.game.TypeTreeVO;
import red.mlz.console.domain.game.TypeVO;
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
        List<TypeVO> typeVOList = new ArrayList<>();
        for (Type type : typeList) {
            TypeVO typeVO = new TypeVO();
            List<ChildrenListVO> childrenList = new ArrayList<>();
            for (Type children : typeService.getChildrenList(type.getId())) {
                ChildrenListVO childrenListVO = new ChildrenListVO();
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
        return new Response(1001, typeVOList);
    }

    private String formatDate(long timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date date = new Date(timestamp * 1000);
        return sdf.format(date);
    }
    
    @RequestMapping("/type/info")
    public Response typeInfo(
            @VerifiedUser User loginUser,
            @RequestParam(name = "typeId") BigInteger typeId) {
        
        if (BaseUtils.isEmpty(loginUser)) {
            return new Response(1002); // 用户未登录
        }
        
        Type type = typeService.getById(typeId);
        if (type == null){
            log.info("未找到游戏类型：{}", typeId);
            return new Response(4004);
        }
        
        String createTime = formatDate(type.getCreateTime());
        String updateTime = formatDate(type.getUpdateTime());
        TypeDetailVO typeDetailVO = new TypeDetailVO();
        typeDetailVO.setTypeId(type.getId())
                .setTypeName(type.getTypeName())
                .setParentId(type.getParentId())
                .setImage(type.getImage())
                .setCreateTime(createTime)
                .setUpdateTime(updateTime);
        return new Response(1001, typeDetailVO);
    }

    @RequestMapping("/type/create")
    public Response createType(
            @VerifiedUser User loginUser,
            @RequestParam(name = "typeName") String typeName,
            @RequestParam(name = "image") String image,
            @RequestParam(name = "parentId", required=false) BigInteger parentId) {
        
        if (BaseUtils.isEmpty(loginUser)) {
            return new Response(1002); // 用户未登录
        }
        
        typeName = typeName.trim();
        if (typeName.isEmpty()) {
            log.info("游戏类型名称不能为空字符串");
            return new Response(4004, "游戏类型名称不能为空");
        }
        
        try {
            BigInteger typeId = typeService.edit(null, typeName, image, parentId);
            return new Response(1001, "成功 ID：" + typeId);
        } catch (RuntimeException e) {
            log.info(e.getLocalizedMessage());
            return new Response(4004, "创建失败");
        }
    }

    @RequestMapping("/type/update")
    public Response updateType(
            @VerifiedUser User loginUser,
            @RequestParam(name = "typeId") BigInteger typeId,
            @RequestParam(name = "typeName") String typeName,
            @RequestParam(name = "image") String image,
            @RequestParam(name = "parentId", required=false) BigInteger parentId) {
        
        if (BaseUtils.isEmpty(loginUser)) {
            return new Response(1002); // 用户未登录
        }
        
        typeName = typeName.trim();
        if (typeName.isEmpty()) {
            log.info("游戏类型名称不能为空字符串");
            return new Response(4004, "游戏类型名称不能为空");
        }
        
        try {
            typeService.edit(typeId, typeName, image, parentId);
            return new Response(1001, "成功 ID: " + typeId);
        } catch (RuntimeException e) {
            log.info(e.getLocalizedMessage());
            return new Response(4004, "更新失败");
        }
    }

    @GetMapping("/type/delete")
    public Response deleteType(
            @VerifiedUser User loginUser,
            @RequestParam(name = "typeId") BigInteger typeId) {
        
        if (BaseUtils.isEmpty(loginUser)) {
            return new Response(1002); // 用户未登录
        }
        
        if(typeId == null){
            log.info("游戏类型ID不能为空");
            return new Response(4004, "游戏类型ID不能为空");
        }
        
        if(gameService.getAllGameByTypeId(typeId) != null&& !gameService.getAllGameByTypeId(typeId).isEmpty()){
            log.info("该类型下有游戏，不能删除");
            return new Response(4004, "该类型下有游戏，不能删除");
        }
        
        try {
            int result = typeService.delete(typeId);
            return result == 1 
                ? new Response(1001, "删除成功") 
                : new Response(4004, "删除失败");
        } catch (Exception e) {
            log.info(e.getLocalizedMessage());
            return new Response(4004, "删除失败");
        }
    }

    @RequestMapping("/type/tree")
    public Response typeTree(
            @VerifiedUser User loginUser,
            @RequestParam(name = "keyword", required = false) String keyword) {
        
        if (BaseUtils.isEmpty(loginUser)) {
            return new Response(1002); // 用户未登录
        }
        
        List<Type> rootTypes = typeService.getRootTypes();
        List<TypeTreeVO> typeTreeList = new ArrayList<>();

        // 遍历根节点，递归构建类型树
        for (Type rootType : rootTypes) {
            // 递归构建当前节点及其子节点
            TypeTreeVO typeTreeVO = buildTree(rootType, keyword);
            if (typeTreeVO != null) {
                typeTreeList.add(typeTreeVO);
            }
        }
        return new Response(1001, typeTreeList);
    }

    private TypeTreeVO buildTree(Type type, String keyword) {
        TypeTreeVO typeTreeVO = new TypeTreeVO();
        typeTreeVO.setImage(type.getImage());
        typeTreeVO.setTypeId(type.getId());
        typeTreeVO.setTypeName(type.getTypeName());
        List<Type> children = typeService.getChildrenList(type.getId());
        List<TypeTreeVO> childrenList = new ArrayList<>();

        // 递归构建子节点
        for (Type child : children) {
            TypeTreeVO childTreeVO = buildTree(child, keyword);
            if (childTreeVO != null) {
                childrenList.add(childTreeVO);
            }
        }
        typeTreeVO.setChildrenList(childrenList);

        // 根据关键字过滤
        if (keyword != null && !keyword.isEmpty()) {
            if (!typeTreeVO.getTypeName().contains(keyword) && childrenList.isEmpty()) {
                return null; 
            }
        }

        return typeTreeVO;
    }
}
