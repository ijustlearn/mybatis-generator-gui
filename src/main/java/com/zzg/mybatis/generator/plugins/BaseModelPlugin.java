package com.zzg.mybatis.generator.plugins;

import org.mybatis.generator.api.*;

import java.io.File;
import java.util.*;

import org.mybatis.generator.api.dom.java.*;
import org.mybatis.generator.exception.ShellException;
import org.mybatis.generator.internal.DefaultShellCallback;

import static org.mybatis.generator.internal.util.StringUtility.stringHasValue;

public class BaseModelPlugin extends PluginAdapter {
    private final static String DEFAULT_BASE_MODEL_CLASS = ".BaseModel";
    private static final FullyQualifiedJavaType SERIALIZEBLE_TYPE = new FullyQualifiedJavaType("java.io.Serializable");

    private static final List<String> BASE_MODEL_FIELD = Arrays.asList(new String[]{"id", "ts", "enable", "createTime", "dr"});

    private Set<String> setMethodStrs = new HashSet<>();
    private Set<String> getMethodStrs = new HashSet<>();
    private Set<String> fieldStrs = new HashSet<>();



    private Set<Method> methods = new HashSet<>();

    private Set<Field> fields = new HashSet<>();


    @Override
    public boolean validate(List<String> list) {
        return true;
    }

    private ShellCallback shellCallback = null;

    public BaseModelPlugin() {
        shellCallback = new DefaultShellCallback(false);
    }

    @Override
    public List<GeneratedJavaFile> contextGenerateAdditionalJavaFiles(IntrospectedTable introspectedTable) {
        boolean hasPk = introspectedTable.hasPrimaryKeyColumns();
        JavaFormatter javaFormatter = context.getJavaFormatter();
        String modelTargetDir = context.getJavaModelGeneratorConfiguration().getTargetProject();
        String modelTargetPackage = context.getJavaModelGeneratorConfiguration().getTargetPackage();

        List<GeneratedJavaFile> mapperJavaFiles = new ArrayList<>();
        String javaFileEncoding = context.getProperty("javaFileEncoding");

        TopLevelClass baseModel = new TopLevelClass(modelTargetPackage + DEFAULT_BASE_MODEL_CLASS);

        if (stringHasValue(modelTargetPackage)) {

            baseModel.addImportedType(SERIALIZEBLE_TYPE);

            baseModel.setVisibility(JavaVisibility.PUBLIC);
            baseModel.addJavaDocLine("/**");
            baseModel.addJavaDocLine(" * " + "MODEL公共基类，由MybatisGenerator自动生成请勿修改");


            baseModel.addJavaDocLine(" */");


            if (!this.methods.isEmpty()) {
                for (Method method : this.methods) {
                    baseModel.addMethod(method);
                }
            }
            if (!this.fields.isEmpty()) {
                for (Field field : this.fields) {
                    baseModel.addField(field);
                }
            }

            List<GeneratedJavaFile> generatedJavaFiles = introspectedTable.getGeneratedJavaFiles();


            GeneratedJavaFile modelJavafile = new GeneratedJavaFile(baseModel, modelTargetDir, javaFileEncoding, javaFormatter);
            try {
                File modelDir = shellCallback.getDirectory(modelTargetDir, modelTargetPackage);
                File modelFile = new File(modelDir, modelJavafile.getFileName());
                // 文件不存在
                if (!modelFile.exists()) {
                    mapperJavaFiles.add(modelJavafile);
                }
            } catch (ShellException e) {
                e.printStackTrace();
            }
        }
        return mapperJavaFiles;
    }

    @Override
    public boolean modelGetterMethodGenerated(Method method, TopLevelClass topLevelClass, IntrospectedColumn introspectedColumn, IntrospectedTable introspectedTable, ModelClassType modelClassType) {
        for (String s : BASE_MODEL_FIELD) {
            String upeeredString = "get" + upperCase(s);
            if (!this.getMethodStrs.contains(upeeredString) && method.getName().equals(upeeredString)) {
                this.getMethodStrs.add(upeeredString);
                this.methods.add(method);
                return false;
            }
        }
        return super.modelGetterMethodGenerated(method, topLevelClass, introspectedColumn, introspectedTable, modelClassType);
    }

    @Override
    public boolean modelSetterMethodGenerated(Method method, TopLevelClass topLevelClass, IntrospectedColumn introspectedColumn, IntrospectedTable introspectedTable, ModelClassType modelClassType) {
        for (String s : BASE_MODEL_FIELD) {
            String upeeredString = "set" + upperCase(s);
            if (!this.setMethodStrs.contains(upeeredString) && method.getName().equals(upeeredString)) {
                this.setMethodStrs.add(upeeredString);
                this.methods.add(method);
                return false;
            }
        }
        return super.modelSetterMethodGenerated(method, topLevelClass, introspectedColumn, introspectedTable, modelClassType);
    }

    @Override
    public boolean modelFieldGenerated(Field field, TopLevelClass topLevelClass, IntrospectedColumn introspectedColumn, IntrospectedTable introspectedTable, ModelClassType modelClassType) {
        for (String s : BASE_MODEL_FIELD) {
            if (!this.fieldStrs.contains(s) && field.getName().equals(s)) {
                this.fieldStrs.add(s);
                this.fields.add(field);
                return false;
            }
        }
        return super.modelFieldGenerated(field, topLevelClass, introspectedColumn, introspectedTable, modelClassType);
    }

    public boolean modelBaseRecordClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {

        String modelTargetPackage = context.getJavaModelGeneratorConfiguration().getTargetPackage();
//        topLevelClass.addImportedType(modelTargetPackage + DEFAULT_BASE_MODEL_CLASS);
        topLevelClass.setSuperClass(modelTargetPackage + DEFAULT_BASE_MODEL_CLASS);
//        org.mybatis.generator.api.dom.java.Field field = new org.mybatis.generator.api.dom.java.Field();
//        field.setFinal(true);
//        field.setInitializationString("1L");
//        field.setName("serialVersionUID");
//        field.setStatic(true);
//        field.setType(new FullyQualifiedJavaType("long"));
//        field.setVisibility(JavaVisibility.PRIVATE);
//        this.getContext().getCommentGenerator().addFieldComment(field, introspectedTable);
//        topLevelClass.addField(field);
        return super.modelBaseRecordClassGenerated(topLevelClass, introspectedTable);
    }

    //首字母小写
    private String upperCase(String str) {
        char[] ch = str.toCharArray();
        if (ch[0] >= 'a' && ch[0] <= 'z') {
            ch[0] = (char) (ch[0] - 32);
        }
        return new String(ch);
    }

}
