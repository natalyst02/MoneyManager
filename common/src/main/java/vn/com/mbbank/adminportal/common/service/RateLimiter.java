package vn.com.mbbank.adminportal.common.service;

public interface RateLimiter {
  boolean isAllowed(String key, long limit, long windowTimeSeconds);
}
