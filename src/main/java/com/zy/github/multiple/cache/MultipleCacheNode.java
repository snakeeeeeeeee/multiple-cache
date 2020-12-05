package com.zy.github.multiple.cache;

import org.springframework.cache.Cache;

import java.util.concurrent.Callable;

/**
 * @author zy 2020/12/5
 */
public class MultipleCacheNode<T extends Cache> implements Cache {

    private MultipleCacheNode next;

    private T cache;

    public MultipleCacheNode(T cache) {
        if (cache instanceof MultipleCacheNode) {
            throw new IllegalArgumentException("the cache param can not be type of MultipleCacheNode!");
        }
        this.cache = cache;
    }

    public void setNext(MultipleCacheNode next) {
        this.next = next;
    }

    public boolean hasNext() {
        return null != next;
    }

    @Override
    public String getName() {
        return cache.getName();
    }

    @Override
    public Object getNativeCache() {
        return cache.getNativeCache();
    }

    @Override
    public ValueWrapper get(Object key) {
        ValueWrapper value = cache.get(key);
        if (null == value && hasNext()) {
            value = next.get(key);
            if (null != value) {
                cache.putIfAbsent(key, value.get());
            }
        }
        return value;
    }

    @Override
    public <T> T get(Object key, Class<T> type) {
        return cache.get(key, type);
    }

    @Override
    public <T> T get(Object key, Callable<T> valueLoader) {
        return cache.get(key, valueLoader);
    }

    @Override
    public void put(Object key, Object value) {
        if (hasNext()) {
            next.put(key, value);
        }
        cache.put(key, value);
    }

    @Override
    public ValueWrapper putIfAbsent(Object key, Object value) {
        return cache.putIfAbsent(key, value);
    }

    @Override
    public void evict(Object key) {
        if (hasNext()) {
            next.evict(key);
        }
        cache.evict(key);
    }

    @Override
    public void clear() {
        if (hasNext()) {
            next.clear();
        }
        cache.clear();
    }
}
