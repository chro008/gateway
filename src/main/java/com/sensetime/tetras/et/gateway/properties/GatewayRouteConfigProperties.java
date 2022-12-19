package com.sensetime.tetras.et.gateway.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;


/**
 * @author lixiaoming
 * @date 2022/12/6 16:22
 */
@Component
@Data
@ConfigurationProperties(prefix = "gateway.routes.config")
public class GatewayRouteConfigProperties {

    private String dataId;
    private String group;
    private String namespace;

}
