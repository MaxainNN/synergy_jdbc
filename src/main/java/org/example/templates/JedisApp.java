package org.example.templates;

import redis.clients.jedis.Jedis;

/**
 * Class - example from the notes for connecting to Redis
 * using Jedis (dependency in POM)
 */
public class JedisApp {

    public static void main(String[] args) {
        try (Jedis client = new Jedis("localhost", 6379)) {
            client.set("username","Maxim");
            System.out.println(client.get("username"));
        }
    }
}
