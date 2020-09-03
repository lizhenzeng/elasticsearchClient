package com.tigerobo.search.entity;

import com.tigerobo.search.factory.EsClient;

import java.lang.reflect.Type;
import java.util.List;

public interface Statement {
    void setException(Exception exception);

    void setPs(List<Parameter> ps);

    Object getResult(EsClient esClient);

    void setGenericType(Type[] genericType);
}
