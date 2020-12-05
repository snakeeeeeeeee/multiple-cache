package com.zy.github.multiple.cache.sync.modle;

/**
 * 清除缓存事件
 */
public class EvictEvent extends CacheSyncEvent {
    public EvictEvent(){}
    public EvictEvent(String cacheName, Object key) {
        super(cacheName, key, null);
    }
}
