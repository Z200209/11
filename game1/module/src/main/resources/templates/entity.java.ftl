package ${package.Entity};
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class ${entity} {
//entity body
<#list table.fields as field>
    //${field.comment}
    private ${field.propertyType} ${field.propertyName};
</#list>
}
