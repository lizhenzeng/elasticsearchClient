package com.tigerobo.search.entity;

import java.util.ArrayList;
import java.util.List;

public class ElementMeta extends Element{


    List<FunctionMeta> functionList = new ArrayList<>();


    public ElementMeta(String paramterName, String compareValue, ElementTypeEnum type) {
        super(paramterName, compareValue, type);
    }
}
