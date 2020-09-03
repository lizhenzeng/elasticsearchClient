package com.tigerobo.search.condition;

import com.tigerobo.search.constant.Constant;
import com.tigerobo.search.entity.MetaDataStatement;
import com.tigerobo.search.parser.OgnalHelper;

import java.util.Arrays;
import java.util.stream.Collectors;

public class IfCondition extends AbstractCondition implements BaseCondition {


    public IfCondition() {
        super(conditionFragmentReg, conditionJudgeReg, conditionSuccessFragmentReg);
    }


    public String conditionIfTrue(String sql, MetaDataStatement metaDataStatement) {
        String value = OgnalHelper.getKeyByArgs(metaDataStatement, getConditionJudgeStr());
        try {
            setOriginalSql(sql);
            String successFragmentSql = makeEnoughLengthSpace(getConditionFragmentStr().length(), Constant.blank, null);
            if (Boolean.valueOf(value)) {
                successFragmentSql = makeEnoughLengthSpace(getConditionFragmentStr().length(), Constant.blank, getConditionSuccessFragmentStr());
            }
            sql = String.format("%s%s%s", sql.substring(0, getStartIndex()), successFragmentSql, sql.substring(getEndIndex()));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return sql;
    }

    public String makeEnoughLengthSpace(Integer length, String value, String containerValue) {
        if (containerValue != null && containerValue.length() > 0 && length > containerValue.length()) {
            length = length - containerValue.length();
            return Arrays.stream(new Integer[length]).map(val -> value).collect(Collectors.joining()) + containerValue;
        }
        return Arrays.stream(new Integer[length]).map(val -> value).collect(Collectors.joining());
    }

}
