package com.zy.github.multiple.cache.strategys;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.zy.github.multiple.cache.constans.CacheConstants;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;


/**
 * redis缓存策略
 *
 * @author z 2020/11/29
 */
public abstract class AbstractRedisCacheStrategy<K, V> implements CacheStrategy<K, V, RedisCacheWriter> {


    protected long defaultExpire = 0;
    protected RedisSerializer redisSerializer;
    protected String cacheName;

    public AbstractRedisCacheStrategy() {
        init();
    }

    public AbstractRedisCacheStrategy(String cacheName) {
        init(cacheName);
    }

    public AbstractRedisCacheStrategy(RedisSerializer redisSerializer, String cacheName) {
        init(redisSerializer, cacheName);
    }

    public void init() {
        if (ObjectUtils.isEmpty(redisSerializer)) {
            synchronized (AbstractRedisCacheStrategy.class) {
                redisSerializer = jackson2JsonRedisSerializer();
            }
        }

        if (ObjectUtils.isEmpty(cacheName)) {
            synchronized (AbstractRedisCacheStrategy.class) {
                cacheName = CacheConstants.REDIS_DEFAULT;
            }
        }
    }

    public void init(String cacheName) {
        this.cacheName = cacheName;
        if (redisSerializer == null) {
            synchronized (AbstractRedisCacheStrategy.class) {
                redisSerializer = jackson2JsonRedisSerializer();
            }
        }
    }

    public void init(RedisSerializer redisSerializer, String cacheName) {
        this.cacheName = cacheName;
        this.redisSerializer = redisSerializer;
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


    private Jackson2JsonRedisSerializer jackson2JsonRedisSerializer() {
        Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<>(Object.class);
        ObjectMapper om = new ObjectMapper();
        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        om.activateDefaultTyping(LaissezFaireSubTypeValidator.instance, ObjectMapper.DefaultTyping.NON_FINAL);
        jackson2JsonRedisSerializer.setObjectMapper(om);
        return jackson2JsonRedisSerializer;
    }
}
