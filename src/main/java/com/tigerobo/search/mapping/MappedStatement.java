package com.tigerobo.search.mapping;

import com.tigerobo.search.annotation.MetaAnnotation;
import com.tigerobo.search.bind.BaseMapperInterface;
import com.tigerobo.search.entity.MetaDataStatement;
import com.tigerobo.search.entity.Parameter;
import com.tigerobo.search.entity.WrapClass;
import com.tigerobo.search.factory.EsClient;
import com.tigerobo.search.utils.ReflectUtils;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;

public class MappedStatement implements InvocationHandler {

    private Map<Method, MetaDataStatement> mdsl = new HashMap<>();
    private Map<Method,String[]> methodArgsName = new HashMap<>();
    private Class proxyInterface;
    private EsClient esClient;
    public MappedStatement(){}
    public MappedStatement(Class proxyInterface,EsClient esClient){
        this.proxyInterface = proxyInterface;
        this.esClient = esClient;
        parserMapper(proxyInterface);
        parserGenericTypeOfInterface(proxyInterface);
    }

    public void parserGenericTypeOfInterface(Class proxyInterface){
        if (proxyInterface.isInterface()) {
            Type[] types = proxyInterface.getGenericInterfaces();
            if(types!=null && types.length>0){
                for(Type tp :types){
                    if(((Class)((ParameterizedType)tp).getRawType()).isAssignableFrom(BaseMapperInterface.class)){
                        ParameterizedType pt = (ParameterizedType)tp;
                        mdsl.entrySet().stream().forEach(val->val.getValue().setGenericType(pt.getActualTypeArguments()));
                    }
                }
            }
        }

   }

    public MappedStatement parserMapper(Class proxyInterface) {
        Method[] ms = proxyInterface.getDeclaredMethods();
        Arrays.stream(ms).forEach(val ->mdsl.put(val, convertMethod2MDS(val)));
        return this;
    }

    private MetaDataStatement convertMethod2MDS(Method method) {
        MetaAnnotation ma = new MetaAnnotation();
        ma.registerAnnotation(Arrays.stream(method.getAnnotations()).collect(Collectors.toList()));
        ma.parser();
        try {
            WrapClass wc = new WrapClass.Builder().setClass(proxyInterface).build();
            String[] originParamterNames = ReflectUtils.getMethodParameterNames(method, wc);
            methodArgsName.put(method,originParamterNames);
        }catch (Exception e){
            e.printStackTrace();
        }
        return new MetaDataStatement((String) ma.getObject("sql"));
    }

    private void initMetaDataStatement(MetaDataStatement metaDataStatement, Method method, Object[] args) {
        try {
            String[] originParamterNames =  methodArgsName.get(method);
            List<Parameter> ps = new ArrayList<>();
            if (originParamterNames != null && args != null && originParamterNames.length == args.length) {
                for (int i = 0; i < originParamterNames.length; i++) {
                    Parameter p = new Parameter(originParamterNames[i], args[i],args[i].getClass());
                    ps.add(p);
                }
            }
            metaDataStatement.setPs(ps);
        } catch (Exception e) {
            metaDataStatement.setException(e);
        }
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        MetaDataStatement mds = mdsl.get(method);
        if (mds == null) {
            parserMapper(proxy.getClass());
            mds = mdsl.get(method);
        }
        initMetaDataStatement(mds,method,args);
        return mds.getResult(esClient);
    }
}
