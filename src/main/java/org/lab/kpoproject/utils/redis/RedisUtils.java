package org.lab.kpoproject.utils.redis;

import org.lab.kpoproject.exception.KeyNotFoundException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class RedisUtils {
    private static final String PREFIX = "refresh_token: ";
    private final StringRedisTemplate template;

    public RedisUtils(final StringRedisTemplate template) {
        this.template = template;
    }

    public boolean add(final String email, final String token) {
        final String key = PREFIX + email;
        if (template.hasKey(key)) {
            return false;
        }

        template.opsForValue().set(key, token);
        return true;
    }

    public String get(final String email) {
        final String key = PREFIX + email;
        if (template.hasKey(key)) {
            return template.opsForValue().get(key);
        }
        throw new KeyNotFoundException("Ключ с email = " +
                email + " не найден");
    }

    public boolean update(final String key, final String value) {
        final String newKey = PREFIX + key;
        if (template.hasKey(newKey)) {
            template.opsForValue().set(newKey, value);
            return true;
        }
        return false;
    }

    public boolean exist(final String email) {
        return template.hasKey(PREFIX + email);
    }

    public boolean delete(final String email) {
        final String key = PREFIX + email;
        if (!template.hasKey(key)) {
            return false;
        }
        template.delete(key);
        return true;
    }

}
