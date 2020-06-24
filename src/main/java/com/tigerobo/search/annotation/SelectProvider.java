package com.tigerobo.search.annotation;

import org.springframework.beans.factory.annotation.Lookup;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD})
@Documented
@Inherited
public @interface SelectProvider {

    String sql();
}
