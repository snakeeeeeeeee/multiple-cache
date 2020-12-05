package com.zy.github.multiple.cache.sync;


import com.zy.github.multiple.cache.sync.modle.CacheSyncEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * @author z
 * @date 2020/11/27 16:26
 */
@Slf4j
public class RedisCacheSyncManager extends AbstractCacheSyncManager {

    private RedisTemplate redisTemplate;

    public RedisCacheSyncManager(String appName, RedisTemplate redisTemplate) {
        super(appName);
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void publish(CacheSyncEvent event) {
        redisTemplate.convertAndSend(getChannelName(), event);
        log.info("发送缓存同步消息: channel: {}, event: {}", getChannelName(), event);
    }
}
