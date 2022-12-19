package com.sensetime.tetras.et.gateway.properties;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Data
@RefreshScope
@Configuration
@ConfigurationProperties(prefix = "gateway.prop")
public class AppProperties {

    /**
     * 鉴权白名单，无需鉴权的path
     */
    @Value("${author_whitelist:}")
    private List<String> authorWhiteList;

    /**
     * 鉴权认证地址
     */
    @Value("${do_verify_url:}")
    private String verifyUrl;

    /**
     * token在网关层缓存时间，单位ms 默认10分钟
     */
    @Value("${token_cache_time:600000}")
    private int tokenCacheTime;

}
