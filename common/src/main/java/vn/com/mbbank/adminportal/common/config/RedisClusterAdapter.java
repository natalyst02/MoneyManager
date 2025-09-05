package vn.com.mbbank.adminportal.common.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import vn.com.mbbank.adminportal.common.util.Constant;
import vn.com.mbbank.adminportal.common.util.Json;

import java.util.Objects;
import java.util.function.Supplier;

@Component
@Log4j2
@RequiredArgsConstructor
public class RedisClusterAdapter implements DisposableBean {
    private static JedisPool jedisPool = null;

    @Value("${redis.cluster.host}")
    private String host;  // ví dụ: 43.216.3.30:6379

    @Value("${redis.cluster.maxIdle}")
    private int maxIdle;

    @Value("${redis.cluster.maxTotal}")
    private int maxTotal;

    @Value("${redis.cluster.timeout}")
    private int timeout;

    @Value("${redis.cluster.username:}")   // mặc định trống
    private String username;

    @Value("${redis.cluster.password:}")   // mặc định trống
    private String password;

    private JedisPool getJedisPool() {
        if (jedisPool == null) {
            GenericObjectPoolConfig<?> config = new GenericObjectPoolConfig<>();
            config.setMaxTotal(maxTotal);
            config.setMaxIdle(maxIdle);
            config.setMinIdle(1);

            String[] node = host.split(":");
            String redisHost = node[0];
            int redisPort = Integer.parseInt(node[1]);

            if (StringUtils.hasText(password)) {
                jedisPool = new JedisPool((GenericObjectPoolConfig<Jedis>) config, redisHost, redisPort, timeout, password);
            } else {
                jedisPool = new JedisPool((GenericObjectPoolConfig<Jedis>) config, redisHost, redisPort, timeout);
            }
        }
        return jedisPool;
    }

    public <T> boolean set(String key, long expireInSeconds, T value) {
        if (!StringUtils.hasText(key)) {
            return false;
        }
        String str = Json.encodeToString(value);
        try (Jedis jedis = getJedisPool().getResource()) {
            if (expireInSeconds < 1) {
                jedis.set(key, str);
            } else {
                jedis.setex(key, expireInSeconds, str);
            }
            return true;
        } catch (Exception e) {
            log.error("Set redis error : " + e.getMessage());
            return false;
        }
    }

    public <T> T get(String key, Class<T> clazz) {
        try (Jedis jedis = getJedisPool().getResource()) {
            var value = jedis.get(key);
            if (!StringUtils.hasText(value)) {
                log.info("No value found from redis with key: " + key);
                return null;
            }
            return Json.decode(value.getBytes(), clazz);
        } catch (Exception e) {
            log.error("Get redis error : " + e.getMessage());
            return null;
        }
    }

    public <T> T computeIfAbsent(String key, Class<T> clazz, Supplier<? extends T> supplier) {
        return computeIfAbsent(key, clazz, Constant.REDIS_DEFAULT_DURATION, supplier);
    }

    public <T> T computeIfAbsent(String key, Class<T> clazz, long duration, Supplier<? extends T> supplier) {
        Objects.requireNonNull(supplier);
        T value = get(key, clazz);
        if (value == null) {
            value = supplier.get();
            set(key, duration, value);
        }
        return value;
    }

    public boolean exists(String key) {
        try (Jedis jedis = getJedisPool().getResource()) {
            return jedis.exists(key);
        } catch (Exception e) {
            log.error("Exists redis error : " + e.getMessage());
            return false;
        }
    }

    public boolean delete(String key) {
        try (Jedis jedis = getJedisPool().getResource()) {
            return jedis.del(key) > 0;
        } catch (Exception e) {
            log.error("Delete redis error: " + e.getMessage());
            return false;
        }
    }

    public long increaseAndGet(String key) {
        try (Jedis jedis = getJedisPool().getResource()) {
            return jedis.incr(key);
        }
    }

    public long increaseAndGet(String key, long increment) {
        try (Jedis jedis = getJedisPool().getResource()) {
            return jedis.incrBy(key, increment);
        }
    }

    public boolean expire(String key, long seconds) {
        try (Jedis jedis = getJedisPool().getResource()) {
            return jedis.expire(key, seconds) == 1;
        }
    }

    @Override
    public void destroy() {
        if (jedisPool != null) {
            try {
                jedisPool.close();
            } catch (Exception e) {
                log.error("Error closing JedisPool: {}", e.getMessage());
            }
        }
    }
}
