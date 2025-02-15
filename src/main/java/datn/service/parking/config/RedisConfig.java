package datn.service.parking.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import redis.clients.jedis.JedisPoolConfig;


@Configuration
@EnableCaching
public class RedisConfig {
    @Bean
    public JedisConnectionFactory jedisConnectionFactory() {
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(128); // Ví dụ cấu hình số lượng kết nối tối đa

        JedisConnectionFactory factory = new JedisConnectionFactory(poolConfig);
        factory.setHostName("redis-service"); // Địa chỉ Redis của bạn
        factory.setPort(6379); // Cổng Redis
        factory.setPassword("1"); // Thêm mật khẩu Redis nếu cần
        return factory;
    }
    // Cấu hình JedisConnectionFactory để kết nối tới Redis

    // Cấu hình CacheManager cho Redis, sử dụng JedisConnectionFactory
    @Bean
    public CacheManager cacheManager(JedisConnectionFactory jedisConnectionFactory) {
        RedisCacheConfiguration cacheConfiguration = RedisCacheConfiguration.defaultCacheConfig();
        return RedisCacheManager.builder(jedisConnectionFactory)
                .cacheDefaults(cacheConfiguration)
                .build();
    }

    // Cấu hình RedisTemplate, nếu cần sử dụng RedisTemplate để thao tác với Redis trực tiếp
    @Bean
    public RedisTemplate<String, Object> redisTemplate(JedisConnectionFactory jedisConnectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(jedisConnectionFactory);
        return template;
    }
}
