package com.ybean.manager;

import com.ybean.model.Session;

import java.security.SecureRandom;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class SessionManager {

    // session 过期时间
    private final int SESSION_EXPIRATION_MINUTES = 10;
    // 存储用户session
    private final ConcurrentHashMap<Integer, Session> sessionMap = new ConcurrentHashMap<>();

    public SessionManager() {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        System.out.println("清除session定时任务启动");
        scheduler.scheduleAtFixedRate(this::cleanExpiredSessions, 1, 1, TimeUnit.MINUTES);
    }

    public String getSession(int customerId) {
        return sessionMap.compute(customerId, (id, session) -> {
            long curTime = System.currentTimeMillis();
            if (session == null || curTime > session.getExpireTime()) {
                // 会话不存在或者已过期，创建新的会话
                return new Session(generateSessionKey(), SESSION_EXPIRATION_MINUTES);
            }
            // 如果会话仍然有效，则直接返回现有的会话
            return session;
        }).getKey();
    }

    public ConcurrentHashMap<Integer, Session> getAllSession() {
        return sessionMap;
    }

    private String generateSessionKey() {
        SecureRandom secureRandom = new SecureRandom();
        byte[] randomBytes = new byte[16]; // 生成16字节（128位）的随机数
        secureRandom.nextBytes(randomBytes);

        // 将字节数组转换为十六进制字符串表示形式
        StringBuilder hexString = new StringBuilder();
        for (byte b : randomBytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString().toUpperCase();
    }

    private void cleanExpiredSessions() {
        long curTime = System.currentTimeMillis();
        sessionMap.forEach((id, session) -> {
            Long expireTime = session.getExpireTime();
            if (curTime > expireTime) {
                System.out.println("清除用户: " + id + "的session");
                sessionMap.remove(id);
            }
        });
    }
}
