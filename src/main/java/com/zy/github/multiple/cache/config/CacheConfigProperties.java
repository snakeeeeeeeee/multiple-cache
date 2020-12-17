package com.zy.github.multiple.cache.config;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.LinkedList;
import java.util.List;

/**
 * @author z
 * @date 2020/11/15
 */
@Data
@ConfigurationProperties(prefix = "multiple-cache", ignoreInvalidFields = true)
public class CacheConfigProperties {

    private List<CaffeineCacheConfig> caffeine = new LinkedList<>();
    private List<RedisCacheConfig> redis = new LinkedList<>();
    private List<MultipleCacheConfig> multiple = new LinkedList<>();


    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CaffeineCacheConfig {

        /**
         * 缓存名称
         */
        protected String name;

        /**
         * 缓存策略
         */
        protected String strategy;

        /**
         * 缓存访问后多久过期
         */
        protected long expireAfterAccess = 3600;

        /**
         * 缓存写入后多久过期
         */
        protected long expireAfterWrite;

        /**
         * 缓存容量最大值
         */
        protected int maximumSize = 200;

        /**
         * 初始缓存容量大小
         */
        protected int initialCapacity = 10;

        /**
         * 缓存加载器
         */
        protected String cacheLoader;

        /**
         * 缓存装饰策略
         */
        protected String decorators;

        /**
         * 是否开启本地缓存同步
         */
        protected boolean disableSync = true;
        /**
         * 是否启用软引用
         */
        protected boolean enableSoftRef = false;

    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class RedisCacheConfig {
        /**
         * 缓存名称
         */
        protected String name;

        /**
         * 过期时间
         */
        protected long expire;

        /**
         * 缓存策略(bean 名称首字母小写)
         */
        protected String strategy;

        /**
         * 缓存重构策略
         */
        protected String decorators;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class MultipleCacheConfig {

        /**
         * 缓存名称
         */
        protected String name;

        /**
         * Caffeine配置
         */
        protected CaffeineCacheConfig caffeine;

        /**
         * Redis配置
         */
        protected RedisCacheConfig redis;

        /**
         * 缓存重构策略
         */
        protected String decorators;
    }
}
