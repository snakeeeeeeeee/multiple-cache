package com.zy.github.multiple.cache.strategys.impl;

import com.zy.github.multiple.cache.strategys.AbstractRedisCacheStrategy;
import org.springframework.data.redis.cache.RedisCacheWriter;

import java.util.concurrent.Callable;

/**
 * @author zy 2020/12/04
 */

public class MD5RedisCacheStrategy<K, V> extends AbstractRedisCacheStrategy<K, V> {

    @Override
    public V doGet(RedisCacheWriter nativeCache, K key) {
        return null;
    }

    @Override
    public boolean doPut(RedisCacheWriter nativeCache, K key, V value) {
        return true;
    }

    @Override
    public V doPutIfAbsent(RedisCacheWriter nativeCache, K key, V value) {
        return null;
    }

    @Override
    public boolean doEvict(RedisCacheWriter nativeCache, K key) {
        return true;
    }

    @Override
    public boolean doClear(RedisCacheWriter nativeCache) {
        return false;
    }

    @Override
    public <T> T doGet(RedisCacheWriter nativeCache, K key, Callable<T> valueLoader) {
        return null;
    }
}
