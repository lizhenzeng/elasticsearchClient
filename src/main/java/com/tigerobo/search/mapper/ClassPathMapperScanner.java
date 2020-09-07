package com.tigerobo.search.mapper;

import com.tigerobo.search.annotation.SelectProvider;
import com.tigerobo.search.factory.MapperFactoryBean;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.annotation.Lookup;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.context.annotation.ScannedGenericBeanDefinition;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.core.type.filter.TypeFilter;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Set;

public class ClassPathMapperScanner extends ClassPathBeanDefinitionScanner {

    private Class<? extends Annotation> annotationClass;

    public Class<? extends Annotation> getAnnotationClass() {
        return annotationClass;
    }

    public void setAnnotationClass(Class<? extends Annotation> annotationClass) {
        this.annotationClass = annotationClass;
    }

    private MapperFactoryBean<?> mapperFactoryBean = new MapperFactoryBean<Object>();

    public ClassPathMapperScanner(BeanDefinitionRegistry registry) {
        super(registry,false);
    }



    public void registerFilter(){


            // default include filter that accepts all classes
        addIncludeFilter(new TypeFilter() {
            @Override
            public boolean match(MetadataReader metadataReader, MetadataReaderFactory metadataReaderFactory) throws IOException {
                return true;
            }
        });

    }


    public Set<BeanDefinitionHolder> doScan(String... basePackage){
        Set<BeanDefinitionHolder> beanDefinitions = super.doScan(basePackage);

        if(beanDefinitions.isEmpty()){
            logger.warn("No es mapper was found in "+ Arrays.toString(basePackage)+" package");
        }else{
            processBeanDefinition(beanDefinitions);
        }
        return beanDefinitions;
    }

    private void processBeanDefinition(Set<BeanDefinitionHolder>  beanDefinitionHolders){
        GenericBeanDefinition definition;
        for(BeanDefinitionHolder holder:beanDefinitionHolders){
            definition = (GenericBeanDefinition) holder.getBeanDefinition();

            if(logger.isDebugEnabled()){
                logger.debug("Create MapperFactoryBean with name'"+holder.getBeanName()+
                        "' and '"+definition.getBeanClassName() +"' mapperInterface");
            }

            definition.getConstructorArgumentValues().addGenericArgumentValue(definition.getBeanClassName());
            definition.setBeanClass(this.mapperFactoryBean.getClass());
            definition.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_BY_NAME);

        }
    }



    @Override
    protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
        AnnotationMetadata metadata = beanDefinition.getMetadata();
        return metadata.isIndependent() && (metadata.isConcrete() || metadata.isAbstract() && metadata.hasAnnotatedMethods(annotationClass.getName()));
    }
}
