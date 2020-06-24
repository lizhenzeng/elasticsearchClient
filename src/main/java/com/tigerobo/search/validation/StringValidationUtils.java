package com.tigerobo.search.validation;

public class StringValidationUtils {

    public static Boolean isNotBlank(String str){
        if(str!=null && str.trim().length()>0 && !str.trim().equalsIgnoreCase("")){
            return true;
        }
        return false;
    }
    public static String removeNRT(String originalSql){
        return originalSql.replaceAll("(\\\r\\\n|\\\r|\\\n|\\\n\\\r|\\\t)", "");
    }

    //

    public static String replaceValue(String target,String reg,String replaceValue){
        int index = target.indexOf(reg);
        if(index!=-1){
            StringBuffer sb = new StringBuffer();
            sb.append(target.substring(0,index)).append(replaceValue).append(target.substring(index+reg.length()));
            return sb.toString();
        }
        return target;
    }
}
