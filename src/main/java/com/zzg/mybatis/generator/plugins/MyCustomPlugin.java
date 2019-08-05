package com.zzg.mybatis.generator.plugins;

import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.Interface;
import org.mybatis.generator.api.dom.java.Method;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.Element;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.api.dom.xml.XmlElement;

import java.util.Iterator;
import java.util.List;

public class MyCustomPlugin extends PluginAdapter {
    @Override
    public boolean validate(List<String> list) {
        return true;
    }

    /**
     * 这里将原有的删除方法进行自定义软删除
     * @param element
     * @param introspectedTable
     * @return
     */
    @Override
    public boolean sqlMapDeleteByPrimaryKeyElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {
        TextElement element1 = new TextElement(String.format("update %s set dr = 'Y' , ts = unix_timestamp(current_timestamp(3))*1000 ",introspectedTable.getFullyQualifiedTable().getIntrospectedTableName()));
        List<Element> elements1 = element.getElements();
        elements1.set(0, element1);
//        return super.sqlMapDeleteByPrimaryKeyElementGenerated(element, introspectedTable);
        return true;
    }

    @Override
    public boolean sqlMapSelectByPrimaryKeyElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {
        System.out.println("mycustomplugin. sqlMapSelectByPrimaryKeyElementGenerated 被调用了一次");
        element.addElement(new TextElement(" and dr = 'N' "));
//        return super.sqlMapSelectByPrimaryKeyElementGenerated(element, introspectedTable);
        return true;
    }



    @Override
    public boolean sqlMapUpdateByPrimaryKeySelectiveElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {
        XmlElement setItems = (XmlElement) element.getElements().get(1);
        Iterator it = setItems.getElements().iterator();
        while (it.hasNext()){
            Object item = it.next();
            if (item instanceof XmlElement){
                XmlElement itemXml = (XmlElement) item;
                if(itemXml.getElements().get(0) instanceof TextElement &&
                        "ts = #{ts,jdbcType=BIGINT},".equals(((TextElement) itemXml.getElements().get(0)).getContent())){
                    it.remove();
                    break;
                }
            }
        }
        setItems.addElement(0, new TextElement("ts= unix_timestamp(current_timestamp(3))*1000 , "));
//        return super.sqlMapUpdateByPrimaryKeySelectiveElementGenerated(element, introspectedTable);
        return true;
    }

    @Override
    public boolean sqlMapUpdateByPrimaryKeyWithBLOBsElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {
        return false;
    }

    @Override
    public boolean sqlMapUpdateByPrimaryKeyWithoutBLOBsElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {
        return false;
    }

}
