package com.tigerobo.search.entity;


import com.tigerobo.search.constant.Constant;
import com.tigerobo.search.parser.OgnalHelper;
import com.tigerobo.search.utils.Validation;

public class SymbolMeta extends Element {

    private MetaDataStatement metaDataStatement;

    private String groupName;
    private String detailName;
    private String maxValue;
    private String minValue;

    public SymbolMeta(String paramterName, String compareValue, ElementTypeEnum type, MetaDataStatement metaDataStatement) {
        super(paramterName, compareValue, type);
        this.metaDataStatement = metaDataStatement;
    }
    public SymbolMeta(String paramterName,String maxValue,String minValue, ElementTypeEnum type, MetaDataStatement metaDataStatement) {
        super(type);
        setParamterName(paramterName);
        this.maxValue = maxValue;
        this.minValue = minValue;
        this.metaDataStatement = metaDataStatement;
    }
    public void proccessor(){
        if(Validation.notEmptyAndBlankStr(getCompareValue())){
            setCompareValue(OgnalHelper.getKeyByArgs(metaDataStatement,String.format("{%s}",getCompareValue())));
        }
        String paramterName = getParamterName();
        if(Validation.notEmptyAndBlankStr(paramterName) && paramterName.contains(Constant.vertical)){
            groupName = paramterName.split(Constant.verticalREG)[0];
            detailName = paramterName.split(Constant.verticalREG)[1];
        }
        if(Validation.notEmptyAndBlankStr(getMaxValue())){
            setMaxValue(OgnalHelper.getKeyByArgs(metaDataStatement,String.format("{%s}",getMaxValue())));
        }
        if(Validation.notEmptyAndBlankStr(getMinValue())){
            setMinValue(OgnalHelper.getKeyByArgs(metaDataStatement,String.format("{%s}",getMinValue())));
        }
    }

    public String getGroupName() {
        return groupName;
    }

    public String getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(String maxValue) {
        this.maxValue = maxValue;
    }

    public String getMinValue() {
        return minValue;
    }

    public void setMinValue(String minValue) {
        this.minValue = minValue;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getDetailName() {
        return detailName;
    }

    public void setDetailName(String detailName) {
        this.detailName = detailName;
    }
}
