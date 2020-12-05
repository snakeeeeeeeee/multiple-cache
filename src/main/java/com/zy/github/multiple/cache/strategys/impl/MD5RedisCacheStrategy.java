//package com.zy.github.multiple.cache.strategys;
//
//import org.springframework.data.redis.cache.RedisCacheWriter;
//import org.springframework.data.redis.serializer.RedisSerializer;
//
//import java.time.Duration;
//import java.util.concurrent.Callable;
//
///**
// * @author zy 2020/12/04
// */
//
//public class MD5RedisCacheStrategy<K, V> extends RedisCacheStrategy<K, V> {
//
//
//    public MD5RedisCacheStrategy(RedisSerializer redisSerializer, String cacheName) {
//        super(redisSerializer, cacheName);
//    }
//
//    @Override
//    public V doGet(RedisCacheWriter nativeCache, K key) {
//        byte[] value = nativeCache.get(cacheName, rawKey(key));
//        if (value != null && value.length > 0) {
//            return (V) redisSerializer.deserialize(value);
//        }
//        return null;
//    }
//
//    @Override
//    public boolean doPut(RedisCacheWriter nativeCache, K key, V value) {
//        nativeCache.put(cacheName, rawKey(key), rawValue(value), Duration.ofSeconds(defaultExpire));
//        return true;
//    }
//
//    @Override
//    public V doPutIfAbsent(RedisCacheWriter nativeCache, K key, V value) {
//        byte[] v = nativeCache.putIfAbsent(cacheName, rawKey(key), rawValue(value), Duration.ofSeconds(defaultExpire));
//        if (value != null && v.length > 0) {
//            return (V) redisSerializer.deserialize(v);
//        }
//        return null;
//    }
//
//    @Override
//    public boolean doEvict(RedisCacheWriter nativeCache, K key) {
//        nativeCache.remove(cacheName, rawKey(key));
//        return true;
//    }
//
//    @Override
//    public boolean doClear(RedisCacheWriter nativeCache) {
//        nativeCache.clean(cacheName, rawKey("*"));
//        return false;
//    }
//
//    @Override
//    public <T> T doGet(RedisCacheWriter nativeCache, K key, Callable<T> valueLoader) {
//        return null;
//    }
//}
