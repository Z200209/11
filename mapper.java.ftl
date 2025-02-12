<#assign className = table.mapperName>

package ${package.Mapper};

import ${package.Entity}.${entity};
import org.apache.ibatis.annotations.*;
import java.math.BigInteger;

@Mapper
public interface ${className} {

// 根据ID查询操作
@Select("SELECT * FROM ${table.name} WHERE id = <#noparse>#{</#noparse>${table.name}Id<#noparse>}</#noparse> AND is_deleted=0")
${entity} getById(BigInteger ${table.name}Id);

// 根据ID提取操作
@Select("SELECT * FROM ${table.name} WHERE id = <#noparse>#{</#noparse>${table.name}Id<#noparse>}</#noparse>")
${entity} extractById(BigInteger ${table.name}Id);

// 插入操作
int insert(@Param("${table.name}")${entity} ${table.name});

// 更新操作
int update(@Param("${table.name}")${entity} ${table.name});

// 删除操作
@Update("UPDATE ${table.name} SET update_time = <#noparse>#{</#noparse>updateTime<#noparse>}</#noparse> , is_deleted = 1 WHERE ${table.name}_id = <#noparse>#{</#noparse>${table.name}Id<#noparse>}</#noparse>")
int delete(@Param("${table.name}Id") BigInteger ${table.name}Id,@Param("updateTime") Integer updateTime);

}
