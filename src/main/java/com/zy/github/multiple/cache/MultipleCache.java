package com.zy.github.multiple.cache;

import org.springframework.cache.Cache;

import java.util.concurrent.Callable;

/**
 * @author z 2020/12/5
 */
public class MultipleCache implements Cache {

    private MultipleCacheNode first;

    public static MultipleCacheBuilder builder() {
        return new MultipleCacheBuilder();
    }

    public MultipleCache(MultipleCacheNode first) {
        this.first = first;
    }

    @Override
    public String getName() {
        return first.getName();
    }

    @Override
    public Object getNativeCache() {
        return first.getNativeCache();
    }

    @Override
    public ValueWrapper get(Object key) {
        return first.get(key);
    }

    @Override
    public <T> T get(Object key, Class<T> type) {
        return (T) first.get(key, type);
    }

    @Override
    public <T> T get(Object key, Callable<T> valueLoader) {
        return (T) first.get(key, valueLoader);
    }

    @Override
    public void put(Object key, Object value) {
        first.put(key, value);
    }

    @Override
    public ValueWrapper putIfAbsent(Object key, Object value) {
        return first.putIfAbsent(key, value);
    }

    @Override
    public void evict(Object key) {
        first.evict(key);
    }

    @Override
    public void clear() {
        first.clear();
    }

    public static class MultipleCacheBuilder {
        private MultipleCacheNode first;

        public MultipleCacheBuilder nextNode(Cache cache) {
            MultipleCacheNode node = new MultipleCacheNode(cache);
            if (first == null) {
                first = node;
            } else {
                first.setNext(node);
            }
            return this;
        }

        public MultipleCache build() {
            return new MultipleCache(first);
        }
    }
}
