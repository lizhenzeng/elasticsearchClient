package com.tigerobo.search.autoconfigure;


import com.tigerobo.search.annotation.EsMapper;
import com.tigerobo.search.bind.BaseMapperInterface;
import com.tigerobo.search.config.EsConfiguration;
import com.tigerobo.search.factory.EsClient;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.boot.autoconfigure.AutoConfigurationPackages;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;

import java.util.List;
@org.springframework.context.annotation.Configuration
@AutoConfigureAfter({EsClient.class})
public class MapperAutoConfiguration  implements BeanFactoryAware, ImportBeanDefinitionRegistrar, ResourceLoaderAware, EnvironmentAware {
    private BeanFactory beanFactory;

    private ResourceLoader resourceLoader;

    private Environment environment;


    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        ClassPathMapperScanner scanner = new ClassPathMapperScanner(registry,false);
        List<String> packages = AutoConfigurationPackages.get(this.beanFactory);
        scanner.setEsClient(beanFactory.getBean(EsClient.class));
        List<Class> conditions = scanner.registerBeanByInterface(packages, BaseMapperInterface.class,null);

    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

}
