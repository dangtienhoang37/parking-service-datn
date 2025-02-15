package datn.service.parking.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class RedisService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    // Ghi dữ liệu vào Redis
    public void saveToRedis(String key, Object value, long timeoutInSeconds) {
        redisTemplate.opsForValue().set(key, value, timeoutInSeconds, TimeUnit.SECONDS);
    }

    // Đọc dữ liệu từ Redis
    public Object getFromRedis(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    // Xóa dữ liệu khỏi Redis
    public void deleteFromRedis(String key) {
        redisTemplate.delete(key);
    }
}
