package com.tigerobo.search.condition;

import java.util.List;

public class AbstractCondition {
    private String startReg;
    private String startValue;
    private String endReg;
    private String endValue;
    private String conditionIfTrue;
    private String[] attributesReg;
    private List<String> attributes;
    private String totalStr;

    public AbstractCondition(String startReg,String endReg,String[] attributesReg){
        this.startReg = startReg;
        this.endReg = endReg;
        this.attributesReg = attributesReg;
    }

    public String getTotalStr() {
        return totalStr;
    }

    public void setTotalStr(String totalStr) {
        this.totalStr = totalStr;
    }

    public String getStartReg() {
        return startReg;
    }

    public String[] getAttributesReg() {
        return attributesReg;
    }

    public void setAttributesReg(String[] attributesReg) {
        this.attributesReg = attributesReg;
    }

    public void setStartReg(String startReg) {
        this.startReg = startReg;
    }

    public String getEndReg() {
        return endReg;
    }

    public void setEndReg(String endReg) {
        this.endReg = endReg;
    }

    public List<String> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<String> attributes) {
        this.attributes = attributes;
    }

    public String getStartValue() {
        return startValue;
    }

    public void setStartValue(String startValue) {
        this.startValue = startValue;
    }

    public String getEndValue() {
        return endValue;
    }

    public void setEndValue(String endValue) {
        this.endValue = endValue;
    }

    public String getConditionIfTrue() {
        return conditionIfTrue;
    }

    public void setConditionIfTrue(String conditionIfTrue) {
        this.conditionIfTrue = conditionIfTrue;
    }
}
