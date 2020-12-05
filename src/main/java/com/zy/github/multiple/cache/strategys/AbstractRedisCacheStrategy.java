package com.zy.github.multiple.cache.strategys;

import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.util.Assert;


/**
 * redis缓存策略
 *
 * @author z 2020/11/29
 */
public abstract class AbstractRedisCacheStrategy<K, V> implements CacheStrategy<K, V, RedisCacheWriter> {


    protected long defaultExpire = 0;
    protected RedisSerializer redisSerializer;
    protected String cacheName;

    public AbstractRedisCacheStrategy(RedisSerializer redisSerializer, String cacheName) {
        this.redisSerializer = redisSerializer;
        this.cacheName = cacheName;
    }

    protected byte[] rawKey(Object key) {
        Assert.notNull(key, "non null key required");
        return redisSerializer.serialize(key);
    }

    protected byte[] rawValue(Object value) {
        return redisSerializer.serialize(value);
    }


    @Override
    public void setDefaultExpire(long expire) {
        this.defaultExpire = expire;
    }
}
