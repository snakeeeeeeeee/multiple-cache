package com.zy.github.multiple.cache.sync;

import com.zy.github.multiple.cache.sync.modle.ClearEvent;
import com.zy.github.multiple.cache.sync.modle.EvictEvent;
import com.zy.github.multiple.cache.sync.modle.PutEvent;

public interface CacheSyncEventHandler {
    /**
     * 放入缓存事件
     * @param event
     */
    void handlePut(PutEvent event);

    /**
     * 清理缓存事件
     * @param event
     */
    void handleEvict(EvictEvent event);

    /**
     * 清除缓存事件
     * @param event
     */
    void handleClear(ClearEvent event);
}
