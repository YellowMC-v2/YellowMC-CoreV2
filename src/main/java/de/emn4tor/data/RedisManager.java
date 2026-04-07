package de.emn4tor.data;

/*
 *  @author: Emn4tor
 *  @created: 24.07.2025
 */

import org.bukkit.configuration.file.FileConfiguration;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisPubSub;

import java.time.Duration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class RedisManager {

    private static volatile RedisManager instance; // volatile ensures visibility across threads
    private static final Logger LOGGER = Logger.getLogger(RedisManager.class.getName());

    private JedisPool pool;          // replaces raw pub/sub Jedis — pools are thread-safe and auto-reconnect
    private String redisHost;
    private int redisPort;
    private String redisPassword;

    // Dedicated thread pool for subscriber threads — avoids unbounded raw Thread spawning
    private final ExecutorService subscriberExecutor = Executors.newCachedThreadPool();

    private RedisManager() {}

    // Double-checked locking — safe with volatile, avoids unnecessary synchronization after init
    public static RedisManager getInstance() {
        if (instance == null) {
            synchronized (RedisManager.class) {
                if (instance == null) {
                    instance = new RedisManager();
                }
            }
        }
        return instance;
    }

    public synchronized void connect(FileConfiguration config) {
        if (pool != null && !pool.isClosed()) return;

        this.redisHost = config.getString("credentials.redis.host");
        this.redisPort = config.getInt("credentials.redis.port");
        this.redisPassword = config.getString("credentials.redis.password");

        // Validate required config before attempting connection
        if (redisHost == null || redisHost.isBlank()) {
            throw new IllegalStateException("[RedisManager] Redis host is not configured.");
        }
        if (redisPort <= 0 || redisPort > 65535) {
            throw new IllegalStateException("[RedisManager] Redis port is invalid: " + redisPort);
        }

        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(16);                          // max concurrent connections
        poolConfig.setMaxIdle(8);
        poolConfig.setMinIdle(2);
        poolConfig.setTestOnBorrow(true);                    // validates connection before use
        poolConfig.setTestOnReturn(true);
        poolConfig.setTestWhileIdle(true);
        poolConfig.setMinEvictableIdleTime(Duration.ofSeconds(60));
        poolConfig.setTimeBetweenEvictionRuns(Duration.ofSeconds(30));
        poolConfig.setBlockWhenExhausted(true);
        poolConfig.setMaxWait(Duration.ofSeconds(5));        // fail fast if pool is saturated

        // Use password-aware constructor only when needed — avoids passing null auth strings
        if (redisPassword != null && !redisPassword.isBlank()) {
            pool = new JedisPool(poolConfig, redisHost, redisPort, 2000, redisPassword);
        } else {
            pool = new JedisPool(poolConfig, redisHost, redisPort, 2000);
        }

        LOGGER.info("[RedisManager] Connected to Redis at " + redisHost + ":" + redisPort);
    }

    // publish: borrows a connection, uses it, returns it automatically via try-with-resources
    public void publish(String channel, String message) {
        validateNotBlank(channel, "channel");
        validateNotNull(message, "message");
        try (Jedis jedis = pool.getResource()) {
            jedis.publish(channel, message);
        }
    }

    // setTemporary: enforces a positive TTL to prevent accidental persistent keys
    public void setTemporary(String key, String value, int seconds) {
        validateNotBlank(key, "key");
        validateNotNull(value, "value");
        if (seconds <= 0) throw new IllegalArgumentException("[RedisManager] TTL must be positive.");
        try (Jedis jedis = pool.getResource()) {
            jedis.setex(key, seconds, value);
        }
    }

    // subscribe: each subscriber gets its own fresh Jedis (blocking call) on a managed thread
    public void subscribe(String channel, JedisPubSub listener) {
        validateNotBlank(channel, "channel");
        if (listener == null) throw new IllegalArgumentException("[RedisManager] Listener must not be null.");

        subscriberExecutor.submit(() -> {
            // Subscriber connections must be dedicated — never borrowed from the pool
            try (Jedis subscriber = buildConnection()) {
                LOGGER.info("[RedisManager] Subscribing to channel: " + channel);
                subscriber.subscribe(listener, channel); // blocks until unsubscribed
            } catch (Exception e) {
                LOGGER.severe("[RedisManager] Subscriber error on channel '" + channel + "': " + e.getMessage());
                // Do NOT print stack traces containing host/port to stdout in production
            }
        });
    }

    public String get(String key) {
        validateNotBlank(key, "key");
        try (Jedis jedis = pool.getResource()) {
            return jedis.get(key);
        }
    }

    public void delete(String key) {
        validateNotBlank(key, "key");
        try (Jedis jedis = pool.getResource()) {
            jedis.del(key);
        }
    }

    // Graceful shutdown: stops accepting new tasks, waits for subscribers, then destroys pool
    public synchronized void close() {
        subscriberExecutor.shutdown();
        try {
            if (!subscriberExecutor.awaitTermination(5, TimeUnit.SECONDS)) {
                subscriberExecutor.shutdownNow();
            }
        } catch (InterruptedException e) {
            subscriberExecutor.shutdownNow();
            Thread.currentThread().interrupt();
        }
        if (pool != null && !pool.isClosed()) {
            pool.close();
            LOGGER.info("[RedisManager] Redis connection pool closed.");
        }
    }

    // Builds a standalone Jedis connection (used for subscriber threads only)
    private Jedis buildConnection() {
        Jedis jedis = new Jedis(redisHost, redisPort);
        if (redisPassword != null && !redisPassword.isBlank()) {
            jedis.auth(redisPassword);
        }
        return jedis;
    }

    private void validateNotBlank(String value, String name) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("[RedisManager] '" + name + "' must not be blank.");
        }
    }

    private void validateNotNull(Object value, String name) {
        if (value == null) {
            throw new IllegalArgumentException("[RedisManager] '" + name + "' must not be null.");
        }
    }
}