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

        this.redisHost = config.getString("credentials.redis.host");
        this.redisPort = config.getInt("credentials.redis.port");
        this.redisPassword = config.getString("credentials.redis.password");

        pub = new Jedis(redisHost, redisPort);
        if (redisPassword != null && !redisPassword.isEmpty()) {
            pub.auth(redisPassword);
        }

        sub = new Jedis(redisHost, redisPort);
        if (redisPassword != null && !redisPassword.isEmpty()) {
            sub.auth(redisPassword);
        }
    }


    public void publish(String channel, String msg) {
        pub.publish(channel, msg);
    }

    public void setTemporary(String channel, String msg, int seconds) {
        pub.setex(channel, seconds, msg);
    }

    public void subscribe(String channel, JedisPubSub listener) {
        new Thread(() -> {
            try (Jedis subscriber = new Jedis(redisHost, redisPort)) {
                if (redisPassword != null && !redisPassword.isEmpty()) {
                    subscriber.auth(redisPassword);
                }
                System.out.println("[RedisManager] Subscribing to: " + channel);
                subscriber.subscribe(listener, channel);
                System.out.println("[RedisManager] Unsubscribed from: " + channel);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, "Redis-Subscriber-Thread").start();
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