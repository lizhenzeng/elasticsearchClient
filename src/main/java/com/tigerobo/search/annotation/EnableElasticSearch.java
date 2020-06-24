package com.tigerobo.search.annotation;

import com.tigerobo.search.autoconfigure.MapperAutoConfiguration;
import com.tigerobo.search.config.EsConfiguration;
import com.tigerobo.search.factory.EsClient;
import com.tigerobo.search.factory.MapperFactoryBean;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Documented
@Import({MapperAutoConfiguration.class, EsConfiguration.class, EsClient.class})
public @interface EnableElasticSearch {
}
