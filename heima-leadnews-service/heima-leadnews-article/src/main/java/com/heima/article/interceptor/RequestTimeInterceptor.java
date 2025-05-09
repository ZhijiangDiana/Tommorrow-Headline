package com.heima.article.interceptor;

import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @Description
 * @Author 嘉然今天吃向晚
 * @Date 2025/3/15-14:06:36
 */
public class RequestTimeInterceptor implements HandlerInterceptor {

    // 请求开始时记录时间
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        long startTime = System.currentTimeMillis();
        request.setAttribute("startTime", startTime); // 存入 request 属性
        return true; // 继续执行请求
    }

    // 请求结束后计算耗时
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        long startTime = (Long) request.getAttribute("startTime"); // 获取开始时间
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime; // 计算耗时

        // 记录日志
        System.out.println("请求 [" + request.getMethod() + "] " + request.getRequestURI() + " 耗时：" + duration + "ms");
    }
}
