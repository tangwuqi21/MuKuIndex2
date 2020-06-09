package com.rhdk.purchasingservice.common.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * redis操作工具类.</br> (基于RedisTemplate)
 *
 * @author xcbeyond 2020年6月5日下午2:56:24
 */
@Component
public class RedisUtils {

  @Autowired private RedisTemplate redisTemplate;

  /**
   * 读取缓存
   *
   * @param key
   * @return
   */
  public Object get(final String key) {
    return redisTemplate.opsForValue().get(key);
  }

  /** 写入缓存 */
  public boolean set(final String key, String value) {
    boolean result = false;
    try {
      if (redisTemplate.hasKey(key)) {
        getAndSet(key, value);
      } else {
        redisTemplate.opsForValue().set(key, value);
      }
      result = true;
    } catch (Exception e) {
      e.printStackTrace();
    }
    return result;
  }

  /** 写入缓存，增加缓存的有效时间 */
  public boolean setWithTime(final String key, String value, Long hour, TimeUnit unit) {
    boolean result = false;
    try {
      if (redisTemplate.hasKey(key)) {
        getAndSet(key, value);
      } else {
        redisTemplate.opsForValue().set(key, value, hour, unit);
      }
      result = true;
    } catch (Exception e) {
      e.printStackTrace();
    }
    return result;
  }

  /** 批量写入到缓存中 */
  public void setDataList(List<String> dataList, String tempId) {
    dataList
        .parallelStream()
        .forEach(
            a -> {
              set(a, tempId);
            });
  }

  /** 更新缓存 */
  public boolean getAndSet(final String key, String value) {
    boolean result = false;
    try {
      redisTemplate.opsForValue().getAndSet(key, value);
      result = true;
    } catch (Exception e) {
      e.printStackTrace();
    }
    return result;
  }

  /** 删除缓存 */
  public boolean delete(final String key) {
    boolean result = false;
    try {
      redisTemplate.delete(key);
      result = true;
    } catch (Exception e) {
      e.printStackTrace();
    }
    return result;
  }
}
