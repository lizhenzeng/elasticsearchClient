package com.tigerobo.search.parser;

import com.tigerobo.search.entity.MetaDataStatement;

public enum CommandOperator {
    SELECT{
        @Override
        void operation(MetaDataStatement mds) {

        }

        @Override
        boolean needOperator(MetaDataStatement mds) {
            return false;
        }
    };



    CommandOperator() {

    }


    abstract void operation(MetaDataStatement mds);

    abstract boolean needOperator(MetaDataStatement mds);
}
