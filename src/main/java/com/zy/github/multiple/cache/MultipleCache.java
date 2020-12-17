package com.zy.github.multiple.cache;

import org.springframework.cache.Cache;

import java.util.concurrent.Callable;

/**
 * @author z 2020/12/5
 */
public class MultipleCache implements Cache {

    private MultipleCacheNode cacheNode;

    public static MultipleCacheBuilder builder() {
        return new MultipleCacheBuilder();
    }

    public MultipleCache(MultipleCacheNode cacheNode) {
        this.cacheNode = cacheNode;
    }

    @Override
    public String getName() {
        return cacheNode.getName();
    }

    @Override
    public Object getNativeCache() {
        return cacheNode.getNativeCache();
    }

    @Override
    public ValueWrapper get(Object key) {
        return cacheNode.get(key);
    }

    @Override
    public <T> T get(Object key, Class<T> type) {
        return (T) cacheNode.get(key, type);
    }

    @Override
    public <T> T get(Object key, Callable<T> valueLoader) {
        return (T) cacheNode.get(key, valueLoader);
    }

    @Override
    public void put(Object key, Object value) {
        cacheNode.put(key, value);
    }

    @Override
    public ValueWrapper putIfAbsent(Object key, Object value) {
        return cacheNode.putIfAbsent(key, value);
    }

    @Override
    public void evict(Object key) {
        cacheNode.evict(key);
    }

    @Override
    public void clear() {
        cacheNode.clear();
    }

    public static class MultipleCacheBuilder {
        private MultipleCacheNode cache;

        public MultipleCacheBuilder nextNode(Cache cache) {
            MultipleCacheNode node = new MultipleCacheNode(cache);
            if (this.cache == null) {
                this.cache = node;
            } else {
                this.cache.setNext(node);
            }
            return this;
        }

        public MultipleCache build() {
            return new MultipleCache(cache);
        }
    }
}
