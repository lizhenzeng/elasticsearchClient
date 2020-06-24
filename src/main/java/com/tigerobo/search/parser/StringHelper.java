package com.tigerobo.search.parser;

import com.tigerobo.search.validation.StringValidationUtils;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class StringHelper {

    public static <T> T[]  addValueByIndex(T[] origin,T[] value,Integer index,Class<T> tClass){
        if(0<=index && index<origin.length){
            List<T> newInstanceT = new ArrayList();
            for(int i=0;i<index;i++){
                newInstanceT.add(origin[i]);
            }
            for(T t:value){
                newInstanceT.add(t);
            }
            for(int i = index+1;i<origin.length;i++){
                newInstanceT.add(origin[i]);
            }
            return newInstanceT.toArray((T[]) Array.newInstance(tClass , origin.length + value.length -1));
        }
        return null;
    }

    public static String[]  splitAddSplitSymbol(String str,String splitSymbol){

        List<String> result = new ArrayList<>();
        splitAddSplitSymbol(str,splitSymbol,result);
        return result.toArray(new String[result.size()]);
    }


    public static void  splitAddSplitSymbol(String str,String splitSymbol,List<String> result){
        int index = str.indexOf(splitSymbol);
        if(index == -1){
            if(StringValidationUtils.isNotBlank(str)){
                result.add(str);
            }
            return;
        }else{
            String begin = str.substring(0,index);
            if(StringValidationUtils.isNotBlank(begin)){
                result.add(begin);
            }
            result.add(str.substring(index,index+splitSymbol.length()));
            splitAddSplitSymbol(str.substring(index+splitSymbol.length()),splitSymbol,result);
        }

    }
}
