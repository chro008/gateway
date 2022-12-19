package com.sensetime.tetras.et.gateway.config;

import com.alibaba.cloud.nacos.NacosConfigProperties;
import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.PropertyKeyConst;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.exception.NacosException;
import com.sensetime.tetras.et.gateway.properties.GatewayRouteConfigProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

/**
 * @author lixiaoming
 * @date 2022/12/6 16:27
 */
@Configuration
public class GatewayConfigServiceConfig {

    @Autowired
    private GatewayRouteConfigProperties gatewayProperties;

    @Autowired
    private NacosConfigProperties nacosProperties;

    @Bean
    public ConfigService configService() throws NacosException {
        Properties properties = new Properties();
        properties.put(PropertyKeyConst.NAMESPACE, gatewayProperties.getNamespace());
        properties.put(PropertyKeyConst.SERVER_ADDR, nacosProperties.getServerAddr());
        properties.put(PropertyKeyConst.CONTEXT_PATH, "");
        properties.put(PropertyKeyConst.USERNAME, "nacos");
        properties.put(PropertyKeyConst.PASSWORD, "nacos");
        return NacosFactory.createConfigService(properties);
    }

}
