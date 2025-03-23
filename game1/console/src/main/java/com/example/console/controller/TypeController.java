package com.example.console.controller;

import com.example.console.annotations.VerifiedUser;
import com.example.console.domain.TypeDetailVO;
import com.example.console.domain.TypeTreeVO;
import com.example.module.entity.Game;
import com.example.module.entity.Type;
import com.example.module.entity.User;
import com.example.module.service.GameService;
import com.example.module.service.TypeService;
import com.example.module.utils.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * 游戏类型控制器
 */
@Slf4j
@RestController
@RequestMapping("/game/console/type")
public class TypeController {
    @Autowired
    private TypeService typeService;
    @Autowired
    private GameService gameService;

    /**
     * 获取类型列表
     */
    @RequestMapping("/list")
    public Response<List<Type>> typeList(@VerifiedUser User loginUser) {
        // 验证用户是否登录
        if (loginUser == null) {
            log.warn("未登录用户尝试获取类型列表");
            return new Response(1002, "用户未登录");
        }
        
        try {
            List<Type> typeList = typeService.getAll(null);
            return new Response(1001, typeList);
        } catch (Exception e) {
            log.error("获取类型列表失败", e);
            return new Response(4004, "系统异常");
        }
    }

    /**
     * 获取类型详情
     */
    @RequestMapping("/info")
    public Response<TypeDetailVO> typeInfo(
            @VerifiedUser User loginUser,
            @RequestParam(name = "typeId") BigInteger typeId) {
        
        // 验证用户是否登录
        if (loginUser == null) {
            log.warn("未登录用户尝试获取类型详情");
            return new Response(1002, "用户未登录");
        }
        
        try {
            Type type = typeService.getById(typeId);
            if (type == null) {
                log.info("未找到游戏类型：{}", typeId);
                return new Response(4004, "未找到游戏类型");
            }
            
            TypeDetailVO typeDetailVO = new TypeDetailVO()
                    .setTypeId(type.getId())
                    .setTypeName(type.getTypeName())
                    .setParentId(type.getParentId())
                    .setImage(type.getImage())
                    .setCreateTime(formatDate(type.getCreateTime()))
                    .setUpdateTime(formatDate(type.getUpdateTime()));
                    
            return new Response(1001, typeDetailVO);
        } catch (Exception e) {
            log.error("获取类型详情失败", e);
            return new Response(5000, "系统异常");
        }
    }

    /**
     * 创建类型
     */
    @RequestMapping("/create")
    public Response<String> createType(
            @VerifiedUser User loginUser,
            @RequestParam(name = "typeName") String typeName,
            @RequestParam(name = "image") String image,
            @RequestParam(name = "parentId", required = false) BigInteger parentId) {
        
        // 验证用户是否登录
        if (loginUser == null) {
            log.warn("未登录用户尝试创建类型");
            return new Response(1002, "用户未登录");
        }
        
        try {
            // 参数验证
            typeName = typeName.trim();
            if (typeName.isEmpty()) {
                log.info("游戏类型名称不能为空字符串");
                return new Response(4005, "游戏类型名称不能为空");
            }
            
            // 创建类型
            BigInteger typeId = typeService.edit(null, typeName, image, parentId);
            return new Response<>(1001, "创建成功，ID：" + typeId);
        } catch (Exception e) {
            log.error("创建类型失败", e);
            return new Response(4004, "系统异常");
        }
    }

    /**
     * 更新类型
     */
    @RequestMapping("/update")
    public Response<String> updateType(
            @VerifiedUser User loginUser,
            @RequestParam(name = "typeId") BigInteger typeId,
            @RequestParam(name = "typeName") String typeName,
            @RequestParam(name = "image") String image,
            @RequestParam(name = "parentId", required = false) BigInteger parentId) {
        
        // 验证用户是否登录
        if (loginUser == null) {
            log.warn("未登录用户尝试更新类型");
            return new Response(1002, "用户未登录");
        }
        
        try {
            // 参数验证
            typeName = typeName.trim();
            if (typeName.isEmpty()) {
                log.info("游戏类型名称不能为空字符串");
                return new Response(4005, "游戏类型名称不能为空");
            }
            
            // 检查类型是否存在
            Type type = typeService.getById(typeId);
            if (type == null) {
                log.info("未找到游戏类型：{}", typeId);
                return new Response(4004, "未找到游戏类型");
            }
            
            // 更新类型
            typeService.edit(typeId, typeName, image, parentId);
            return new Response(1001, "更新成功");
        } catch (Exception e) {
            log.error("更新类型失败", e);
            return new Response(4004, "系统异常");
        }
    }

    /**
     * 删除类型
     */
    @GetMapping("/delete")
    public Response<String> deleteType(
            @VerifiedUser User loginUser,
            @RequestParam(name = "typeId") BigInteger typeId) {
        
        // 验证用户是否登录
        if (loginUser == null) {
            log.warn("未登录用户尝试删除类型");
            return new Response(1002, "用户未登录");
        }
        
        try {
            // 参数验证
            if (typeId == null) {
                log.info("游戏类型ID不能为空");
                return new Response(4005, "游戏类型ID不能为空");
            }
            
            // 检查该类型下是否有游戏
            List<Game> games = gameService.getAllGameByTypeId(typeId);
            if (games != null && !games.isEmpty()) {
                log.info("该类型下有游戏，不能删除：{}", typeId);
                return new Response(4005, "该类型下有游戏，不能删除");
            }
            
            // 删除类型
            int result = typeService.delete(typeId);
            if (result == 1) {
                return new Response(1001, "删除成功");
            } else {
                return new Response(4004, "删除失败");
            }
        } catch (Exception e) {
            log.error("删除类型失败", e);
            return new Response(4004, "系统异常");
        }
    }

    /**
     * 格式化日期
     */
    private String formatDate(Integer date) {
        if (date == null) {
            return "";
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(date);
    }

    @RequestMapping("/tree")
    public Response typeTree(@RequestParam(name = "keyword", required = false) String keyword) {
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
