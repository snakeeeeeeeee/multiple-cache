package com.zy.github.multiple.cache.manager;

import com.zy.github.multiple.cache.CacheDecorationHandler;
import com.zy.github.multiple.cache.MultipleCache;
import com.zy.github.multiple.cache.decorators.CacheDecorationBuilder;
import org.springframework.cache.Cache;
import org.springframework.cache.support.AbstractCacheManager;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author zy 2020/12/5
 */
public class CaffeineRedisCacheManager extends AbstractCacheManager {

    private CaffeineCacheManagerAdapter caffeineCacheManager;
    private RedisCacheManagerAdapter redisCacheManager;
    private Map<String, Set<CacheDecorationHandler>> handerMap;

    public CaffeineRedisCacheManager(CaffeineCacheManagerAdapter caffeineCacheManager,
                                     RedisCacheManagerAdapter redisCacheManager,
                                     Map<String, Set<CacheDecorationHandler>> handerMap) {
        this.caffeineCacheManager = caffeineCacheManager;
        this.redisCacheManager = redisCacheManager;
        this.handerMap = handerMap;
    }

    @Override
    protected Collection<? extends Cache> loadCaches() {
        Set<Cache> caches = new LinkedHashSet<>();
        Collection<String> redisCaches = redisCacheManager.getCacheNames();
        redisCaches.forEach(item -> {
            Cache multipleCache = MultipleCache.builder()
                    .nextNode(caffeineCacheManager.getCache(item))
                    .nextNode(redisCacheManager.getCache(item))
                    .build();

            Set<CacheDecorationHandler> handlers = handerMap.get(item);
            if (handlers != null) {
                multipleCache = CacheDecorationBuilder
                        .newBuilder(multipleCache, handlers)
                        .build();
            }
            caches.add(multipleCache);
        });
        return caches;
    }
}
