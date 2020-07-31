package com.mukutech.seapersonservice;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.InjectionConfig;
import com.baomidou.mybatisplus.generator.config.*;
import com.baomidou.mybatisplus.generator.config.po.TableFill;
import com.baomidou.mybatisplus.generator.config.po.TableInfo;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;

import java.util.ArrayList;
import java.util.List;

public class MybatisGenerator {

    static String outPath = "D:/GenJavaFile";
    static String author = "LMYOU";
    static String driverName = "com.mysql.jdbc.Driver";
    static String url = "jdbc:mysql://localhost:3306/center_test_db?useUnicode=true&characterEncoding=utf-8&useSSL=true&serverTimezone=UTC";
    static String username = "root";
    static String password = "admin";
    static String[] tables = new String[]{"TEST_DEMO"};
    //static String delete_falg = "DEL_FLAG";

    public static void main(String[] args) throws InterruptedException {
        AutoGenerator mpg = new AutoGenerator();
        // 全局配置
        GlobalConfig gc = new GlobalConfig();
        String projectPath = System.getProperty("user.dir");
        gc.setOutputDir(MybatisGenerator.outPath);
        gc.setFileOverride(true);
        gc.setActiveRecord(true);
        gc.setIdType(IdType.INPUT);
        gc.setEnableCache(false); // XML 二级缓存
        gc.setBaseResultMap(true); // XML ResultMap
        gc.setBaseColumnList(true); // XML columList
        gc.setOpen(true);
        gc.setAuthor(MybatisGenerator.author);
        // 自定义文件命名，注意 %s 会自动填充表实体属性！
        gc.setMapperName("%sMapper");
        gc.setXmlName("%sMapper");
        gc.setServiceName("I%sService");
        gc.setServiceImplName("%sServiceImpl");
        gc.setControllerName("%sController");
        mpg.setGlobalConfig(gc);
        // 数据源配置
        DataSourceConfig dsc = new DataSourceConfig();
        dsc.setDbType(DbType.MYSQL);
        dsc.setDriverName(MybatisGenerator.driverName);
        dsc.setUrl(MybatisGenerator.url);
        dsc.setUsername(MybatisGenerator.username);
        dsc.setPassword(MybatisGenerator.password);
        mpg.setDataSource(dsc);
        // 包配置
        PackageConfig pc = new PackageConfig();
        pc.setParent("com.mukutech");
        pc.setModuleName("seapersonservice");
        pc.setController("controller");
        mpg.setPackageInfo(pc);

        InjectionConfig injectionConfig =
                new InjectionConfig() {

                    // 使用map进行自定义属性设置
                    @Override
                    public void initMap() {
                        // Map<String, Object> map = new HashMap<>();
                        // map.put("abc", this.getConfig().getGlobalConfig().getAuthor()
                        // + "-mp");
                        // this.setMap(map);
                    }
                };
        List<FileOutConfig> focList = new ArrayList<>();
        // 自定义配置会被优先输出
        focList.add(
                new FileOutConfig("/templates/mapper.xml.vm") {
                    @Override
                    public String outputFile(TableInfo tableInfo) {
                        // 自定义输出文件名 ， 如果你 Entity 设置了前后缀、此处注意 xml 的名称会跟着发生变化！！
                        return outPath
                                + "/resources/mapping/"
                                + tableInfo.getEntityName()
                                + "Mapper"
                                + StringPool.DOT_XML;
                    }
                });
        // 自定义配置会被优先输出
        focList.add(
                new FileOutConfig("/templates/dto.java.vm") {
                    @Override
                    public String outputFile(TableInfo tableInfo) {
                        // 自定义输出文件名 ， 如果你 Entity 设置了前后缀、此处注意 xml 的名称会跟着发生变化！！
                        return outPath
                                + "/com/mukutech/seapersonservice/pojo/dto/"
                                + tableInfo.getEntityName()
                                + "DTO"
                                + StringPool.DOT_JAVA;
                    }
                });

        focList.add(
                new FileOutConfig("/templates/vo.java.vm") {
                    @Override
                    public String outputFile(TableInfo tableInfo) {
                        return outPath
                                + "/com/mukutech/seapersonservice/pojo/vo/"
                                + tableInfo.getEntityName()
                                + "VO.java";
                    }
                });

        injectionConfig.setFileOutConfigList(focList);
        // 配置自定义属性注入
        mpg.setCfg(injectionConfig);

        // 策略配置
        StrategyConfig strategy = new StrategyConfig();

        // 自动填充
        //    TableFill gmtCreate = new TableFill("gmt_create", FieldFill.INSERT);
        TableFill gmtModified = new TableFill("UPDATE_DATE", FieldFill.UPDATE);
        TableFill createBy = new TableFill("CREATE_BY", FieldFill.INSERT);
        TableFill createDate = new TableFill("CREATE_DATE", FieldFill.INSERT);
        TableFill updateBy = new TableFill("UPDATE_BY", FieldFill.UPDATE);
        ArrayList<TableFill> tableFills = new ArrayList<>();
        tableFills.add(createDate);
        tableFills.add(gmtModified);
        tableFills.add(createBy);
        tableFills.add(updateBy);
        strategy.setTableFillList(tableFills);

        // 此处可以修改为您的表前缀
        strategy.setTablePrefix(new String[]{"T_"});
        // 表名生成策略
        strategy.setNaming(NamingStrategy.underline_to_camel);
        // 需要生成的表
        strategy.setInclude(MybatisGenerator.tables);
        //strategy.setLogicDeleteFieldName(MybatisGenerator.delete_falg);
        // 关闭默认 xml 生成，调整生成 至 根目录
        TemplateConfig tc = new TemplateConfig();
        tc.setXml(null);
        mpg.setTemplate(tc);
        // 使用lombok
        strategy.setEntityLombokModel(true);
        mpg.setStrategy(strategy);
        // 执行生成
        mpg.execute();
    }
}
