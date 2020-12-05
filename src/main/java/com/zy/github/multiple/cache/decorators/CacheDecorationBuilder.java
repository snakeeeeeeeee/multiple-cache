package com.zy.github.multiple.cache.decorators;

import com.zy.github.multiple.cache.strategys.CacheStrategy;
import com.zy.github.multiple.cache.sync.CacheSyncManager;
import com.zy.github.multiple.cache.CacheDecorationHandler;
import com.zy.github.multiple.cache.CacheStrategyAdapter;
import org.springframework.cache.Cache;
import org.springframework.cache.caffeine.CaffeineCache;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class CacheDecorationBuilder {
    private Cache base;
    private Cache result;
    private Map<Class, CacheDecorationHandler> handlers;

    private CacheDecorationBuilder(Cache base, Map<Class, CacheDecorationHandler> handlers) {
        this.base = base;
        this.result = base;
        this.handlers = handlers;
    }

    public static CacheDecorationBuilder newBuilder(Cache base, Set<CacheDecorationHandler> handlers) {
        if (null == handlers) {
            return new CacheDecorationBuilder(base, new HashMap<>(1));
        }
        Map<Class, CacheDecorationHandler> handlerMap = new HashMap<>(8);

        handlers.forEach(item -> {
            if (item instanceof CacheSyncManager) {
                handlerMap.put(CacheSyncManager.class, item);
            }
        });

        return new CacheDecorationBuilder(base, handlerMap);
    }


    public CacheDecorationBuilder localCacheSync(boolean disableSync) {
        if ((base instanceof CaffeineCache) && !disableSync) {
            CacheSyncManager cacheSyncManager = getHandler(CacheSyncManager.class);
            if (cacheSyncManager != null) {
                result = new CacheSyncDecorator(result, cacheSyncManager);
            }
        }
        return this;
    }

    public CacheDecorationBuilder localCacheSync(boolean disableSync, CacheSyncManager cacheSyncManager) {
        if ((base instanceof CaffeineCache) && disableSync) {
            result = new CacheSyncDecorator(result, cacheSyncManager);
        }
        return this;
    }

    public CacheDecorationBuilder customCacheStrategy(CacheStrategy strategy, Long expire) {
        result = null == strategy ? result : new CacheStrategyAdapter(result, strategy, null == expire ? 0 : expire.longValue());
        return this;
    }


    public Cache build() {
        return result;
    }

    protected <T extends CacheDecorationHandler> T getHandler(Class<T> tClass) {
        return (T) handlers.get(tClass);
    }
}
