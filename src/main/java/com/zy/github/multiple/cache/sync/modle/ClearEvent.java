package com.zy.github.multiple.cache.sync.modle;

/**
 * 删除缓存事件
 */
public class ClearEvent extends CacheSyncEvent {
    public ClearEvent(){}
    public ClearEvent(String cacheName) {
        super(cacheName, null, null);
    }
}
