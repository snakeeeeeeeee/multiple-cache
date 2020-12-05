package com.zy.github.multiple.cache.constans;

/**
 * @author z 2020/12/3
 */
public enum CacheStrategyType {
    REDIS_,
    CAFFEINE_;

    public enum Redis {
        DEFAULT,
        MD5
    }

    public enum Caffeine {
        DEFAULT
    }
}
