package com.tigerobo.search.bind;

import com.tigerobo.search.annotation.Param;

public interface BaseMapperInterface<S> {

    S insert(@Param(value="s") S s);
    S updateByPrimaryKey(@Param(value="s")S s);
}
