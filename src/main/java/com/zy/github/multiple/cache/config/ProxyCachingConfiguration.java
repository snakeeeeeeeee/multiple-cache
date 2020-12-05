package com.zy.github.multiple.cache.config;

import com.zy.github.multiple.cache.interceptor.CacheInterceptorCustom;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.cache.annotation.AbstractCachingConfiguration;
import org.springframework.cache.annotation.AnnotationCacheOperationSource;
import org.springframework.cache.config.CacheManagementConfigUtils;
import org.springframework.cache.interceptor.BeanFactoryCacheOperationSourceAdvisor;
import org.springframework.cache.interceptor.CacheInterceptor;
import org.springframework.cache.interceptor.CacheOperationSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Role;
import org.springframework.core.Ordered;

//@Configuration
public class ProxyCachingConfiguration extends AbstractCachingConfiguration {

    @Bean(name = CacheManagementConfigUtils.CACHE_ADVISOR_BEAN_NAME)
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    public BeanFactoryCacheOperationSourceAdvisor cacheAdvisor() {
        BeanFactoryCacheOperationSourceAdvisor advisor =
                new BeanFactoryCacheOperationSourceAdvisor();
        advisor.setCacheOperationSource(cacheOperationSource());
        advisor.setAdvice(cacheInterceptor());
        advisor.setOrder(Ordered.LOWEST_PRECEDENCE - 1);
        return advisor;
    }

    @Bean
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    public CacheOperationSource cacheOperationSource() {
        return new AnnotationCacheOperationSource();
    }

    @Bean
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    public CacheInterceptor cacheInterceptor() {
        CacheInterceptor interceptor = new CacheInterceptorCustom();
        interceptor.setCacheOperationSources(cacheOperationSource());
        if (this.cacheResolver != null) {
            interceptor.setCacheResolver(this.cacheResolver.get());
        }
        else if (this.cacheManager != null) {
            interceptor.setCacheManager(this.cacheManager.get());
        }
        if (this.keyGenerator != null) {
            interceptor.setKeyGenerator(this.keyGenerator.get());
        }
        if (this.errorHandler != null) {
            interceptor.setErrorHandler(this.errorHandler.get());
        }
        return interceptor;
    }
}
