package com.tigerobo.search.constant;

import com.tigerobo.search.annotation.MetaAnnotation;
import com.tigerobo.search.entity.InsertMetaDataStatement;
import com.tigerobo.search.entity.MetaDataStatement;
import com.tigerobo.search.entity.Statement;
import com.tigerobo.search.entity.UpdateMetaDataStatement;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum ConstantType {


    SELECT(Constant.select) {
        @Override
        public Statement getStatmentByMethod(MetaAnnotation ma) {
            return new MetaDataStatement((String) ma.getObject("sql"));
        }
    }, INSERT(Constant.insert) {
        @Override
        public Statement getStatmentByMethod(MetaAnnotation ma) {
            return new InsertMetaDataStatement();
        }
    }, UPDATE(Constant.update) {
        @Override
        public Statement getStatmentByMethod(MetaAnnotation ma) {
            return new UpdateMetaDataStatement();
        }
    };
//    DELETE,UPDATE,INSERT,ORDER,GROUP,COLUMN,WHERE,VALUE,LEFTBRACKETS,RIGHTBRACKETS;

    private String value;

    ConstantType(String value) {
        this.value = value;
    }

    public abstract Statement getStatmentByMethod(MetaAnnotation ma);

    public static ConstantType search(Method method) {
        List<ConstantType> constantTypes = Arrays.stream(ConstantType.values()).filter(val -> val.value.indexOf(method.getName()) != -1).collect(Collectors.toList());
        if (constantTypes != null && constantTypes.size() > 0) {
            return constantTypes.get(0);
        }
        return ConstantType.SELECT;
    }
}
