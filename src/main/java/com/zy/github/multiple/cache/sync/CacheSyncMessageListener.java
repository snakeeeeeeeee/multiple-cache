package com.zy.github.multiple.cache.sync;

import com.zy.github.multiple.cache.sync.modle.CacheSyncEvent;
import com.zy.github.multiple.cache.util.HostUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.ObjectUtils;

@Slf4j
public class CacheSyncMessageListener implements MessageListener {

    private RedisTemplate redisTemplate;

    private CacheSyncManager cacheSyncManager;

    public CacheSyncMessageListener(RedisTemplate redisTemplate, CacheSyncManager cacheSyncManager) {
        this.redisTemplate = redisTemplate;
        this.cacheSyncManager = cacheSyncManager;
    }

    @Override
    public void onMessage(Message message, byte[] pattern) {
        log.debug("接收到缓存同步消息：{}", message);
        try {
            CacheSyncEvent event = (CacheSyncEvent) redisTemplate
                    .getValueSerializer().deserialize(message.getBody());
            if (ObjectUtils.nullSafeEquals(HostUtil.getHostName(), event.getHost())) {
                log.debug("该消息由本机发出，无须处理：{}", event);
                return;
            }
            cacheSyncManager.handle(event);
        } catch (Exception e) {
            log.error("同步消息异常！", e);
        }
    }

    public String getChannelName() {
        return cacheSyncManager.getChannelName();
    }
}
