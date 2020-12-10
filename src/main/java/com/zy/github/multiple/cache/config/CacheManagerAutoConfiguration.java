package com.zy.github.multiple.cache.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.github.benmanes.caffeine.cache.CacheLoader;
import com.zy.github.multiple.cache.manager.CaffeineCacheManagerAdapter;
import com.zy.github.multiple.cache.manager.CaffeineRedisCacheManager;
import com.zy.github.multiple.cache.manager.RedisCacheManagerAdapter;
import com.zy.github.multiple.cache.strategys.AbstractCaffeineCacheStrategy;
import com.zy.github.multiple.cache.strategys.impl.DefaultRedisCacheStrategy;
import com.zy.github.multiple.cache.strategys.AbstractRedisCacheStrategy;
import com.zy.github.multiple.cache.strategys.CacheStrategy;
import com.zy.github.multiple.cache.sync.CacheSyncManager;
import com.zy.github.multiple.cache.sync.CacheSyncMessageListener;
import com.zy.github.multiple.cache.sync.RedisCacheSyncManager;
import com.zy.github.multiple.cache.CacheDecorationHandler;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.CacheManager;
import org.springframework.cache.support.CompositeCacheManager;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.serializer.*;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author zy 2020/11/29
 */
@Configuration
@ConditionalOnMissingBean(CacheManager.class)
@EnableConfigurationProperties(CacheConfigProperties.class)
public class CacheManagerAutoConfiguration {


    private final Map<String, CacheStrategy> strategyMap;
    private final Map<String, CacheLoader> cacheLoaderMap;
    private final Map<String, CacheDecorationHandler> decorationHandlerMap;
    private final CacheConfigProperties cacheProperties;
    private String applicationName;

    public CacheManagerAutoConfiguration(CacheConfigProperties cacheConfigProperties, ApplicationContext applicationContext,
                                         Map<String, CacheStrategy> strategyMap, Map<String, CacheLoader> cacheLoaderMap,
                                         Map<String, CacheDecorationHandler> decorationHandlerMap) {
        applicationName = applicationContext.getEnvironment().getProperty("spring.application.name");
        this.applicationName = ObjectUtils.isEmpty(applicationName) ? "DEFAULT" : applicationName;
        this.cacheProperties = cacheConfigProperties;
        this.strategyMap = strategyMap;
        this.cacheLoaderMap = cacheLoaderMap;
        this.decorationHandlerMap = decorationHandlerMap;
    }

    @Bean
    @ConditionalOnMissingBean(RedisTemplate.class)
    public RedisTemplate<String, Object> redisTemplate(LettuceConnectionFactory lettuceConnectionFactory) {
        // 设置序列化
        Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer = getJsonSerializer();
        // 配置redisTemplate
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(lettuceConnectionFactory);
        RedisSerializer<String> stringSerializer = RedisSerializer.string();
        redisTemplate.setKeySerializer(stringSerializer);
        redisTemplate.setValueSerializer(jackson2JsonRedisSerializer);
        redisTemplate.setHashKeySerializer(stringSerializer);
        redisTemplate.setHashValueSerializer(jackson2JsonRedisSerializer);
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }

    @Bean
    @ConditionalOnMissingBean(StringRedisTemplate.class)
    public StringRedisTemplate stringRedisTemplate(LettuceConnectionFactory lettuceConnectionFactory) {
        Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer = getJsonSerializer();

        StringRedisTemplate redisTemplate = new StringRedisTemplate();
        redisTemplate.setConnectionFactory(lettuceConnectionFactory);
        RedisSerializer<String> stringSerializer = RedisSerializer.string();
        redisTemplate.setKeySerializer(stringSerializer);
        redisTemplate.setValueSerializer(jackson2JsonRedisSerializer);
        redisTemplate.setHashKeySerializer(stringSerializer);
        redisTemplate.setHashValueSerializer(jackson2JsonRedisSerializer);
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }

    @Bean(name = "syncCacheTaskExecutor")
    @ConditionalOnMissingBean(name = "syncCacheTaskExecutor")
    public TaskExecutor syncTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(4);
        executor.setKeepAliveSeconds(60);
        executor.setThreadNamePrefix("cache-sync-");
        return executor;
    }

    @Bean(name = "redisCacheMessageSyncListenerContainer")
    @ConditionalOnMissingBean(name = "redisCacheMessageSyncListenerContainer")
    public RedisMessageListenerContainer container(RedisConnectionFactory connectionFactory, CacheSyncMessageListener receiver,
                                                   @Qualifier("syncCacheTaskExecutor") TaskExecutor executor) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setTaskExecutor(executor);
        container.addMessageListener(receiver, new ChannelTopic(receiver.getChannelName()));
        return container;
    }

    @Bean
    @ConditionalOnMissingBean(CacheSyncManager.class)
    public CacheSyncManager redisBasedCacheSyncServce(RedisTemplate redisTemplate) {
        return new RedisCacheSyncManager(applicationName, redisTemplate);
    }

    @Bean
    @ConditionalOnMissingBean(CacheSyncMessageListener.class)
    public CacheSyncMessageListener cacheSyncMessageListener(CacheSyncManager cacheSyncManager, RedisTemplate redisTemplate) {
        return new CacheSyncMessageListener(redisTemplate, cacheSyncManager);
    }

    @Bean
    @ConditionalOnMissingBean(RedisCacheWriter.class)
    public RedisCacheWriter redisCacheWriter(LettuceConnectionFactory lettuceConnectionFactory) {
        return RedisCacheWriter.nonLockingRedisCacheWriter(lettuceConnectionFactory);
    }

    @Bean
    @ConditionalOnMissingBean(CacheManager.class)
    public CompositeCacheManager cacheManager(CacheSyncManager cacheSyncManager, RedisCacheWriter redisCacheWriter) {
        List<CacheManager> cacheManagerList = new LinkedList<>();

        //caffeine
        if (!CollectionUtils.isEmpty(cacheProperties.getCaffeine())) {
            cacheManagerList.add(buildCaffeineCacheManager(cacheProperties.getCaffeine(), cacheSyncManager));
        }

        //redis
        if (!CollectionUtils.isEmpty(cacheProperties.getRedis())) {
            cacheManagerList.add(buildRedisCacheManager(redisCacheWriter, cacheSyncManager, cacheProperties.getRedis()));
        }

        //caffeine + redis
        if (!CollectionUtils.isEmpty(cacheProperties.getMultiple())) {
            cacheManagerList.add(buildCaffeineRedis(cacheSyncManager, redisCacheWriter));
        }

        CompositeCacheManager cacheManager = new CompositeCacheManager();
        cacheManager.setCacheManagers(cacheManagerList);
        return cacheManager;
    }


    private RedisCacheManagerAdapter buildRedisCacheManager(RedisCacheWriter cacheWriter,
                                                            CacheSyncManager cacheSyncManager,
                                                            Collection<CacheConfigProperties.RedisCacheConfig> configs) {
        Set<String> redisCacheNames = new LinkedHashSet<>();
        Map<String, RedisCacheConfiguration> redisCacheConfigurationMap = new HashMap<>();
        Map<String, AbstractRedisCacheStrategy> cacheStrategyMap = new HashMap<>(8);
        Map<String, Set<CacheDecorationHandler>> decorationHandlers = new HashMap<>(8);
        configs.forEach(item -> {
            redisCacheNames.add(item.getName());
            redisCacheConfigurationMap.put(item.getName(), redisCacheConfiguration(item));

            if (item.getStrategy() != null && strategyMap.containsKey(item.getStrategy())) {
                CacheStrategy strategy = strategyMap.get(item.getStrategy());
                if (strategy instanceof AbstractRedisCacheStrategy) {
                    cacheStrategyMap.put(item.getName(), (AbstractRedisCacheStrategy) strategy);
                }
            } else {
                DefaultRedisCacheStrategy cacheStrategy = new DefaultRedisCacheStrategy(item.getName());
                cacheStrategyMap.put(item.getName(), cacheStrategy);
            }

            if (!ObjectUtils.isEmpty(item.getDecorators())) {
                List<String> decoratorList = Arrays.asList(item.getDecorators().split(","));
                Set<CacheDecorationHandler> collect = decoratorList.stream()
                        .map(decorator -> decorationHandlerMap.get(decorator)).collect(Collectors.toSet());
                decorationHandlers.put(item.getName(), collect);
            }
        });
        RedisCacheManagerAdapter redisCacheManager = new RedisCacheManagerAdapter(cacheWriter, redisCacheNames,
                false, redisCacheConfigurationMap, cacheStrategyMap, decorationHandlers);
        redisCacheManager.initCaches();
        return redisCacheManager;
    }

    private CaffeineCacheManagerAdapter buildCaffeineCacheManager(Collection<CacheConfigProperties.CaffeineCacheConfig> configs,
                                                                  CacheSyncManager cacheSyncManager) {
        Set<CaffeineCacheManagerAdapter.CacheConfig> caffeineCacheConfigs = new LinkedHashSet<>();
        Set<String> caffeineCacheNames = new LinkedHashSet<>();
        Map<String, AbstractCaffeineCacheStrategy> cacheStrategyMap = new HashMap<>(8);
        Map<String, Set<CacheDecorationHandler>> decorationHandlers = new HashMap<>(8);
        configs.forEach(item -> {
            caffeineCacheNames.add(item.getName());
            CaffeineCacheManagerAdapter.CacheConfig cacheConfig = new CaffeineCacheManagerAdapter.CacheConfig();
            cacheConfig.setExpireAfterAccess(item.getExpireAfterAccess());
            cacheConfig.setExpireAfterWrite(item.getExpireAfterWrite());
            cacheConfig.setInitialCapacity(item.getInitialCapacity());
            cacheConfig.setMaximumSize(item.getMaximumSize());
            cacheConfig.setName(item.getName());
            cacheConfig.setDisableSync(item.isDisableSync());
            cacheConfig.setEnableSoftRef(item.isEnableSoftRef());

            if (!ObjectUtils.isEmpty(item.getCacheLoader())
                    && cacheLoaderMap.containsKey(item.getCacheLoader())) {
                cacheConfig.setCacheLoader(cacheLoaderMap.get(item.getCacheLoader()));
            }

            caffeineCacheConfigs.add(cacheConfig);
            if (!ObjectUtils.isEmpty(item.getStrategy()) && strategyMap.containsKey(item.getStrategy())) {
                CacheStrategy strategy = strategyMap.get(item.getStrategy());
                if (strategy instanceof AbstractCaffeineCacheStrategy) {
                    cacheStrategyMap.put(item.getName(), (AbstractCaffeineCacheStrategy) strategy);
                }
            }

            if (!ObjectUtils.isEmpty(item.getDecorators())) {
                List<String> decoratorList = Arrays.asList(item.getDecorators().split(","));
                Set<CacheDecorationHandler> collect = decoratorList.stream()
                        .map(decorator -> decorationHandlerMap.get(decorator)).collect(Collectors.toSet());
                decorationHandlers.put(item.getName(), collect);
            }
        });
        return new CaffeineCacheManagerAdapter(caffeineCacheNames, caffeineCacheConfigs, cacheStrategyMap,
                decorationHandlers, cacheSyncManager);
    }


    private RedisCacheConfiguration redisCacheConfiguration(CacheConfigProperties.RedisCacheConfig redisCacheConfig) {
        RedisSerializationContext.SerializationPair pair = RedisSerializationContext.SerializationPair
                .fromSerializer(getJsonSerializer());
        return RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofSeconds(redisCacheConfig.getExpire()))
                .serializeValuesWith(pair);
    }


    private Jackson2JsonRedisSerializer<Object> getJsonSerializer() {
        // 设置序列化
        Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<>(Object.class);
        ObjectMapper om = new ObjectMapper();
        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        om.activateDefaultTyping(LaissezFaireSubTypeValidator.instance, ObjectMapper.DefaultTyping.NON_FINAL);
        jackson2JsonRedisSerializer.setObjectMapper(om);
        return jackson2JsonRedisSerializer;
    }


    private CaffeineRedisCacheManager buildCaffeineRedis(CacheSyncManager cacheSyncManager, RedisCacheWriter redisCacheWriter) {
        Set<CacheConfigProperties.CaffeineCacheConfig> caffeineCacheConfigs = new LinkedHashSet<>();
        Set<CacheConfigProperties.RedisCacheConfig> redisCacheConfigs = new LinkedHashSet<>();
        Map<String, Set<CacheDecorationHandler>> decorationHandlers = new HashMap<>(8);
        cacheProperties.getMultiple().forEach(item -> {
            CacheConfigProperties.CaffeineCacheConfig caffeineCacheConfig = item.getCaffeine();
            caffeineCacheConfig.setName(item.getName());
            CacheConfigProperties.RedisCacheConfig redisCacheConfig = item.getRedis();
            redisCacheConfig.setName(item.getName());
            caffeineCacheConfigs.add(caffeineCacheConfig);
            redisCacheConfigs.add(redisCacheConfig);

            if (!ObjectUtils.isEmpty(item.getDecorators())) {
                List<String> decoratorList = Arrays.asList(item.getDecorators().split(","));
                Set<CacheDecorationHandler> collect = decoratorList.stream()
                        .map(decorator -> decorationHandlerMap.get(decorator)).collect(Collectors.toSet());
                decorationHandlers.put(item.getName(), collect);
            }

        });

        CaffeineRedisCacheManager multipleCacheManager
                = new CaffeineRedisCacheManager(
                buildCaffeineCacheManager(caffeineCacheConfigs, cacheSyncManager),
                buildRedisCacheManager(redisCacheWriter, cacheSyncManager, redisCacheConfigs), decorationHandlers);
        multipleCacheManager.initializeCaches();
        return multipleCacheManager;
    }

}
