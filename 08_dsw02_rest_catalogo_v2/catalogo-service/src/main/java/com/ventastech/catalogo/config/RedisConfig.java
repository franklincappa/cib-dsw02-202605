package com.ventastech.catalogo.config;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Configuration
@EnableCaching
public class RedisConfig implements CachingConfigurer {

    private GenericJackson2JsonRedisSerializer jsonSerializer() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());

        // Spring Boot 4 compatible — misma configuración
        mapper.activateDefaultTyping(
                LaissezFaireSubTypeValidator.instance,
                ObjectMapper.DefaultTyping.NON_FINAL,
                JsonTypeInfo.As.PROPERTY
        );
        return new GenericJackson2JsonRedisSerializer(mapper);
    }

    private RedisCacheConfiguration defaultConfig() {
        return RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(10))
                .disableCachingNullValues()
                .serializeKeysWith(
                        RedisSerializationContext.SerializationPair
                                .fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(
                        RedisSerializationContext.SerializationPair
                                .fromSerializer(jsonSerializer()));
    }

    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory factory) {
        Map<String, RedisCacheConfiguration> configs = new HashMap<>();

        configs.put("productos",     defaultConfig().entryTtl(Duration.ofMinutes(5)));
        configs.put("producto",      defaultConfig().entryTtl(Duration.ofMinutes(5)));
        configs.put("categorias",    defaultConfig().entryTtl(Duration.ofMinutes(30)));
        configs.put("categoria",     defaultConfig().entryTtl(Duration.ofMinutes(30)));
        configs.put("marcas",        defaultConfig().entryTtl(Duration.ofMinutes(30)));
        configs.put("marca",         defaultConfig().entryTtl(Duration.ofMinutes(30)));
        configs.put("inventario",    defaultConfig().entryTtl(Duration.ofMinutes(2)));
        configs.put("bajo-critico",  defaultConfig().entryTtl(Duration.ofMinutes(1)));

        return RedisCacheManager.builder(factory)
                .cacheDefaults(defaultConfig())
                .withInitialCacheConfigurations(configs)
                .build();
    }

    //Error Handler — si Redis falla
    @Override
    public CacheErrorHandler errorHandler() {
        return new CacheErrorHandler() {

            @Override
            public void handleCacheGetError(RuntimeException e, Cache cache, Object key) {
                log.warn("[REDIS] Error al LEER caché '{}' key='{}' — va directo a BD. Causa: {}",
                        cache.getName(), key, e.getMessage());
                // no lanza excepción — Spring va a BD automáticamente
            }

            @Override
            public void handleCachePutError(RuntimeException e, Cache cache, Object key, Object value) {
                log.warn("[REDIS] Error al GUARDAR en caché '{}' key='{}' — continuando sin caché. Causa: {}",
                        cache.getName(), key, e.getMessage());
            }

            @Override
            public void handleCacheEvictError(RuntimeException e, Cache cache, Object key) {
                log.warn("[REDIS] Error al EVICTAR caché '{}' key='{}' — continuando. Causa: {}",
                        cache.getName(), key, e.getMessage());
            }

            @Override
            public void handleCacheClearError(RuntimeException e, Cache cache) {
                log.warn("[REDIS] Error al LIMPIAR caché '{}' — continuando. Causa: {}",
                        cache.getName(), e.getMessage());
            }
        };
    }

}
