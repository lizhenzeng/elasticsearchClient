package com.tigerobo.search.condition;

public class AbstractCondition {



    private Integer startIndex;
    private Integer endIndex;
    private String originalSql;
    private String conditionFragmentReg;
    private String conditionJudgeReg;
    private String conditionSuccessFragmentReg;
    private String conditionFragmentStr;
    private String conditionJudgeStr;
    private String conditionSuccessFragmentStr;

    public AbstractCondition(String conditionFragmentReg,String conditionJudgeReg,String conditionSuccessFragmentReg){
        this.conditionFragmentReg=conditionFragmentReg;
        this.conditionJudgeReg = conditionJudgeReg;
        this.conditionSuccessFragmentReg = conditionSuccessFragmentReg;
    }

    public String getOriginalSql() {
        return originalSql;
    }

    public void setOriginalSql(String originalSql) {
        this.originalSql = originalSql;
    }

    public Integer getStartIndex() {
        return startIndex;
    }

    public void setStartIndex(Integer startIndex) {
        this.startIndex = startIndex;
    }

    public Integer getEndIndex() {
        return endIndex;
    }

    public void setEndIndex(Integer endIndex) {
        this.endIndex = endIndex;
    }

    public String getConditionFragmentReg() {
        return conditionFragmentReg;
    }

    public void setConditionFragmentReg(String conditionFragmentReg) {
        this.conditionFragmentReg = conditionFragmentReg;
    }

    public String getConditionJudgeReg() {
        return conditionJudgeReg;
    }

    public void setConditionJudgeReg(String conditionJudgeReg) {
        this.conditionJudgeReg = conditionJudgeReg;
    }

    public String getConditionSuccessFragmentReg() {
        return conditionSuccessFragmentReg;
    }

    public void setConditionSuccessFragmentReg(String conditionSuccessFragmentReg) {
        this.conditionSuccessFragmentReg = conditionSuccessFragmentReg;
    }

    public String getConditionFragmentStr() {
        return conditionFragmentStr;
    }

    public void setConditionFragmentStr(String conditionFragmentStr) {
        if(originalSql!=null && originalSql.length()>0){
            setStartIndex(originalSql.indexOf(conditionFragmentStr));
            setEndIndex(startIndex+conditionFragmentStr.length());
        }
        this.conditionFragmentStr = conditionFragmentStr;
    }

    public String getConditionJudgeStr() {
        return conditionJudgeStr;
    }

    public void setConditionJudgeStr(String conditionJudgeStr) {
        this.conditionJudgeStr = conditionJudgeStr.substring(1,conditionJudgeStr.length()-1);;;
    }

    public String getConditionSuccessFragmentStr() {
        return conditionSuccessFragmentStr;
    }

    public void setConditionSuccessFragmentStr(String conditionSuccessFragmentStr) {
        this.conditionSuccessFragmentStr = conditionSuccessFragmentStr.substring(1,conditionSuccessFragmentStr.length()-1);;
    }
}
