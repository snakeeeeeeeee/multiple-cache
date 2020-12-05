package com.zy.github.multiple.cache.sync.modle;

/**
 * 新增/修改缓存事件
 */
public class PutEvent extends CacheSyncEvent {
    public PutEvent(){}
    public PutEvent(String cacheName, Object key, Object value) {
        super(cacheName, key, value);
    }
}
