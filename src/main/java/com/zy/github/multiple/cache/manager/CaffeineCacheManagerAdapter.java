package com.zy.github.multiple.cache.manager;

import com.github.benmanes.caffeine.cache.CacheLoader;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.zy.github.multiple.cache.decorators.CacheDecorationBuilder;
import com.zy.github.multiple.cache.strategys.CacheStrategy;
import com.zy.github.multiple.cache.strategys.AbstractCaffeineCacheStrategy;
import com.zy.github.multiple.cache.CacheDecorationHandler;
import com.zy.github.multiple.cache.sync.CacheSyncManager;
import lombok.Data;
import org.springframework.cache.Cache;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.caffeine.CaffeineCacheManager;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class CaffeineCacheManagerAdapter extends CaffeineCacheManager {
    private Map<String, CacheConfig> configMap = new HashMap<>();

    private final Map<String, AbstractCaffeineCacheStrategy> cacheStrategyMap;
    private final Map<String, Set<CacheDecorationHandler>> decorationHandlers;

    private final CacheSyncManager cacheSyncManager;

    @Data
    public static class CacheConfig {

        /**
         * 缓存名称
         */
        private String name;

        /**
         * 缓存过期时间(最后一次访问时间后)
         */
        private long expireAfterAccess;

        /**
         * 缓存过期时间(无论是否访问)
         */
        private long expireAfterWrite;

        /**
         * 缓存最大长度
         */
        private int maximumSize;

        /**
         * 初始缓存长度
         */
        private int initialCapacity;

        /**
         * 是否启用软引用
         */
        private boolean enableSoftRef = false;

        /**
         * 是否开启缓存同步策略
         */
        private boolean disableSync = true;

        /**
         * 缓存加载器
         */
        private CacheLoader cacheLoader;
    }


    public CaffeineCacheManagerAdapter(Collection<String> cacheNames, Collection<CacheConfig> cacheConfigs,
                                       Map<String, AbstractCaffeineCacheStrategy> cacheStrategyMap,
                                       Map<String, Set<CacheDecorationHandler>> decorationHandlers,
                                       CacheSyncManager cacheSyncManager) {
        super();
        this.cacheStrategyMap = cacheStrategyMap;
        this.decorationHandlers = decorationHandlers;
        for (CacheConfig cacheConfig : cacheConfigs) {
            configMap.put(cacheConfig.getName(), cacheConfig);
        }
        this.cacheSyncManager = cacheSyncManager;
        setCacheNames(cacheNames);
    }


    @Override
    public com.github.benmanes.caffeine.cache.Cache<Object, Object> createNativeCaffeineCache(String name) {
        if (configMap.containsKey(name)) {
            CacheConfig cacheConfig = configMap.get(name);

            Caffeine caffeine = Caffeine.newBuilder()
                    .initialCapacity(cacheConfig.getInitialCapacity())
                    .maximumSize(cacheConfig.getMaximumSize());
            if (cacheConfig.getExpireAfterWrite() > 0) {
                caffeine.expireAfterWrite(cacheConfig.getExpireAfterWrite(), TimeUnit.SECONDS);
            } else if (cacheConfig.getExpireAfterAccess() > 0) {
                caffeine.expireAfterAccess(cacheConfig.getExpireAfterAccess(), TimeUnit.SECONDS);
            }

            if (cacheConfig.isEnableSoftRef()) {
                caffeine.softValues();
            }

            caffeine.recordStats();
            if (cacheConfig.getCacheLoader() == null) {
                return caffeine.build();
            } else {
                return caffeine.build(cacheConfig.getCacheLoader());
            }
        }
        return Caffeine.newBuilder().build();
    }


    @Override
    protected Cache createCaffeineCache(String name) {
        Cache result = new CaffeineCache(name, createNativeCaffeineCache(name), isAllowNullValues());
        CacheStrategy cacheStrategy = null == cacheStrategyMap ? null : cacheStrategyMap.get(name);
        CacheConfig cacheConfig = configMap.get(name);
        return CacheDecorationBuilder.newBuilder(result, decorationHandlers.get(name))
                //自定义的存储策略
                .customCacheStrategy(cacheStrategy,
                        cacheConfig.getExpireAfterAccess() > 0 ? cacheConfig.getExpireAfterAccess() : cacheConfig.getExpireAfterWrite())
                //本地缓存同步
                .localCacheSync(cacheConfig.disableSync, cacheSyncManager)
                .valueRebuild()
                .build();
    }
}
