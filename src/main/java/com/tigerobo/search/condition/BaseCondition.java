package com.tigerobo.search.condition;

public interface BaseCondition {
     String IFREG = "<if.*>";
    String ENDIFREG = "</.*if>";
     String TESTREG = "test=\".*\"";
     String conditionFragmentReg="<if.*>";
     String conditionJudgeReg="(\"|')((\\{)?#.*(==|!=|<=|>=|<>|<|>)+.*)(\\1)";
     String conditionSuccessFragmentReg=">(.*)<";
}
