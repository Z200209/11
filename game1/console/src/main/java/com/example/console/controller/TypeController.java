package com.example.console.controller;

import com.example.console.domain.TypeDetailVO;
import com.example.console.domain.ChildrenListVO;
import com.example.console.domain.TypeTreeVO;
import com.example.console.domain.TypeVO;
import com.example.module.entity.Type;
import com.example.module.service.GameService;
import com.example.module.service.TypeService;
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


@RestController
@RequestMapping("/game/console/type")
@Slf4j
public class TypeController {
    @Autowired
    private TypeService typeService;
    @Autowired
    private GameService gameService;

    @RequestMapping("/list")
    public List<TypeVO> typeList(@RequestParam(name = "keyword", required=false) String keyword) {
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
        return  typeVOList;
    }


    private String formatDate(long timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date date = new Date(timestamp * 1000);
        return sdf.format(date);
    }
    @RequestMapping("/info")
    public TypeDetailVO typeInfo(@RequestParam(name = "typeId") BigInteger typeId) {
        Type type = typeService.getById(typeId);
        if (type == null){
            log.info("未找到游戏类型：{}", typeId);
            return null;
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
        return typeDetailVO;
    }

    @RequestMapping("/create")
    public String createType(@RequestParam(name = "typeName") String typeName,
                             @RequestParam(name = "image") String image,
                             @RequestParam(name = "parentId", required=false) BigInteger parentId) {
        typeName = typeName.trim();
        if (typeName.isEmpty()) {
            log.info("游戏类型名称不能为空字符串");
            return "失败";
        }
        try {
            BigInteger typeId = typeService.edit(null, typeName, image,parentId);
            return "成功 ID：" + typeId ;
        } catch (RuntimeException e) {
            log.info(e.getLocalizedMessage());
            return "失败";
        }
    }

    @RequestMapping("/update")
    public String updateType(@RequestParam(name = "typeId") BigInteger typeId,
                             @RequestParam(name = "typeName") String typeName,
                             @RequestParam(name = "image") String image,
                             @RequestParam(name = "parentId", required=false) BigInteger parentId) {
        typeName = typeName.trim();
        if (typeName.isEmpty()) {
            log.info("游戏类型名称不能为空字符串");
            return "失败";
        }
        try {
            typeService.edit(typeId, typeName, image, parentId);
            return "成功 ID:" + typeId;
        } catch (RuntimeException e) {
            log.info(e.getLocalizedMessage());
            return "失败";
        }
    }

    @GetMapping("/delete")
    public String deleteType(@RequestParam(name = "typeId") BigInteger typeId) {
        if(typeId == null){
            log.info("游戏类型ID不能为空");
            return "失败";
        }
        if(gameService.getById(typeId)!=null){
            log.info("该类型下有游戏，不能删除");
            return "失败";
        }
        try {
            int result = typeService.delete(typeId);
             return result == 1 ? "成功" : "失败";

        } catch (Exception e) {
            log.info(e.getLocalizedMessage());
            return "失败";
        }

    }

@RequestMapping("/tree")
public List<TypeTreeVO> typeTree(@RequestParam(name = "keyword", required = false) String keyword) {
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
    return typeTreeList;
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
