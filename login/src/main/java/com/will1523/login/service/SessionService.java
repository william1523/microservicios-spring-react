package com.will1523.login.service;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Service
public class SessionService {

    private final StringRedisTemplate redisTemplate;
    private static final long EXPIRATION_TIME_MS = 864_000_000; // 10 days, matching JwtUtils

    public SessionService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public Map<String, String> getAllSessions() {
        Set<String> keys = redisTemplate.keys("session:*");
        Map<String, String> sessions = new HashMap<>();
        if (keys != null) {
            for (String key : keys) {
                String username = key.replace("session:", "");
                String token = redisTemplate.opsForValue().get(key);
                sessions.put(username, token);
            }
        }
        return sessions;
    }

    public void saveSession(String username, String token) {
        String key = "session:" + username;
        redisTemplate.opsForValue().set(key, token, Duration.ofMillis(EXPIRATION_TIME_MS));
    }

    public String getSession(String username) {
        String key = "session:" + username;
        return redisTemplate.opsForValue().get(key);
    }
}
