package com.tigerobo.search.factory;

import com.tigerobo.search.mapping.MappedStatement;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Proxy;

public class MapperFactoryBean<T> implements FactoryBean<T> {

    private Class<T> mapperInterface;

    private MappedStatement mappedStatement;
    private EsClient esClient;


    public MapperFactoryBean(){}


    public MapperFactoryBean(Class<T> mapperInterface, EsClient esClient){
        this.mapperInterface = mapperInterface;
        this.esClient = esClient;
        this.mappedStatement = new MappedStatement(mapperInterface,esClient);
    }

    @Override
    public T getObject() throws Exception {
        return (T) Proxy.newProxyInstance(mappedStatement.getClass().getClassLoader(),new Class[]{mapperInterface},mappedStatement);
    }


    @Override
    public Class<T> getObjectType() {
        return this.mapperInterface;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }
}
