package de.emn4tor.data;

/*
 *  @author: Emn4tor
 *  @created: 24.07.2025
 */

import org.bukkit.configuration.file.FileConfiguration;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;

public class RedisManager {
    private static RedisManager instance;
    private Jedis pub;
    private Jedis sub;

    // Redis connection details
    private String redisHost;
    private int redisPort;
    private String redisPassword;

    private RedisManager() {}

    public static synchronized RedisManager getInstance() {
        if (instance == null) {
            instance = new RedisManager();
        }
        return instance;
    }

    public void connect(FileConfiguration config) {
        if (pub != null) return;

        this.redisHost = config.getString("redis.host");
        this.redisPort = config.getInt("redis.port");
        this.redisPassword = config.getString("redis.password");

        pub = new Jedis(redisHost, redisPort);
        pub.auth(redisPassword);

        sub = new Jedis(redisHost, redisPort);
        sub.auth(redisPassword);
    }


    public void publish(String channel, String msg) {
        pub.publish(channel, msg);
    }

    public void setTemporary(String channel, String msg, int seconds) {
        pub.setex(channel, seconds, msg);
    }

    public void subscribe(String channel, JedisPubSub listener) {
        new Thread(() -> {
            try {
                Jedis subscriber = new Jedis(redisHost, redisPort);
                subscriber.auth(redisPassword);
                System.out.println("[RedisManager] Subscribing to channel: " + channel);
                subscriber.subscribe(listener, channel);
            } catch (Exception e) {
                System.err.println("[RedisManager] Failed to subscribe: " + e.getMessage());
                e.printStackTrace();
            }
        }).start();
    }


    public void close() {
        if (pub != null) pub.close();
        if (sub != null) sub.close();
    }

    public String get(String key) {
        return pub.get(key);
    }

    public void delete(String key) {
        pub.del(key);
    }
}