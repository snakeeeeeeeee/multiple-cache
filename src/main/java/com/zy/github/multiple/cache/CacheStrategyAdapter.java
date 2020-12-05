package com.zy.github.multiple.cache;

import com.zy.github.multiple.cache.strategys.CacheStrategy;
import org.springframework.cache.Cache;
import org.springframework.cache.support.AbstractValueAdaptingCache;

import java.util.concurrent.Callable;

public class CacheStrategyAdapter<NC> extends AbstractValueAdaptingCache implements Cache {
    private Cache cache;

    private CacheStrategy strategy;

    /**
     * @param cache    cache
     * @param strategy 缓存策略
     * @param expire   过期时间
     */
    public CacheStrategyAdapter(Cache cache, CacheStrategy strategy, long expire) {
        super(false);
        this.cache = cache;
        this.strategy = strategy;
        strategy.setDefaultExpire(expire);
    }


    @Override
    public String getName() {
        return cache.getName();
    }

    @Override
    public Object getNativeCache() {
        return cache.getNativeCache();
    }

    public NC getNativeCacheCore() {
        return (NC) getNativeCache();
    }

    @Override
    protected Object lookup(Object key) {
        return strategy.doGet(getNativeCacheCore(), key);
    }

    @Override
    public <T> T get(Object key, Callable<T> valueLoader) {
        return (T) strategy.doGet(getNativeCacheCore(), key, valueLoader);
    }

    @Override
    public void put(Object key, Object value) {
        strategy.doPut(getNativeCacheCore(), key, value);
    }

    @Override
    public ValueWrapper putIfAbsent(Object key, Object value) {
        return toValueWrapper(strategy.doPutIfAbsent(getNativeCacheCore(), key, value));
    }

    @Override
    public void evict(Object key) {
        strategy.doEvict(getNativeCacheCore(), key);
    }

    @Override
    public void clear() {
        strategy.doClear(getNativeCacheCore());
    }
}
