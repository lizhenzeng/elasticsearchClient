package com.tigerobo.search.entity;


public class WrapClass {
    public Object annotation;
    public String bindMethodName;
    public Class clazz;

    public Class getClazz() {
        return clazz;
    }

    public void setClazz(Class clazz) {
        this.clazz = clazz;
    }

    WrapClass(Object an, String bindMethodName, Class clazz){
        this.annotation = an;
        this.bindMethodName = bindMethodName;
        this.clazz = clazz;
    }
    public static class Builder{
        public Object value;
        public Class clzz;
        public String bindMethodName;

        public WrapClass.Builder setClassObject(Object value){
            this.value = value;
            return this;
        }
        public WrapClass.Builder setClass(Class clzz){
            this.clzz = clzz;
            return this;
        }
        public WrapClass.Builder setBindMethodName(String bindMethodName){
            this.bindMethodName = bindMethodName;
            return this;
        }
        public WrapClass build(){
            return  new WrapClass(value,bindMethodName,clzz);
        }
    }

    public Object getAnnotation() {
        return annotation;
    }

    public void setAnnotation(Object annotation) {
        this.annotation = annotation;
    }

    public String getBindMethodName() {
        return bindMethodName;
    }

    public void setBindMethodName(String bindMethodName) {
        this.bindMethodName = bindMethodName;
    }
}
