package com.tigerobo.search.entity;

import com.alibaba.fastjson.JSONObject;
import com.tigerobo.search.annotation.Table;
import com.tigerobo.search.factory.EsClient;
import com.tigerobo.search.utils.EsClientUtils;
import com.tigerobo.search.utils.ReflectUtils;

import javax.persistence.Id;
import javax.persistence.Transient;
import java.lang.reflect.Type;
import java.util.List;

public class InsertMetaDataStatement implements Statement {
    private Parameter parameter;
    private Object index;
    private Object type;

    @Override
    public void setException(Exception exception) {

    }

    @Override
    public void setPs(List<Parameter> ps) {
        if (ps != null && !ps.isEmpty()) {
            parameter = ps.get(0);
            Object[] values = ReflectUtils.getClassTypeAnnotation(parameter.getValue(), Table.class, new String[]{"index", "type"});
            if (values != null && values.length == 2) {
                index = values[0];
                type = values[1];
            }
        }
    }


    @Override
    public Object getResult(EsClient esClient) {
        EsClientUtils esClientUtils = new EsClientUtils(esClient);
        Object target =  parameter.getValue();
        ReflectUtils.setValueByAnnotation(target,Transient.class,null);
        if (esClientUtils.save(
                index,
                type,
                ReflectUtils.getValueByAnnotation(target, Id.class),
                JSONObject.toJSONString(parameter.getValue()))) {
            return parameter.getValue();
        }
        return null;
    }

    @Override
    public void setGenericType(Type[] genericType) {



    }
}
