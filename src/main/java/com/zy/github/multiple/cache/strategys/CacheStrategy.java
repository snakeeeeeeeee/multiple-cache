package com.zy.github.multiple.cache.strategys;

import java.util.concurrent.Callable;

/**
 * 缓存策略
 * @param <K> 缓存key
 * @param <V> 缓存值
 * @param <NC> 缓存
 */
public interface CacheStrategy<K, V, NC> {

    /**
     * 获取缓存
     * @param nativeCache 缓存
     * @param key 缓存key
     * @return 缓存值
     */
    V doGet(NC nativeCache, K key);

    /**
     * 放入缓存
     * @param nativeCache 缓存
     * @param key 缓存key
     * @param value 缓存值
     * @return
     */
    boolean doPut(NC nativeCache, K key, V value);

    /**
     * 不存在则放入缓存
     * @param nativeCache 缓存
     * @param key 缓存key
     * @param value 缓存值
     * @return
     */
    V doPutIfAbsent(NC nativeCache, K key, V value);

    /**
     * 删除缓存
     * @param nativeCache 缓存
     * @param key 缓存key
     * @return 是否成功
     */
    boolean doEvict(NC nativeCache, K key);

    /**
     * 清理整个缓存
     * @param nativeCache 缓存
     * @return
     */
    boolean doClear(NC nativeCache);

    /**
     * 获取一个缓存
     * @param nativeCache 缓存
     * @param key 缓存key
     * @param valueLoader
     * @param <T>
     * @return
     */
    <T> T doGet(NC nativeCache, K key, Callable<T> valueLoader);

    /**
     * 设置默认过期时间
     * @param expire
     */
    void setDefaultExpire(long expire);
}
