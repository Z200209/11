package com.example.app.controller;

import com.example.app.domain.ChildrenListVO;
import com.example.app.domain.TypeVO;
import com.example.module.entity.Type;
import com.example.module.service.TypeService;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/game/app/type")
public class TypeController {
    private static final Logger log = LoggerFactory.getLogger(TypeController.class);
    @Resource
    private TypeService typeService;
    @RequestMapping("/list")
    public List<TypeVO> typeList(@RequestParam(name = "keyword", required=false)String keyword) {
        List<Type> typeList = typeService.getAll(keyword);
        if (typeList.isEmpty()){
            log.info("没有找到类型信息");
        }
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
        return typeVOList;
    }
}
