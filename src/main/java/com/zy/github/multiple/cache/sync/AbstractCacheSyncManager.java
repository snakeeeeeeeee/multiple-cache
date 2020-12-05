package com.zy.github.multiple.cache.sync;

import com.zy.github.multiple.cache.sync.modle.CacheSyncEvent;
import com.zy.github.multiple.cache.sync.modle.ClearEvent;
import com.zy.github.multiple.cache.sync.modle.EvictEvent;
import com.zy.github.multiple.cache.sync.modle.PutEvent;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 缓存同步管理器
 * @author z
 * @date 2020/11/27 15:29
 */
@Slf4j
public abstract class AbstractCacheSyncManager implements CacheSyncManager {


    private static Map<String, CacheSyncEventHandler> handlerMap = new ConcurrentHashMap<>();

    public static void registHandler(String name, CacheSyncEventHandler handler) {
        handlerMap.put(name, handler);
    }

    protected String applicationName;

    public AbstractCacheSyncManager(String appName) {
        this.applicationName = appName;
    }

    public static void doHandle(CacheSyncEvent event) {
        CacheSyncEventHandler handler = handlerMap.get(event.getCacheName());
        if (null == handler) {
            log.warn("不存在的缓存消息同步器：{}", event);
            return;
        }
        if (event instanceof PutEvent) {
            handler.handlePut((PutEvent) event);
        } else if (event instanceof EvictEvent) {
            handler.handleEvict((EvictEvent) event);
        } else if (event instanceof ClearEvent) {
            handler.handleClear((ClearEvent) event);
        } else {
            log.warn("不支持的事件：{}", event);
        }
    }

    @Override
    public void handle(CacheSyncEvent event) {
        doHandle(event);
    }

    @Override
    public String getChannelName() {
        return applicationName + ":" + SYNCCHANNEL;
    }
}
