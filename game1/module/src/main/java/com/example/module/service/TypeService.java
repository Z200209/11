package com.example.module.service;

import com.example.module.entity.Type;
import com.example.module.mapper.TypeMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class TypeService {

@Resource
private TypeMapper mapper;

/**
* 根据ID获取实体
*
* @param id 实体ID
* @return 实体对象
*/
public Type getById(BigInteger id) {
if (id == null) {
throw new IllegalArgumentException("ID 不能为空");
}
return mapper.getById(id);
}

/**
* 提取特定的实体信息（如果有特殊用途）
*
* @param id 实体ID
* @return 实体对象
*/
public Type extractById(BigInteger id) {
if (id == null) {
throw new IllegalArgumentException("ID 不能为空");
}
return mapper.extractById(id);
}

/**
* 插入新的实体记录
*
* @param entity 实体对象
* @return 影响的行数
*/
public int insert(Type entity) {
if (entity == null) {
throw new IllegalArgumentException("Type 实体不能为空");
}
return mapper.insert(entity);
}

/**
* 更新实体记录
*
* @param entity 实体对象
* @return 影响的行数
*/
public int update(Type entity) {
if (entity == null) {
throw new IllegalArgumentException("Type 实体不能为空");
}
return mapper.update(entity);
}

/**
* 删除实体记录（逻辑删除）
*
* @param id 实体ID
* @return 影响的行数
*/
public int delete(BigInteger id) {
if (id == null) {
throw new IllegalArgumentException("ID 不能为空");
}
int time = (int) (System.currentTimeMillis() / 1000);
return mapper.delete(id, time);
}

}