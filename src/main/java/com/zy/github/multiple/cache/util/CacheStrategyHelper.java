package com.zy.github.multiple.cache.util;

import com.zy.github.multiple.cache.config.CacheConfigProperties;
import com.zy.github.multiple.cache.constans.CacheConstants;
import com.zy.github.multiple.cache.constans.CacheStrategyType;
import com.zy.github.multiple.cache.strategys.AbstractCaffeineCacheStrategy;
import com.zy.github.multiple.cache.strategys.CacheStrategy;
import com.zy.github.multiple.cache.strategys.impl.DefaultAbstractRedisCacheStrategy;
import lombok.Getter;
import com.zy.github.multiple.cache.config.CacheConfigProperties.*;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 缓存构建工具类
 *
 * @author z
 * @date 2020/11/27 14:32
 */
public class CacheStrategyHelper {

    @Getter
    private static Map<String, Class<? extends CacheStrategy>> STRATEGY_MAP = new LinkedHashMap<>();


    public static final CacheConfigProperties.CaffeineCacheConfig DEFAULT_CAFFEINE_CONFIG = CacheConfigProperties.CaffeineCacheConfig.builder()
            .name(CacheConstants.LOCAL_DEFAULT).initialCapacity(10)
            .maximumSize(2000).expireAfterAccess(1800).build();


    public static final CacheConfigProperties.RedisCacheConfig DEFAULT_REDIS_CONFIG = RedisCacheConfig.builder()
            .name(CacheConstants.REMOTE_DEFAULT).expire(3600).build();

    public static final MultipleCacheConfig DEFAULT_MULTIPLE_CONFIG = MultipleCacheConfig.builder()
            .name(CacheConstants.MULTIPLE_DEFAULT)
            .caffeine(CaffeineCacheConfig.builder().name(CacheConstants.MULTIPLE_DEFAULT)
                    .initialCapacity(10).maximumSize(2000).expireAfterAccess(1800).build())
            .redis(RedisCacheConfig.builder().name(CacheConstants.MULTIPLE_DEFAULT).expire(3600).build())
            .build();

    static {
        STRATEGY_MAP.put(CacheStrategyType.REDIS_.name() + CacheStrategyType.Redis.DEFAULT.name(), DefaultAbstractRedisCacheStrategy.class);
        STRATEGY_MAP.put(CacheStrategyType.CAFFEINE_.name() + CacheStrategyType.Caffeine.DEFAULT.name(), AbstractCaffeineCacheStrategy.class);
    }




}
