package com.zy.github.multiple.cache.sync;

import com.zy.github.multiple.cache.CacheDecorationHandler;
import com.zy.github.multiple.cache.sync.modle.CacheSyncEvent;

/**
 * @author z
 * @date 2020/11/27 15:28
 */
public interface CacheSyncManager{

    String SYNCCHANNEL = "cache-sync";

    void publish(CacheSyncEvent event);

    void handle(CacheSyncEvent event);

    String getChannelName();
}
