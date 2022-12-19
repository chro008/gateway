package com.sensetime.tetras.et.gateway.util;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.nio.charset.StandardCharsets;

@Slf4j
public class HttpUtil {

    private static final RestTemplate REST_TEMPLATE = new RestTemplate();

    static {
        REST_TEMPLATE.getMessageConverters().set(1, new StringHttpMessageConverter(StandardCharsets.UTF_8));
    }

    /**
     * 发送POST JSON请求
     *
     * @param url  请求地址
     * @param data 请求数据
     */
    public static JSONObject post(String url, Object data) {
        return post(url, data, JSONObject.class);
    }

    /**
     * 发送POST JSON请求
     *
     * @param url         请求地址
     * @param data        请求数据
     * @param returnClass 返回类型
     */
    public static <T> T post(String url, Object data, Class<T> returnClass) {
        log.info("发送HTTP post请求 url: {},data: {}", url, data);
        String resultStr = REST_TEMPLATE.postForObject(url, data, String.class);
        log.info("HTTP POST请求返回数据: {}", resultStr);
        return JSON.parseObject(resultStr, returnClass);
    }

    /**
     * 发送POST JSON请求(携带鉴权)
     *
     * @param url            请求地址
     * @param data           请求数据
     * @param requestHeaders 请求头
     * @param returnClass    返回类型
     */
    public static <T> T post(String url, Object data, HttpHeaders requestHeaders, Class<T> returnClass) {
        log.info("发送HTTP post请求 url: {},data: {},header:{}", url, data, requestHeaders);
        HttpEntity<Object> requestEntity = new HttpEntity<>(data, requestHeaders);
        String resultStr = REST_TEMPLATE.postForObject(url, requestEntity, String.class);
        log.info("HTTP POST请求返回数据: {}", resultStr);
        return JSON.parseObject(resultStr, returnClass);
    }

    /**
     * 发送GET请求
     */
    public static <T> T get(String url, Class<T> returnClass) {
        log.info("发送HTTP GET请求 url: {}", url);
        String resultStr = REST_TEMPLATE.getForObject(URI.create(url), String.class);
        log.info("HTTP GET请求返回数据: {}", resultStr);
        return JSON.parseObject(resultStr, returnClass);
    }

    /**
     * 发送GET JSON请求(携带鉴权)
     *
     * @param url            请求地址
     * @param data           请求数据
     * @param requestHeaders 请求头
     * @param returnClass    返回类型
     */
    public static <T> T get(String url, Object data, HttpHeaders requestHeaders, Class<T> returnClass) {
        log.info("发送HTTP get请求 url: {},data: {}", url, data);
        HttpEntity<Object> requestEntity = new HttpEntity<>(data, requestHeaders);
        ResponseEntity<T> exchange = REST_TEMPLATE.exchange(url, HttpMethod.GET, requestEntity, returnClass);
        log.info("HTTP GET请求返回数据: {}", exchange);
        return exchange.getBody();
    }


    /**
     * 发送post表单请求
     * <p>
     * 上传文件示例 {@code data.put("file", new FileSystemResource()));}
     */
    public static <T> T postFormData(String url, MultiValueMap<String, Object> data, Class<T> returnClass) {
        log.info("发送HTTP POST 表单请求 url: {} ,data: {}", url, data);
        String resultStr = REST_TEMPLATE.postForObject(url, data, String.class);
        log.info("HTTP POST表单请求返回数据: {}", resultStr);
        return JSON.parseObject(resultStr, returnClass);
    }

    /**
     * 发送post xml请求
     */
    public static String postXml(String url, String xmlString) {
        log.info("发送HTTP POST XML 请求 url: {} ,data: {}", url, xmlString);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_XML);
        String resultStr = REST_TEMPLATE.postForObject(url, new HttpEntity<>(xmlString, httpHeaders), String.class);
        log.info("HTTP POST XML返回数据: {}", resultStr);
        return resultStr;
    }

}
