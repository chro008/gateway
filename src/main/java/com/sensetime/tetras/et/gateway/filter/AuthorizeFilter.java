package com.sensetime.tetras.et.gateway.filter;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.sensetime.tetras.et.gateway.constants.GatewayConstants;
import com.sensetime.tetras.et.gateway.dto.Response;
import com.sensetime.tetras.et.gateway.manager.TokenCacheManager;
import com.sensetime.tetras.et.gateway.properties.AppProperties;
import com.sensetime.tetras.et.gateway.util.HttpUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

/**
 * @author lixiaoming
 * @date 2022/12/8 11:29
 * 鉴权过滤器
 */
@Component
@Slf4j
public class AuthorizeFilter implements GlobalFilter, Ordered {

    @Autowired
    private AppProperties appProperties;

    @Autowired
    private TokenCacheManager cacheManager;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();
        log.info("网关拦截：path {}", path);
        // 不需要鉴权的path 直接放过
        List<String> authorWhiteList = appProperties.getAuthorWhiteList();
        if (!CollectionUtils.isEmpty(authorWhiteList) && authorWhiteList.contains(path)) {
            return chain.filter(exchange);
        }

        // 判断header中token和uid是否存在，不存在直接返回异常信息
        ServerHttpResponse response = exchange.getResponse();
        String token = exchange.getRequest().getHeaders().getFirst("token");
        if (!StringUtils.hasText(token)) {
            Response<String> unAuthorRes = Response.fail(10401, "用户信息已过期，请重新登录");
            String message = JSON.toJSONString(unAuthorRes);
            DataBuffer buffer = response.bufferFactory().wrap(message.getBytes(StandardCharsets.UTF_8));
            return response.writeWith(Mono.just(buffer));
        }
        String uid = exchange.getRequest().getHeaders().getFirst("uid");
        if (!StringUtils.hasText(uid)) {
            Response<String> invalidUidRes = Response.fail(10402, "没有uid");
            String message = JSON.toJSONString(invalidUidRes);
            DataBuffer buffer = response.bufferFactory().wrap(message.getBytes(StandardCharsets.UTF_8));
            return response.writeWith(Mono.just(buffer));
        }

        // 鉴权成功则放行请求，否则拦截请求，返回错误信息
        String verifyUrl = appProperties.getVerifyUrl();
        if (!StringUtils.hasText(verifyUrl)) {
            Response<String> invalidVerifyUrlRes = Response.fail("鉴权地址不存在，请联系管理员配置鉴权地址！");
            String message = JSON.toJSONString(invalidVerifyUrlRes);
            DataBuffer buffer = response.bufferFactory().wrap(message.getBytes(StandardCharsets.UTF_8));
            return response.writeWith(Mono.just(buffer));
        }

        String wxUid = exchange.getRequest().getHeaders().getFirst("wxuserid");
        HttpHeaders headers = new HttpHeaders();
        headers.put("WXUSERID", Collections.singletonList(wxUid));
        headers.put("TOKEN", Collections.singletonList(token));
        headers.put("UID", Collections.singletonList(uid));

        boolean verify = cacheManager.get(headers);
        if (verify) {
            log.info("verify from token cache manager!");
            return chain.filter(exchange);
        }

        try {
            JSONObject res = HttpUtil.get(verifyUrl, null, headers, JSONObject.class);
            int resCode = res.getIntValue("code");
            if (resCode == GatewayConstants.SUCCEED_CODE_200000) {
                cacheManager.set(headers);
                return chain.filter(exchange);
            } else {
                Response<String> verifyFailRes = Response.fail(resCode, res.getString("message"));
                String message = JSON.toJSONString(verifyFailRes);
                DataBuffer buffer = response.bufferFactory().wrap(message.getBytes(StandardCharsets.UTF_8));
                return response.writeWith(Mono.just(buffer));
            }
        } catch (Exception e) {
            log.error("verify failed, url is {}, header is {}", verifyUrl, headers, e);
            Response<String> verifyFailRes = Response.fail(10403, "请求验证的地址不正确，修改代码");
            String message = JSON.toJSONString(verifyFailRes);
            DataBuffer buffer = response.bufferFactory().wrap(message.getBytes(StandardCharsets.UTF_8));
            return response.writeWith(Mono.just(buffer));
        }

    }


    @Override
    public int getOrder() {
        return 0;
    }
}
