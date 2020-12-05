package com.zy.github.multiple.cache.strategys;

import com.github.benmanes.caffeine.cache.Cache;

public abstract class AbstractCaffeineCacheStrategy<K, V> implements CacheStrategy<K, V, Cache<K, V>> {
    protected long defaultExpire = 0;

    @Override
    public void setDefaultExpire(long expire) {
        this.defaultExpire = expire;
    }
}
