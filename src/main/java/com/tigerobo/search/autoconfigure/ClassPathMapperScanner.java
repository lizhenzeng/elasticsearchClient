package com.tigerobo.search.autoconfigure;

import com.tigerobo.search.factory.EsClient;
import com.tigerobo.search.factory.MapperFactoryBean;
import com.tigerobo.search.utils.AnnotationInstanceConfigUtils;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.context.annotation.*;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.classreading.MetadataReader;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ClassPathMapperScanner extends ClassPathBeanDefinitionScanner {
    private String resourcePattern = "**/*.class";

    private ResourcePatternResolver resourcePatternResolver;
    private BeanDefinitionRegistry registry;
    private ScopeMetadataResolver scopeMetadataResolver = new AnnotationScopeMetadataResolver();
    private BeanNameGenerator beanNameGenerator;
    private EsClient esClient;

    public EsClient getEsClient() {
        return esClient;
    }

    public void setEsClient(EsClient esClient) {
        this.esClient = esClient;
    }

    private MapperFactoryBean<?> mapperFactoryBean = new MapperFactoryBean<Object>();
    public ClassPathMapperScanner(BeanDefinitionRegistry registry, boolean useDefaultFilters) {
        super(registry, useDefaultFilters);
        beanNameGenerator =  new AnnotationBeanNameGenerator();
        this.registry = registry;
    }


    public List<Class> registerBeanByInterface(List<String> basePackage, Class extendInterface, Map<String,Object> props){
        return basePackage.stream()
                .flatMap(val->registerBeanByInterface(val,extendInterface,props).stream()).filter(val->val!=null).collect(Collectors.toList());
    }

    public List<Class> registerBeanByInterface(String basePackage, Class extendInterface, Map<String,Object> props){
        List<Class> res = new ArrayList<>();
        try {
            String packageSearchPath = "classpath*:" + this.resolveBasePackage(basePackage) + '/' + this.resourcePattern;
            Resource[] resources = this.getResourcePatternResolver().getResources(packageSearchPath);
            List<ScannedGenericBeanDefinition> sgbds =  Arrays.stream(resources).map(resource->{
                try {
                    if(resource.isReadable()){
                        MetadataReader metadataReader = this.getMetadataReaderFactory().getMetadataReader(resource);
                        ScannedGenericBeanDefinition sbd = new ScannedGenericBeanDefinition(metadataReader);
                        sbd.setSource(resource);
                        for(String interfaceName:sbd.getMetadata().getInterfaceNames()){
                            if(interfaceName.equalsIgnoreCase(extendInterface.getName())){
                                return sbd;
                            }
                        }

                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
                return null;
            }).filter(val->val!=null).collect(Collectors.toList());


            for(ScannedGenericBeanDefinition sgbd:sgbds){
                BeanDefinition candidate = sgbd;
                ScopeMetadata scopeMetadata = this.scopeMetadataResolver.resolveScopeMetadata(candidate);
                candidate.setScope(scopeMetadata.getScopeName());
                String beanName = this.beanNameGenerator.generateBeanName(candidate, this.registry);
                if (candidate instanceof AbstractBeanDefinition) {
                    this.postProcessBeanDefinition((AbstractBeanDefinition)candidate, beanName);
                }

                if (candidate instanceof AnnotatedBeanDefinition) {
                    AnnotationConfigUtils.processCommonDefinitionAnnotations((AnnotatedBeanDefinition)candidate);
                }
                sgbd.getConstructorArgumentValues().addGenericArgumentValue(sgbd.getBeanClassName()); // issue #59
                sgbd.getConstructorArgumentValues().addGenericArgumentValue(esClient); // issue #59
                sgbd.setBeanClass(this.mapperFactoryBean.getClass());

                if(props!=null&& props.size()>0){
                    props.entrySet().forEach(val->candidate.getPropertyValues().add(val.getKey(),val.getValue()));
                }

                if (this.checkCandidate(beanName, candidate)) {
                    BeanDefinitionHolder definitionHolder = new BeanDefinitionHolder(candidate, beanName);
                    definitionHolder = AnnotationInstanceConfigUtils.applyScopedProxyMode(scopeMetadata, definitionHolder, this.registry);
                    this.registerBeanDefinition(definitionHolder, this.registry);
                    res.add( Class.forName(candidate.getBeanClassName()));
                }
            }

        }catch (Exception e){
            e.printStackTrace();
        }
        return res;
    }

    ResourcePatternResolver getResourcePatternResolver() {
        if (this.resourcePatternResolver == null) {
            this.resourcePatternResolver = new PathMatchingResourcePatternResolver();
        }
        return this.resourcePatternResolver;
    }


}
