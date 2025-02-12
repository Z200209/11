package com.example.app.controller;

import com.example.app.domain.TypeListVO;
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
    public TypeListVO typeList(@RequestParam(name = "page", defaultValue = "1") Integer page,
                               @RequestParam(name = "keyword", required=false)String keyword) {
        int pageSize = 10;
        List<Type> typeList = typeService.getAll(page, pageSize, keyword);
        if (typeList == null){
            log.info("没有找到类型信息");
            return null;
        }

        List<TypeVO> typeVOList = new ArrayList<>();
        for (Type type : typeList) {
            TypeVO typeVO = new TypeVO();
            typeVO.setTypeId(type.getId())
                    .setTypeName(type.getTypeName())
                    .setImage(type.getImage());
            typeVOList.add(typeVO);
        }
        return new TypeListVO()
                .setTypeList(typeVOList)
                .setIsEnd(typeList.size()<pageSize);
    }
}
