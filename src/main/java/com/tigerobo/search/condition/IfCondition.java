package com.tigerobo.search.condition;

import com.tigerobo.search.constant.Constant;
import com.tigerobo.search.entity.MetaDataStatement;
import com.tigerobo.search.parser.OgnalHelper;
import com.tigerobo.search.validation.StringValidationUtils;

public class IfCondition extends AbstractCondition implements BaseCondition {


    public IfCondition() {
        super(IFREG, ENDIFREG, new String[]{TESTREG});
    }

    public String getTestJudgeStr(String s) {
        return s.substring(s.indexOf('"') + 1, s.lastIndexOf('"'));
    }

    public String conditionIfTrue(String sql, MetaDataStatement metaDataStatement) {
        String value = OgnalHelper.getKeyByArgs(metaDataStatement, String.format("{%s}", getTestJudgeStr(getAttributes().get(0))));
        try {
            sql = StringValidationUtils.removeNRT(sql);
            if (Boolean.valueOf(value)) {
                sql = StringValidationUtils.replaceValue(sql, StringValidationUtils.removeNRT(getTotalStr()), StringValidationUtils.removeNRT(getConditionIfTrue()));
            } else {
                sql = StringValidationUtils.replaceValue(sql, StringValidationUtils.removeNRT(getTotalStr()), Constant.empty);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return sql;
    }


}
