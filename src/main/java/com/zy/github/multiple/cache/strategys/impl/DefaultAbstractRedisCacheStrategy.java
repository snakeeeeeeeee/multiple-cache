package com.zy.github.multiple.cache.strategys.impl;

import com.zy.github.multiple.cache.strategys.AbstractRedisCacheStrategy;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.serializer.RedisSerializer;

import java.time.Duration;
import java.util.concurrent.Callable;

/**
 * 默认redis缓存策略实现
 * @author zy 2020/11/29
 */

public class DefaultAbstractRedisCacheStrategy<K, V> extends AbstractRedisCacheStrategy<K, V> {


    public DefaultAbstractRedisCacheStrategy(RedisSerializer redisSerializer, String cacheName) {
        super(redisSerializer, cacheName);
    }

    @Override
    public V doGet(RedisCacheWriter nativeCache, K key) {

        byte[] value = nativeCache.get(cacheName, rawKey(key));
        if (value != null && value.length > 0) {
            return (V) redisSerializer.deserialize(value);
        }
        return null;
    }

    @Override
    public boolean doPut(RedisCacheWriter nativeCache, K key, V value) {
        nativeCache.put(cacheName, rawKey(key), rawValue(value), Duration.ofSeconds(defaultExpire));
        return true;
    }

    @Override
    public V doPutIfAbsent(RedisCacheWriter nativeCache, K key, V value) {
        byte[] v = nativeCache.putIfAbsent(cacheName, rawKey(key), rawValue(value), Duration.ofSeconds(defaultExpire));
        if (value != null && v.length > 0) {
            return (V) redisSerializer.deserialize(v);
        }
        return null;
    }

    @Override
    public boolean doEvict(RedisCacheWriter nativeCache, K key) {
        nativeCache.remove(cacheName, rawKey(key));
        return true;
    }

    @Override
    public boolean doClear(RedisCacheWriter nativeCache) {
        nativeCache.clean(cacheName, rawKey("*"));
        return true;
    }

    @Override
    public <T> T doGet(RedisCacheWriter nativeCache, K key, Callable<T> valueLoader) {
        return (T) this.doGet(nativeCache, key);
    }
}
