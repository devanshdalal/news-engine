package com.document.feed.config;

import com.document.feed.util.ArticleResponseSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kwabenaberko.newsapilib.models.response.ArticleResponse;
import java.io.File;
import java.util.concurrent.TimeUnit;
import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.config.units.EntryUnit;
import org.ehcache.config.units.MemoryUnit;
import org.ehcache.expiry.Duration;
import org.ehcache.expiry.Expirations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LocalCacheConfig {

  private static final String CACHE = "persistent-cache";

  @Bean(destroyMethod = "close")
  public CacheManager cacheManager() {
    return CacheManagerBuilder.newCacheManagerBuilder()
        .with(CacheManagerBuilder.persistence("data" + File.separator + "local.cache"))
        .withCache(
            CACHE,
            CacheConfigurationBuilder.newCacheConfigurationBuilder(
                    String.class,
                    ArticleResponse.class,
                    ResourcePoolsBuilder.newResourcePoolsBuilder()
                        .heap(10, EntryUnit.ENTRIES)
                        .disk(10, MemoryUnit.MB, true))
                .withValueSerializer(ArticleResponseSerializer.class)
                .withExpiry(Expirations.timeToLiveExpiration(Duration.of(3600, TimeUnit.SECONDS))))
        .build(true);
  }

  @Bean
  public Cache<String, ArticleResponse> cache(CacheManager cacheManager) {
    return cacheManager.getCache(CACHE, String.class, ArticleResponse.class);
  }

  private void test() {
    ObjectMapper mapper = new ObjectMapper();
    ArticleResponse r = new ArticleResponse();
    //    mapper.writeValueAsString(r);
  }
}
