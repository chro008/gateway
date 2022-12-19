package com.sensetime.tetras.et.gateway.manager;

import com.alibaba.fastjson2.JSON;
import com.sensetime.tetras.et.gateway.properties.AppProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author lixiaoming
 * @date 2022/12/8 20:51
 * 基于jvm实现的token缓存管理器
 * 只将认证成功的信息进行缓存，认证失败的暂时不做缓存
 */
@Component
@Slf4j
public class TokenCacheManager {

    @Autowired
    private AppProperties appProperties;

    private Map<String, Long> cacheMap = new ConcurrentHashMap<>(128);

    private static final int MAX_CACHE_LIMIT = 10000;

    /**
     * 从缓存中获取
     *
     * @param headers
     */
    public boolean get(HttpHeaders headers) {
        String jsonString = JSON.toJSONString(headers);
        String key = Base64.getEncoder().encodeToString(jsonString.getBytes(StandardCharsets.UTF_8));
        Long cacheTime = cacheMap.get(key);
        if (Objects.isNull(cacheTime)) {
            return false;
        }
        boolean isPass = isPass(cacheTime);
        // 惰性删除
        if (isPass) {
            cacheMap.remove(key);
            return false;
        }
        return true;
    }

    /**
     * 缓存是否过期
     *
     * @param cacheTime
     * @return
     */
    private boolean isPass(Long cacheTime) {
        int cacheTimeConfig = appProperties.getTokenCacheTime();
        long cachedTime = System.currentTimeMillis() - cacheTime;
        return cachedTime > cacheTimeConfig;
    }

    /**
     * 将token进行缓存
     *
     * @param headers
     */
    public void set(HttpHeaders headers) {
        // 缓存上线判断  防止oom
        if (cacheMap.size() >= MAX_CACHE_LIMIT) {
            return;
        }
        String jsonString = JSON.toJSONString(headers);
        String key = Base64.getEncoder().encodeToString(jsonString.getBytes(StandardCharsets.UTF_8));
        cacheMap.put(key, System.currentTimeMillis());
    }

    /**
     * 定时清除缓存 暂定每5分钟执行一次
     */
    @Scheduled(cron = "0 0/5 * * * ?  ")
    public void clearOldCache() {
        log.info("start clear old cache, current number is {}", cacheMap.size());
        int count = 0;
        for (Map.Entry<String, Long> entry : cacheMap.entrySet()) {
            long cacheTime = entry.getValue();
            if (isPass(cacheTime)) {
                count++;
                cacheMap.remove(entry.getKey());
            }
        }
        log.info("finish clear old cache, current number is {}, delete number is {}", cacheMap.size(), count);
    }

}
