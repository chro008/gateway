package com.sensetime.tetras.et.gateway.config;

import com.alibaba.cloud.nacos.NacosConfigProperties;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.listener.Listener;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sensetime.tetras.et.gateway.properties.GatewayRouteConfigProperties;
import com.sensetime.tetras.et.gateway.service.RouteService;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executor;

/**
 * @author lixiaoming
 * @date 2022/12/6 16:36
 */
@Component
@RefreshScope
@Slf4j
public class GatewayRouteInitConfig {

    @Autowired
    private GatewayRouteConfigProperties configProperties;

    @Autowired
    private NacosConfigProperties nacosConfigProperties;

    @Autowired
    private ConfigService configService;

    @Autowired
    private RouteService routeService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @PostConstruct
    public void init() {
        try {
            String initConfigInfo = configService.getConfigAndSignListener(configProperties.getDataId(), configProperties.getGroup(), nacosConfigProperties.getTimeout(), new Listener() {
                @Override
                public Executor getExecutor() {
                    return null;
                }

                @Override
                public void receiveConfigInfo(String configInfo) {
                    if (StringUtils.hasText(configInfo)) {
                        List<RouteDefinition> routeDefinitions = null;
                        try {
                            routeDefinitions = objectMapper.readValue(configInfo, new TypeReference<>() {
                            });
                        } catch (JsonProcessingException e) {
                            e.printStackTrace();
                        }
                        for (RouteDefinition definition : Objects.requireNonNull(routeDefinitions)) {
                            routeService.update(definition);
                        }
                    } else {
                        System.out.println("当前网关无动态路由配置");
                    }
                }
            });
            if (StringUtils.hasText(initConfigInfo)) {
                List<RouteDefinition> routeDefinitions = objectMapper.readValue(initConfigInfo, new TypeReference<>() {
                });
                for (RouteDefinition definition : Objects.requireNonNull(routeDefinitions)) {
                    routeService.add(definition);
                }
            } else {
                System.out.println("当前网关无动态路由配置");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
