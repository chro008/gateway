package com.sensetime.tetras.et.gateway.service.impl;

import com.sensetime.tetras.et.gateway.service.RouteService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.event.RefreshRoutesEvent;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionWriter;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

/**
 * @author lixiaoming
 * @date 2022/12/6 16:26
 */
@Service
@Slf4j
public class RouteServiceImpl implements RouteService, ApplicationEventPublisherAware {

    @Autowired
    RouteDefinitionWriter writer;

    @Autowired
    ApplicationEventPublisher publisher;

    @Override
    public void update(RouteDefinition routeDefinition) {
        writer.delete(Mono.just(routeDefinition.getId()));
        writer.save(Mono.just(routeDefinition)).subscribe();
        this.publisher.publishEvent(new RefreshRoutesEvent(this));
        log.info("update route: {}", routeDefinition.getId());
    }

    @Override
    public void add(RouteDefinition routeDefinition) {
        writer.save(Mono.just(routeDefinition)).subscribe();
        this.publisher.publishEvent(new RefreshRoutesEvent(this));
        log.info("add route: {}", routeDefinition.getId());
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.publisher = applicationEventPublisher;
    }
}
