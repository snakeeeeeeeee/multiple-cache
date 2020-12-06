package com.zy.github.multiple.cache.decorators;

import org.springframework.cache.Cache;
import org.springframework.cache.support.SimpleValueWrapper;

import java.util.concurrent.Callable;

/**
 * @author z
 * @date 2020/11/27 15:58
 */
public abstract class AbstractCacheDecorator<C extends Cache> implements Cache {

    protected C target;

    public AbstractCacheDecorator(C target) {
        this.target = target;
    }

    @Override
    public String getName() {
        return target.getName();
    }

    @Override
    public Object getNativeCache() {
        return target.getNativeCache();
    }

    @Override
    public ValueWrapper get(Object key) {
        return target.get(key);
    }

    @Override
    public <T> T get(Object key, Class<T> type) {
        return target.get(key, type);
    }

    @Override
    public <T> T get(Object key, Callable<T> valueLoader) {
        return target.get(key, valueLoader);
    }

    @Override
    public void put(Object key, Object value) {
        target.put(key, value);
    }

    @Override
    public void evict(Object key) {
        target.evict(key);
    }

    @Override
    public ValueWrapper putIfAbsent(Object key, Object value) {
        return target.putIfAbsent(key, value);
    }

    @Override
    public void clear() {
        target.clear();
    }

    protected Cache.ValueWrapper toValueWrapper(Object storeValue) {
        return (storeValue != null ? new SimpleValueWrapper(storeValue) : null);
    }
}
