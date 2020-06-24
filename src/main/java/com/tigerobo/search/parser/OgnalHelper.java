package com.tigerobo.search.parser;


import com.tigerobo.search.entity.MetaDataStatement;
import com.tigerobo.search.entity.Parameter;
import com.tigerobo.search.utils.Validation;
import ognl.Ognl;
import ognl.OgnlContext;

import java.util.*;
import java.util.stream.Collectors;

public class OgnalHelper {

    public static Collection<Character> tokenSymbol = Collections.unmodifiableList(Arrays.asList('{','}'));

    public static String getKeyByArgs(MetaDataStatement metaDataStatement , String ognalStr)  {
        List<Parameter> ps = metaDataStatement.getPs();
        Map<String,Object> argsMap = new HashMap<>();
        ps.forEach(val->argsMap.put(val.getName(),val.getValue()));
        String[] psNames = ps.stream().map(val->val.getName()).collect(Collectors.toList()).toArray(new String[ps.size()]);

        try {
            OgnlContext context = new OgnlContext(argsMap);
            return getToken( ognalStr,context,psNames);

        }catch (Exception e){
            throw new OgnlException(String.format("parser ognl str is error stack is %s",e.getMessage()));
        }
    }

    public static String getToken(String expressStr,OgnlContext context ,String[] ps ){
        if((Validation.notEmptyAndBlankStr(expressStr) && isNeedOgnl(expressStr,tokenSymbol) )|| isContainArgsName(expressStr,ps)){
            return getTokenList(expressStr,context);
        }else{
            return expressStr;
        }
    }

    public static boolean isNeedOgnl(String value,Collection<Character> symbol){
        for(Character s:symbol){
            if(value.contains(String.valueOf(s))){
                return true;
            }
        }
        return false;
    }

    public static boolean isContainArgsName(String express,String[] args){
        if(Validation.notEmptyAndBlankStr(express)){
            for(String arg:args){
                if(express.contains(arg)){
                    return true;
                }
            }
        }
        return false;
    }


    public static String getTokenList(String value, OgnlContext context )  {
        if(Validation.notEmptyAndBlankStr(value)){
            Set<String> res = new HashSet<>();
            getSplitTokenList("{","}",value,res,0,value.length()-1);
            Map<String,String> replaceToken = new HashMap<>();
            res.forEach(val->{
                try {
                    Object express = Ognl.parseExpression(val);
                    String keyStr = String.valueOf(Ognl.getValue(express,context,context.getRoot()));
                    replaceToken.put(val,keyStr);
                }catch (Exception e){}
            });

            return  getKeyByTokenValues(replaceToken,value);
        }
        return value;
    }

    public static void getSplitTokenList(String prefix,String suffix,String value,Set<String> res,int begin,int end){
       int startIndex = value.indexOf(prefix,begin);
       int endIndex = value.indexOf(suffix,begin);
       if(startIndex != -1 && endIndex != -1){
           if(startIndex + 1 < endIndex)
           res.add(value.substring(startIndex + 1,endIndex));
           begin = endIndex + 1;
           if(begin < end){
               getSplitTokenList(prefix,suffix,value,res,begin,end);
           }
       }
    }


    public static String getKeyByTokenValues(Map<String,String> map , String value){
        for(Map.Entry<String,String> entry:map.entrySet()){
//            value = value.replaceAll(entry.getKey(),entry.getValue());
            value =  entry.getValue();
        }
        return value;
    }

}
