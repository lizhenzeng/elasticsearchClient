package com.tigerobo.search.utils;

import java.util.Collection;

public class Validation {

    public static boolean notEmptyAndBlankStr(String value){
        return value!=null && !value.trim().equalsIgnoreCase("")&& !value.trim().equalsIgnoreCase("null");
    }

    public static boolean collectNotEmpty(Collection c){
        return c!=null && !c.isEmpty();
    }

    public static boolean equalTrimIgnoreCase(String value,String value1){
        return value.trim().equalsIgnoreCase(value1.trim());
    }
}
