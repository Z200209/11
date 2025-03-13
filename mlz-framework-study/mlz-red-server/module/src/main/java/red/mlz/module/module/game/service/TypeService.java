package red.mlz.module.module.game.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import red.mlz.module.module.game.entity.Type;
import red.mlz.module.module.game.mapper.TypeMapper;
import red.mlz.module.utils.BaseUtils;

import javax.annotation.Resource;
import java.math.BigInteger;
import java.util.List;
import java.util.Set;
import java.util.ArrayList;


@Service
@Slf4j
public class TypeService {
    @Resource
    private TypeMapper mapper;

    public Type getById(BigInteger id) {
        return mapper.getById(id);
    }

    public List<Type> getTypeByIds(Set<BigInteger> typeIdSet) {
        if (BaseUtils.isEmpty(typeIdSet)) {
            return new ArrayList<>();
        }
        
        StringBuffer typeIdList = new StringBuffer();
        for (BigInteger bigInteger : typeIdSet) {
            if (!typeIdList.isEmpty()){
                typeIdList.append(",");
            }
            typeIdList.append(bigInteger.toString());
        }
        String ids = typeIdList.toString();

        return mapper.getTypeByIds(ids);
    }

    public Type extractById(BigInteger id) {
        return mapper.extractById(id);
    }

    public BigInteger insert(Type type) {
        mapper.insert(type);
        return type.getId();
    }

    public int update(Type type) {
        return mapper.update(type);
    }
    
    public void unsafeUpdate(Type type) {
        int result = mapper.update(type);
        if(result == 0){
            throw new RuntimeException("update error");
        }
    }

    public int delete(BigInteger id) {
        if (BaseUtils.isEmpty(id)){
            throw new RuntimeException("id 不能为空");
        }
        int time = BaseUtils.currentSeconds();
        return mapper.delete(id, time);
    }

    public List<Type> getAll(String keyword) {
        return mapper.getParentTypeList(keyword);
    }

    public List<Type> getChildrenList(BigInteger id) {
        if (BaseUtils.isEmpty(id)) {
            return new ArrayList<>();
        }
        return mapper.getChildrenTypeList(id);
    }

    public Integer getTotalCount(String keyword) {
        return mapper.getTotalCount(keyword);
    }

    @Transactional(rollbackFor = Exception.class)
    public BigInteger edit(BigInteger id, String typeName, String image, BigInteger parentId) {
        if (BaseUtils.isEmpty(typeName)) {
            throw new RuntimeException("typeName 不能为空");
        }
        if (BaseUtils.isEmpty(image)) {
            throw new RuntimeException("image 不能为空");
        }
        
        int time = BaseUtils.currentSeconds();
        Type type = new Type();
        type.setTypeName(typeName);
        type.setParentId(parentId);
        type.setImage(image);
        type.setUpdateTime(time);
        
        if (BaseUtils.isEmpty(id)) {
            type.setCreateTime(time);
            type.setIsDeleted(0);
            insert(type);
            log.info("新增类型: {}", type.getTypeName());
        } else {
            type.setId(id);
            try {
                unsafeUpdate(type);
                log.info("更新类型: {}", type.getTypeName());
            } catch (Exception e) {
                log.error("更新类型失败: {}", e.getMessage());
                throw new RuntimeException("更新类型失败");
            }
        }
        
        return type.getId();
    }

    public List<BigInteger> getTypeIdList(String keyword) {
        return mapper.getTypeIdList(keyword);
    }

    public List<Type> getRootTypes() {
        return mapper.getRootTypes();
    }
}
