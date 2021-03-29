package com.korm.connection;

import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.ehcache.config.CacheConfiguration;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.ExpiryPolicyBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;

import java.time.Duration;

public class KormCache {
    private static KormCache kormCache = null;
    private static CacheManager cacheManager = null;

    private KormCache(){
        cacheManager = CacheManagerBuilder.newCacheManagerBuilder().build();
        cacheManager.init();
    };

    public static KormCache getInstance() {
        if (kormCache == null) {
            kormCache = new KormCache();
        }
        return kormCache;
    }

    public Cache getCache(String tableName, Class key, Class value) {
        CacheConfiguration cacheConfiguration = CacheConfigurationBuilder.newCacheConfigurationBuilder(key, value,
                ResourcePoolsBuilder.heap(100))
                .withExpiry(ExpiryPolicyBuilder.timeToIdleExpiration(Duration.ofSeconds(120)))
                .build();

        Cache cache = cacheManager.createCache(tableName, cacheConfiguration);
        return cache;
    }

}
