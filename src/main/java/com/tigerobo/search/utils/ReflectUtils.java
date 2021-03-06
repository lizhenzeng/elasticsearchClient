package com.tigerobo.search.utils;

import com.tigerobo.search.annotation.MetaAnnotation;
import com.tigerobo.search.annotation.Param;
import com.tigerobo.search.entity.WrapClass;
import javassist.*;
import javassist.bytecode.CodeAttribute;
import javassist.bytecode.LocalVariableAttribute;
import javassist.bytecode.MethodInfo;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ReflectUtils {
    public static Method getCloseMethodByNameAndParamtized(Class clzz, String methodName, Class... parameterTypes) {
        try {
            List<Method> methods = new ArrayList<>();
            for (Method m : clzz.getDeclaredMethods()) {
                if (m.getName().equals(methodName) && isMatch(m.getParameterTypes(), parameterTypes)) {
                    methods.add(m);
                }
            }
            if (methods != null && !methods.isEmpty()) {
                return methods.get(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static Boolean isMatch(Class[] parameterTypes, Class[] methodParameterTypes) {
        if (parameterTypes.length == methodParameterTypes.length) {
            for (int i = 0; i < parameterTypes.length; i++) {
                if (!methodParameterTypes[i].isAssignableFrom(parameterTypes[i])
                        && !parameterTypes[i].isAssignableFrom(methodParameterTypes[i])) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    public static CtMethod[] getClazzOfCtMethods(Class clazz) throws NotFoundException {
        CtClass cc = convertClass2CtClass(clazz);
        CtMethod[] cm = cc.getDeclaredMethods();
        return cm;
    }

    public static CtClass convertClass2CtClass(Class cLass) throws NotFoundException {
        ClassPool pool = ClassPool.getDefault();
        return pool.get(cLass.getName());
    }

    public static Object[] getArgs(Method method, WrapClass wc, MetaAnnotation ma) throws NotFoundException {
        CtMethod ctMethod = convertMethod2CtMethod(method, wc);
        return getMethodArgsByParameterNames(ctMethod, ma);
    }

    public static CtMethod convertMethod2CtMethod(Method method, WrapClass wc) throws NotFoundException {
        CtClass mainCtClass = ReflectUtils.convertClass2CtClass(wc.getClazz());
        List<CtClass> paramterClazz = Arrays.stream(method.getGenericParameterTypes()).map(val -> {
            try {
                return ReflectUtils.convertClass2CtClass((Class) val);
            } catch (NotFoundException e) {
            }
            return null;
        }).collect(Collectors.toList());
        return mainCtClass.getDeclaredMethod(method.getName(), paramterClazz.toArray(new CtClass[paramterClazz.size()]));
    }

    public static String[] getParameterNameJava8(Class clazz, String methodName) {
        List<String> paramterList = new ArrayList<>();
        Method[] methods = clazz.getDeclaredMethods();
        for (Method method : methods) {
            if (methodName.equals(method.getName())) {
                //直接通过method就能拿到所有的参数
                Parameter[] params = method.getParameters();
                for (Parameter parameter : params) {
                    Annotation parameterAnnotation = parameter.getAnnotation(Param.class);
                    if (parameterAnnotation != null) {
                        paramterList.add(((Param) parameterAnnotation).value());
                    } else {
                        paramterList.add(parameter.getName());
                    }
                }
            }
        }
        return paramterList.toArray(new String[paramterList.size()]);
    }

    public static String[] getMethodParameterNames(Method method, WrapClass wc) throws NotFoundException {
//        LocalVariableTableParameterNameDiscoverer localVariableTableParameterNameDiscoverer = new LocalVariableTableParameterNameDiscoverer();
//        String[] parameterNames = localVariableTableParameterNameDiscoverer.getParameterNames(method);
        ParameterNameDiscoverer discoverer = new DefaultParameterNameDiscoverer();
        String[] parameterNames = discoverer.getParameterNames(method);
        if (parameterNames != null && parameterNames.length == method.getParameterCount()) {
            return parameterNames;
        } else {
//            CtMethod ctMethod = convertMethod2CtMethod(method, wc);
            parameterNames = getParameterNameJava8(wc.getClazz(), method.getName());
            if (parameterNames != null && parameterNames.length == method.getParameterCount()) {
                return parameterNames;
            }
        }
        return null;
    }

    public static Object[] getMethodArgsByParameterNames(CtMethod ctMethod, MetaAnnotation ma) throws NotFoundException {
        List<Object> args = new ArrayList();
        for (String p : ReflectUtils.methodParamterOriginalName(ctMethod)) {
            Object arg = ma.getObject(p);
            if (arg != null) {
                args.add(arg);
            }
        }

        return args.toArray(new Object[args.size()]);
    }


    public static String[] methodParamterOriginalName(CtMethod cm) throws NotFoundException {
        // 使用javaassist的反射方法获取方法的参数名
        MethodInfo methodInfo = cm.getMethodInfo();
        CodeAttribute codeAttribute = methodInfo.getCodeAttribute();
        LocalVariableAttribute attr = (LocalVariableAttribute) codeAttribute.getAttribute(LocalVariableAttribute.tag);
        String[] paramNames = new String[cm.getParameterTypes().length];
        int pos = Modifier.isStatic(cm.getModifiers()) ? 0 : 1;
        for (int i = 0; i < paramNames.length; i++)
            paramNames[i] = attr.variableName(i + pos);
        return paramNames;
    }


    public static boolean isCanditateByParamterName(CtMethod cm, MetaAnnotation ms) {
        try {
            String[] ss = methodParamterOriginalName(cm);
            for (String s : ss) {
                if (ms.getObject(s) == null) {
                    return false;
                }
            }
            return true;
        } catch (Exception e) {

        }
        return false;
    }

    public static <T extends Annotation> Object getValueByAnnotation(Object target, Class<T> t) {
        if (target != null) {
            for (Field f : target.getClass().getDeclaredFields()) {
                if (f.getAnnotation(t) != null) {
                    f.setAccessible(true);
                    try {
                        return f.get(target);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return null;
    }

    public static <T extends Annotation> void setValueByAnnotation(Object target,Class<T> t,Object value){
        if (target != null) {
            for (Field f : target.getClass().getDeclaredFields()) {
                if (f.getAnnotation(t) != null) {
                    f.setAccessible(true);
                    try {
                         f.set(target,value);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public static <T extends Annotation> Object[] getClassTypeAnnotation(Object target, Class<T> t, String[] keys) {
        try {
            T ans = target.getClass().getDeclaredAnnotation(t);
            if (ans != null) {
                Object[] res = new Object[keys.length];
                for (int i = 0; i < keys.length; i++) {
                    Method m = t.getMethod(keys[i]);
                    res[i]=m.invoke(ans);
                }
                return res;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


}
