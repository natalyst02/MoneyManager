package vn.com.mbbank.adminportal.common.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import vn.com.mbbank.adminportal.common.config.RedisClusterAdapter;
import vn.com.mbbank.adminportal.common.service.RateLimiter;

@Service
@RequiredArgsConstructor
@Log4j2
public class RedisRateLimiterImpl implements RateLimiter {
  private final RedisClusterAdapter redisClusterAdapter;

  @Override
  public boolean isAllowed(String key, long limit, long windowTimeSeconds) {
    var count = redisClusterAdapter.increaseAndGet(key);
    if (count == 1) {
      return redisClusterAdapter.expire(key, windowTimeSeconds);
    } else {
      return count <= limit;
    }
  }
}
