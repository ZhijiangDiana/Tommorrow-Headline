package com.heima.admin.config;

import com.heima.admin.interceptor.AdTokenInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @Description
 * @Author 嘉然今天吃向晚
 * @Date 2024/12/26-20:39:43
 */
@Configuration
public class WebMvcConfiguration implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new AdTokenInterceptor())
                .addPathPatterns("/**");
    }
}
