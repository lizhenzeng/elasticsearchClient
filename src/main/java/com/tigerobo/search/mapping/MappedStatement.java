package com.tigerobo.search.mapping;

import com.tigerobo.search.annotation.MetaAnnotation;
import com.tigerobo.search.bind.BaseMapperInterface;
import com.tigerobo.search.constant.ConstantType;
import com.tigerobo.search.entity.Parameter;
import com.tigerobo.search.entity.Statement;
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

    private Map<Method, Statement> mdsl = new HashMap<>();
    private Map<Method, String[]> methodArgsName = new HashMap<>();
    private EsClient esClient;

    private static final Set<String> excludeMethodNames = new HashSet<>();
    static {
        excludeMethodNames.add("toString");
        excludeMethodNames.add("equals");
        excludeMethodNames.add("hashcode");

    }
    public MappedStatement() {
    }

    public MappedStatement(Class proxyInterface, EsClient esClient) {
        this.esClient = esClient;
        Class<?>[] baseInterfaces = proxyInterface.getInterfaces();
        if (baseInterfaces != null && baseInterfaces.length > 0) {
            Arrays.stream(baseInterfaces).forEach(val -> parserMapper(val));
        }
        parserMapper(proxyInterface);
        parserGenericTypeOfInterface(proxyInterface);
    }

    public void parserGenericTypeOfInterface(Class proxyInterface) {
        if (proxyInterface.isInterface()) {
            Type[] types = proxyInterface.getGenericInterfaces();
            if (types != null && types.length > 0) {
                for (Type tp : types) {
                    if (((Class) ((ParameterizedType) tp).getRawType()).isAssignableFrom(BaseMapperInterface.class)) {
                        ParameterizedType pt = (ParameterizedType) tp;
                        mdsl.entrySet().stream().forEach(val -> val.getValue().setGenericType(pt.getActualTypeArguments()));
                    }
                }
            }
        }

    }

    public MappedStatement parserMapper(Class proxyInterface) {
        Method[] ms = proxyInterface.getDeclaredMethods();
        Arrays.stream(ms).filter(val->!excludeMethodNames.contains(val)).forEach(val -> mdsl.put(val, convertMethod2MDS(val,proxyInterface)));
        return this;
    }

    private Statement convertMethod2MDS(Method method,Class interfaceClass) {
        MetaAnnotation ma = new MetaAnnotation();
        ma.registerAnnotation(Arrays.stream(method.getAnnotations()).collect(Collectors.toList()));
        ma.parser();
        try {
            WrapClass wc = new WrapClass.Builder().setClass(interfaceClass).build();
            String[] originParamterNames = ReflectUtils.getMethodParameterNames(method, wc);
            methodArgsName.put(method, originParamterNames);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ConstantType.search(method).getStatmentByMethod(ma);
    }

    private void initMetaDataStatement(Statement metaDataStatement, Method method, Object[] args) {
        try {
            String[] originParamterNames = methodArgsName.get(method);
            List<Parameter> ps = new ArrayList<>();
            if (originParamterNames != null && args != null && originParamterNames.length == args.length) {
                for (int i = 0; i < originParamterNames.length; i++) {
                    if(args[i]!=null){
                        Parameter p = new Parameter(originParamterNames[i], args[i], args[i].getClass());
                        ps.add(p);
                    }
                }
            }
            metaDataStatement.setPs(ps);
        } catch (Exception e) {
            e.printStackTrace();
            metaDataStatement.setException(e);
        }
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if(!excludeMethodNames.contains(method.getName())){
            Statement mds = mdsl.get(method);
            if (mds == null) {
                parserMapper(proxy.getClass());
                mds = mdsl.get(method);
            }
            initMetaDataStatement(mds, method, args);
            return mds.getResult(esClient);
        }
        return null;

    }
}
