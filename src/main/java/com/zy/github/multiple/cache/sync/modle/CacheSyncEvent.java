package com.zy.github.multiple.cache.sync.modle;

import com.zy.github.multiple.cache.util.HostUtil;
import lombok.Data;

import java.io.Serializable;

@Data
public class CacheSyncEvent implements Serializable {
    private String host = HostUtil.getHostName();
    private String cacheName;
    private Object key;
    private Object value;
    public CacheSyncEvent(){}
    public CacheSyncEvent(String cacheName, Object key, Object value) {
        this.cacheName = cacheName;
        this.key = key;
        this.value = value;
    }
}
