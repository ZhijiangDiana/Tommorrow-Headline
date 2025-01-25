package com.heima.common.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.StringRedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class PipelineService {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private CacheService cacheService;

    /**
     * 根据指定格式查询
     * @param pattern
     * @return
     */
    public Map<String, String> getKeyValueWithPipeline(String pattern) {
        Map<String, String> resultMap = new HashMap<>();

        // 1. 扫描符合前缀的 key
        List<String> keys = new ArrayList<>(cacheService.scan(pattern));

        if (keys.isEmpty()) {
            return resultMap; // 没有匹配的 key，直接返回
        }

        // 2. 使用 Pipeline 批量查询 key 对应的值
        List<String> values = this.getValuesWithPipeline(keys);

        // 3. 组合 key-value 结果
        for (int i = 0; i < keys.size(); i++) {
            String value = values.get(i);
            if (value != null)
                resultMap.put(keys.get(i), value);
        }

        return resultMap;
    }

    /**
     * 根据大量的key查询，返回键值对
     * @param keys
     * @return
     */
    public Map<String, String> getKeyValueWithPipeline(List<String> keys) {
        Map<String, String> resultMap = new HashMap<>();
        // 1. 使用 Pipeline 批量查询 key 对应的值
        List<String> values = this.getValuesWithPipeline(keys);

        // 2. 组合 key-value 结果
        for (int i = 0; i < keys.size(); i++) {
            String value = values.get(i);
            if (value != null)
                resultMap.put(keys.get(i), value);
        }

        return resultMap;
    }

    /**
     * 根据大量的key查询，返回值
     * @param keys
     * @return
     */
    public List<String> getValuesWithPipeline(Collection<String> keys) {
        List<Object> values = stringRedisTemplate.executePipelined(new RedisCallback<Object>() {
            @Override
            public Object doInRedis(RedisConnection redisConnection) throws DataAccessException {
                StringRedisConnection stringRedisConnection = (StringRedisConnection) redisConnection;
                for (String key : keys) {
                    stringRedisConnection.get(key);
                }
                return null;
            }
        });

        return (List<String>) (List<?>) values;
    }

    /**
     * 根据指定格式大量删除
     * @param pattern
     * @return
     */
    public Long deleteWithPipeline(String pattern) {
        Set<String> keys = cacheService.scan(pattern);

        List<Object> cnt = stringRedisTemplate.executePipelined(new RedisCallback<Object>() {
            @Override
            public Object doInRedis(RedisConnection redisConnection) throws DataAccessException {
                StringRedisConnection stringRedisConnection = (StringRedisConnection) redisConnection;
                for (String key : keys) {
                    stringRedisConnection.del(key);
                }

                return null;
            }
        });

        Long res = 0L;
        for (Object o : cnt) {
            Long count = (Long) o;
            res += count;
        }

        return res;
    }

    /**
     * 根据key大量删除
     * @param keys
     * @return
     */
    public Long deleteWithPipeline(Collection<String> keys) {
        List<Object> cnt = stringRedisTemplate.executePipelined(new RedisCallback<Object>() {
            @Override
            public Object doInRedis(RedisConnection redisConnection) throws DataAccessException {
                StringRedisConnection stringRedisConnection = (StringRedisConnection) redisConnection;
                for (String key : keys) {
                    stringRedisConnection.del(key);
                }

                return null;
            }
        });

        Long res = 0L;
        for (Object o : cnt) {
            Long count = (Long) o;
            res += count;
        }

        return res;
    }
}
