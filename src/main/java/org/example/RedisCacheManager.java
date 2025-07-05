package org.example;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * Class for manage and implement Redis
 */
public class RedisCacheManager {

    private final JedisPool jedisPool;

    public RedisCacheManager(String host, int port){
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        this.jedisPool = new JedisPool(jedisPoolConfig, host, port);
    }

    /**
     * Stores a document in Redis cache with a time-to-live (TTL) expiration
     * @param key The unique identifier for the cached document ("type:id")
     * @param value The document content to cache, serialized as a String (JSON)
     * @param ttlSeconds Time-to-live in seconds (3600 for stable data)
     */
    public void cacheDocument(String key, String value, int ttlSeconds) {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.setex(key, ttlSeconds, value);
        }
    }

    /**
     * Get cache document with the key
     * @param key The unique identifier for the cached document ("type:id")
     * @return document that has defined key
     */
    public String getCachedDocument(String key) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.get(key);
        }
    }

    /**
     * Close connection with Jedis
     */
    public void close() {
        jedisPool.close();
        System.out.println("Connection with Jedis has been closed.");
    }
}
