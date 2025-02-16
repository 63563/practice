package com.ybean.model;

import java.util.concurrent.TimeUnit;

public class Session {

    /**
     * session key
     */
    private final String key;

    /**
     * 过期时间
     */
    private Long expireTime;

    public Session(String key, int expirationMinutes) {
        this.key = key;
        this.expireTime = System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(expirationMinutes);
    }

    /**
     * 续过期时间
     *
     * @param expirationMinutes 过期时间
     */
    public void renew(int expirationMinutes) {
        this.expireTime = System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(expirationMinutes);
    }

    public String getKey() {
        return key;
    }

    public Long getExpireTime() {
        return expireTime;
    }
}
