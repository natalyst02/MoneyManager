package vn.com.mbbank.adminportal.common.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import redis.clients.jedis.Connection;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;
import vn.com.mbbank.adminportal.common.util.Constant;
import vn.com.mbbank.adminportal.common.util.Json;

import java.time.Duration;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;

@Component
@Log4j2
@RequiredArgsConstructor
public class RedisClusterAdapter implements DisposableBean {
  private static JedisCluster jedisCluster = null;

  @Value("${redis.cluster.host}")
  private String host;

  @Value("${redis.cluster.maxIdle}")
  private int maxIdle;

  @Value("${redis.cluster.timeout}")
  private int timeout;

  @Value("${redis.cluster.soTimeOut}")
  private int soTimeOut;

  @Value("${redis.cluster.maxAttempts}")
  private int maxAttempts;

  @Value("${redis.cluster.username}")
  private String username;

  @Value("${redis.cluster.password}")
  private String password;

  private JedisCluster getJedisCluster() {
    if (jedisCluster == null) {
      GenericObjectPoolConfig<Connection> config = new GenericObjectPoolConfig();
      // Số lượng connect tối đa
      config.setMaxTotal(config.getMaxTotal());
      // SỐ lượng kết nối tối đa không hoạt động được giữ trong pool.
      // Nếu vượt quá số lượng này, các kết nối sẽ bị đóng để giảm tải cho hệ thống.
      config.setMaxIdle(config.getMaxIdle());
      config.setMinIdle(config.getMinIdle());
      String[] urlList = host.split(",");
      Set<HostAndPort> jedisClusterNodes = new HashSet<>();
      for (String url : urlList) {
        String[] node = url.split(":");
        jedisClusterNodes.add(new HostAndPort(node[0], Integer.parseInt(node[1])));
      }
      jedisCluster = new JedisCluster(jedisClusterNodes, timeout, soTimeOut, maxAttempts, username, password, "", config);
    }
    return jedisCluster;
  }

  public <T> boolean set(String key, long expireInSeconds, T value) {
    if (!StringUtils.hasText(key)) {
      return false;
    }
    String str = Json.encodeToString(value);
    try {
      jedisCluster = getJedisCluster();
      if (expireInSeconds < 1) {
        jedisCluster.set(key, str);
      } else {
        jedisCluster.setex(key, expireInSeconds, str);
      }
      return true;
    } catch (Exception e) {
      log.error("Set redis cluster error : " + e.getMessage());
      return false;
    }
  }

  public <T> T get(String key, Class<T> clazz) {
    try {
      jedisCluster = getJedisCluster();
      var value = jedisCluster.get(key);
      if (!StringUtils.hasText(value)) {
        log.info("No value found from redis with key: " + key);
        return null;
      }
      return Json.decode(value.getBytes(), clazz);
    } catch (Exception e) {
      log.error("Get redis cluster error : " + e.getMessage());
      return null;
    }
  }

  public <T> T computeIfAbsent(String key, Class<T> clazz, Supplier<? extends T> supplier) {
    Objects.requireNonNull(supplier);
    T value;
    value = get(key, clazz);
    if (value == null) {
      value = supplier.get();
      set(key, Constant.REDIS_DEFAULT_DURATION, value);
    }
    return value;
  }

  public <T> T computeIfAbsent(String key, Class<T> clazz, long duration, Supplier<? extends T> supplier) {
    Objects.requireNonNull(supplier);
    T value;
    value = get(key, clazz);
    if (value == null) {
      value = supplier.get();
      set(key, duration, value);
    }
    return value;
  }

  public boolean exists(String key) {
    try {
      jedisCluster = getJedisCluster();
      return jedisCluster.exists(key);
    } catch (Exception e) {
      log.error("Get redis cluster error : " + e.getMessage());
      return false;
    }
  }

  public boolean delete(String key) {
    try {
      jedisCluster = getJedisCluster();
      //phương thức xóa key redis cluster trả về số lượng key đã bị xóa
      return jedisCluster.del(key) > 0;
    } catch (Exception e) {
      log.error("Delete redis key error: " + e.getMessage());
      return false;
    }
  }

  @Override
  public void destroy() {
    if (jedisCluster != null) {
      try {
        jedisCluster.close();
      } catch (Exception e) {
        log.error("Error closing JedisCluster: {}", e.getMessage());
      }
    }
  }

  public long increaseAndGet(String key) {
    return jedisCluster.incr(key);
  }

  public long increaseAndGet(String key, long increment) {
    return jedisCluster.incrBy(key, increment);
  }

  public boolean expire(String key, long seconds) {
    return jedisCluster.expire(key, seconds) == 1;
  }
}
