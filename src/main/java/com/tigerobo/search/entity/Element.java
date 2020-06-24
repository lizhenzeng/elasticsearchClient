package com.tigerobo.search.entity;

import com.tigerobo.search.constant.ConstantType;
import org.elasticsearch.index.query.QueryBuilder;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Element implements Iterator<Element> {




    private String prefix;
    private Element suffix;
    private String paramterName;
    private String compareValue;
    private ElementTypeEnum type;
    private List<Element> selfrelation = new ArrayList<>();
    private int index = -1;
    private QueryBuilder qu;

    public Element(ElementTypeEnum type){
        this(null,null,null,null,type);
    }

    public Element(String paramterName,String compareValue,ElementTypeEnum type){
         this(null,null,paramterName,compareValue,type);
    }

    public Element(String prefix,Element suffix,String paramterName,String compareValue,ElementTypeEnum type){
        this.prefix = prefix;
        this.suffix = suffix;
        this.paramterName =paramterName;
        this.compareValue = compareValue;
        this.type = type;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public void setSuffix(Element suffix) {
        this.suffix = suffix;
    }

    public void setType(ElementTypeEnum type) {
        this.type = type;
    }

    public void setParamterName(String paramterName) {
        this.paramterName = paramterName;
    }

    public void setCompareValue(String compareValue) {
        this.compareValue = compareValue;
    }

    public QueryBuilder getQu() {
        return qu;
    }

    public void setQu(QueryBuilder qu) {
        this.qu = qu;
    }

    public Boolean addElement(Element element){
        return selfrelation.add(element) ;
    }

    public List<Element> getElements(){
        return selfrelation;
    }

    public void clear(){
        selfrelation.clear();
    }

    public Boolean addAllElement(List<Element> elements){
        return selfrelation.addAll(elements);
    }

    @Override
    public boolean hasNext() {
        int len = selfrelation.size();
        index = index + 1;
        if(len > index){
            return true;
        }else{
            index = -1;
            return false;
        }
    }

    @Override
    public Element next() {
        return selfrelation.get(index);
    }

    public int size(){
        return selfrelation.size();
    }

    public void remove(Element element){
        selfrelation.remove(element);
        index = index -1;
    }
    public String getPrefix() {
        return prefix;
    }

    public Element getSuffix() {
        return suffix;
    }

    public String getParamterName() {
        return paramterName;
    }

    public String getCompareValue() {
        return compareValue;
    }

    public ElementTypeEnum getType() {
        return type;
    }

    @Override
    public boolean equals(Object obj) {
        if(this == obj){
            return true;//地址相等
        }

        if(obj == null){
            return false;//非空性：对于任意非空引用x，x.equals(null)应该返回false。
        }
//        private String prefix;
//        private Element suffix;
//        private String paramterName;
//        private String compareValue;
        if(obj instanceof Element){
            Element other = (Element) obj;
            //需要比较的字段相等，则这两个对象相等
            if(this.prefix!=null && other.prefix!=null
                    && this.prefix.contentEquals(other.prefix)
                    && this.paramterName!=null && other.paramterName!=null
                    && this.paramterName.contentEquals(other.paramterName)
                    && this.compareValue!=null && other.compareValue!=null
                    && this.compareValue.contentEquals(other.compareValue)
            ){
                return true;
            }
        }

        return false;
    }
    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + (prefix == null ? 0 : prefix.hashCode());
        result = 31 * result + (paramterName == null ? 0 : paramterName.hashCode());
        result = 31 * result + (suffix == null ? 0 : suffix.hashCode());
        result = 31 * result + (compareValue == null ? 0 : compareValue.hashCode());
        return result;
    }
}
