package com.zy.github.multiple.cache.manager;

import com.zy.github.multiple.cache.decorators.CacheDecorationBuilder;
import com.zy.github.multiple.cache.strategys.AbstractRedisCacheStrategy;
import com.zy.github.multiple.cache.CacheDecorationHandler;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheWriter;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author zy 2020/11/29
 */
public class RedisCacheManagerAdapter implements CacheManager {
    private final ConcurrentMap<String, Cache> cacheMap = new ConcurrentHashMap<>(16);
    private Map<String, AbstractRedisCacheStrategy> cacheStrategyMap;
    private Map<String, Set<CacheDecorationHandler>> decorationHandlers;
    private RedisCacheManagerProxy cacheManagerProxy;
    private Map<String, RedisCacheConfiguration> expireMap;
    private Set<String> cacheNames;

    public RedisCacheManagerAdapter(RedisCacheWriter cacheWriter, Set<String> cacheNames,
                                    boolean cacheNullValues, Map<String, RedisCacheConfiguration> expireMap,
                                    Map<String, AbstractRedisCacheStrategy> cacheStrategyMap,
                                    Map<String, Set<CacheDecorationHandler>> decorationHandlers) {
        this.cacheStrategyMap = cacheStrategyMap;
        this.decorationHandlers = decorationHandlers;
        this.cacheNames = cacheNames;
        this.expireMap = expireMap;
        RedisCacheConfiguration defaultCacheConfiguration = RedisCacheConfiguration.defaultCacheConfig();
        cacheManagerProxy = new RedisCacheManagerProxy(cacheWriter, defaultCacheConfiguration, expireMap, cacheNullValues);
    }

    public void initCaches() {

        cacheManagerProxy.loadCaches().forEach(item -> {
            Cache storeCache = CacheDecorationBuilder.newBuilder(item, decorationHandlers.get(item.getName()))
                    .customCacheStrategy(cacheStrategyMap.get(item.getName()), expireMap.get(item.getName()).getTtl().getSeconds())
                    .build();
            this.cacheMap.put(item.getName(), storeCache);
        });
    }

    @Override
    public Cache getCache(String name) {
        return cacheMap.get(name);
    }


    @Override
    public Collection<String> getCacheNames() {
        return cacheNames;
    }
}
