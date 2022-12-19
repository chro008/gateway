package com.sensetime.tetras.et.gateway.service;

import org.springframework.cloud.gateway.route.RouteDefinition;

/**
 * @author lixiaoming
 * @date 2022/12/6 16:12
 */
public interface RouteService {

    void update(RouteDefinition routeDefinition);

    void add(RouteDefinition routeDefinition);

}
