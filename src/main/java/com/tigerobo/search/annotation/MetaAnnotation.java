/**
 *    Copyright ${license.git.copyrightYears} the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package com.tigerobo.search.annotation;


import com.tigerobo.search.utils.Validation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

public class MetaAnnotation {
  public List<Annotation> annotations=new ArrayList<>();
  public Map<String,Object> attribute = new LinkedHashMap<>();

  public <T> T getObject(String key) {
    return (T) attribute.get(key);
  }

  public static Set<String> excludeMethodName = new HashSet<>();

  static{
    excludeMethodName.add("toString");
    excludeMethodName.add("equals");
    excludeMethodName.add("hashCode");
    excludeMethodName.add("annotationType");

  }

  public boolean isEmpty(){
    return attribute.isEmpty();
  }


  public Set<Map.Entry<String, Object>> getAttribute(){
     return attribute.entrySet();
  }


  public Class[] getMetaAnnotationParamterizedTypes(){
    if(attribute!=null && attribute.size()>0){
        List<Class> clzs = attribute.entrySet().stream().map(val->val.getValue().getClass()).filter(val->val!=null).collect(Collectors.toList());
        if(clzs!=null && !clzs.isEmpty()){
          return clzs.toArray(new Class[clzs.size()]);
        }
    }
    return null;
  }
  public Object[] getMetaAnnotationValue(){
    if(annotations!=null && !annotations.isEmpty()){
      parser();
    }
    if(attribute!=null && attribute.size()>0){
      List<Object> clzs = attribute.entrySet().stream().map(val->val.getValue()).filter(val->val!=null).collect(Collectors.toList());
      if(clzs!=null && !clzs.isEmpty()){
        return clzs.toArray(new Object[clzs.size()]);
      }
    }
    return null;
  }


  public <T extends Annotation> T getAnnotations(Class<T>  annotation) {
    for(Annotation an:annotations){
      if(an.getClass().isAssignableFrom(annotation)){
          return (T) an;
      }
    }
    return null;
  }

  public MetaAnnotation parser(){
    for(Object an:annotations){
       Method[] ms = an.getClass().getDeclaredMethods();
       for(Method  m: ms){
         try {
           if(!excludeMethodName.contains(m.getName())){
             Object value = m.invoke(an,new Object[]{});
             if(value!=null){
               if(value.getClass().isAssignableFrom(String.class) && Validation.notEmptyAndBlankStr((String) value)){
                  attribute.put(m.getName(),value);
               }else if(value.getClass().isAssignableFrom(String[].class) && ((String[])value).length >0){
                  attribute.put(m.getName(),value);
               }
             }
           }

         } catch (Exception e) {
            e.printStackTrace();
         }
       }
    }
    return this;
  }

  public MetaAnnotation putMetaValue(String key,Object value){
    if(value !=null){
      attribute.put(key,value);
    }
    return this;
  }
  public <T extends Annotation> MetaAnnotation registerAnnotation( List<T> annotations) {
    if(annotations!=null){
      this.annotations.addAll(annotations);
    }
    return this;
  }
  public <T extends Annotation> MetaAnnotation registerAnnotation( T annotations) {
    if(annotations!=null){
      this.annotations.add(annotations);
    }
    return this;
  }

  public String getId(){
    if(annotations!=null && !annotations.isEmpty()){
      attribute.clear();
      parser();
      if(attribute!=null && !attribute.isEmpty())
        return attribute.entrySet().stream().map(val->String.format("%s:%s",val.getKey(),val.getValue())).collect(Collectors.joining(","));
    }
    return null;
  }




}
