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
        return super.sqlMapDeleteByPrimaryKeyElementGenerated(element, introspectedTable);
    }

    @Override
    public boolean sqlMapSelectByPrimaryKeyElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {

        element.addElement(new TextElement(" and dr = 'N' "));
        return super.sqlMapSelectByPrimaryKeyElementGenerated(element, introspectedTable);
    }


    @Override
    public boolean sqlMapUpdateByPrimaryKeySelectiveElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {
        XmlElement setItems = (XmlElement) element.getElements().get(1);
        Iterator it = setItems.getElements().iterator();
        while (it.hasNext()){
            XmlElement item = (XmlElement) it.next();
            if(item.getElements().get(0) instanceof TextElement &&
                    "ts = #{ts,jdbcType=BIGINT},".equals(((TextElement) item.getElements().get(0)).getContent())){
                it.remove();
                break;
            }
        }
        setItems.addElement(0, new TextElement("ts= unix_timestamp(current_timestamp(3))*1000 , "));
        return super.sqlMapUpdateByPrimaryKeySelectiveElementGenerated(element, introspectedTable);
    }

    public boolean sqlMapUpdateByPrimaryKeyWithBLOBsElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {
        return false;
    }

    public boolean sqlMapUpdateByPrimaryKeyWithoutBLOBsElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {
        return false;
    }

}
