package com.example.module;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.InjectionConfig;
import com.baomidou.mybatisplus.generator.config.*;
import com.baomidou.mybatisplus.generator.config.po.TableInfo;
import com.baomidou.mybatisplus.generator.config.rules.DateType;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 代码生成器
 *
 * @author
 * @since
 */
public class CodeGenerator {

    /**
     * 执行代码生成 main 方法
     *
     * @param args
     */
    public static void main(String[] args) {
        CodeGenerator.autoGenerator();
    }

    /**
     * 代码生成设置
     */
    public static void autoGenerator(){
        // 代码生成器
        AutoGenerator mpg = new AutoGenerator();

        // 全局配置
        GlobalConfig gc = new GlobalConfig();
        String projectPath = System.getProperty("user.dir");
        gc.setOutputDir(projectPath + "/module/src/main/java");
        gc.setAuthor("xxxx");
        gc.setOpen(false);
        gc.setIdType(IdType.ID_WORKER);
        gc.setBaseResultMap(true);
        gc.setBaseColumnList(true);
        gc.setServiceName("%sService");
        gc.setFileOverride(false);
        gc.setDateType(DateType.ONLY_DATE);
        gc.setSwagger2(false);
        mpg.setGlobalConfig(gc);

        //数据源配置
        DataSourceConfig dsc = new DataSourceConfig();
        dsc.setUrl("jdbc:mysql://localhost:3306/game?useUnicode=true&characterEncoding=utf-8&autoReconnect=true&serverTimezone=UTC");
        dsc.setDriverName("com.mysql.cj.jdbc.Driver");
        dsc.setUsername("root");
        dsc.setPassword("Password");
        mpg.setDataSource(dsc);

        //包配置
        PackageConfig pc = new PackageConfig();
        pc.setParent("com.example");
        pc.setModuleName("module");
        pc.setService("service");
        pc.setEntity("entity");
        pc.setMapper("mapper");
        mpg.setPackageInfo(pc);

        // 配置模板
        TemplateConfig templateConfig = new TemplateConfig();
        templateConfig.setMapper("/templates/mapper.java");
        templateConfig.setXml("/templates/mapper.xml");
        templateConfig.setController(null);
        templateConfig.setServiceImpl(null);
        templateConfig.setEntity("/templates/entity.java");
        templateConfig.setService("/templates/service.java");
//        templateConfig.setEntity()
        mpg.setTemplate(templateConfig);

        // 策略配置
        StrategyConfig strategy = new StrategyConfig();
        strategy.setNaming(NamingStrategy.underline_to_camel);
        strategy.setColumnNaming(NamingStrategy.underline_to_camel);
        strategy.setEntityLombokModel(true);
        strategy.setRestControllerStyle(true);
        strategy.setControllerMappingHyphenStyle(true);
        strategy.setEntityTableFieldAnnotationEnable(true);
        // 可是输入多个表
        strategy.setInclude("game","type");

//        strategy.setTablePrefix("reservation_label_relation");
        mpg.setStrategy(strategy);

        // 配置额外的输出文件（InjectionConfig可不要这一部分，如果没有额外的输出文件，额外输出文件，如：Spring Cloud的Figen层）
        // ======================= ↓ ==============================
//        String injecPath = "com.practice.app";
//        InjectionConfig injectionConfig = new MyInjectionConfig(injecPath);
//        List<FileOutConfig> list = new ArrayList<>();
//        list.add(new MyFileOutConfig("/templates/appService.java.ftl",  projectPath + "/src/main/java" + String.format("/%s/", injecPath.replaceAll("\\.", "/"))));
//        injectionConfig.setFileOutConfigList(list);
//        mpg.setCfg(injectionConfig);
        // ======================= ↑ ==============================

        mpg.setTemplateEngine(new FreemarkerTemplateEngine());
        mpg.execute();
    }

    /**
     * 定义切面
     */
    static class MyInjectionConfig extends InjectionConfig {

        /**
         * 文件存储的url
         */
        private String parentPath;

        /**
         * 构造器
         *
         * @param parentPath 文件存储的url 如：com.lingluo.app
         */
        public MyInjectionConfig(String parentPath) {
            this.parentPath = parentPath;
        }

        /**
         * 设置在ftl文件中要用到的参数 如：templates/appService.java.ftl 文件中 ${cfg.appServiceName}
         */
        @Override
        public void initMap() {
            List<TableInfo> list = this.getConfig().getTableInfoList();
            Map<String, Object> map = new HashMap<>(10);
            map.put("appService", parentPath);
            for (TableInfo tableInfo : list) {
                map.put("appServiceName", tableInfo.getEntityName() + "AppService");
            }
            this.setMap(map);
        }

    }

    /**
     * 自定义文件输出目录
     */
    static class MyFileOutConfig extends FileOutConfig {

        /**
         * 完整的文件输出目录
         */
        private String filePath;

        /**
         * 文件输出构造器
         *
         * @param templatePath 模板url 如：/templates/appService.java.ftl
         * @param filePath 完成的文件输出目录 如：C:\Users\luohoujian01\myProject\springboot-project/src/main/java/com/lingluo/app/
         */
        public MyFileOutConfig(String templatePath, String filePath) {
            super(templatePath);
            this.filePath = filePath;
        }

        // 自定义输出文件目录，完整的输出目录 如：C:\Users\luohoujian01\myProject\springboot-project/src/main/java/com/lingluo/app/AlipayOrdersAppService.java
        @Override
        public String outputFile(TableInfo tableInfo) {
            return filePath + String.format("%sAppService", tableInfo.getEntityName()) + ".java";
        }
    }
}