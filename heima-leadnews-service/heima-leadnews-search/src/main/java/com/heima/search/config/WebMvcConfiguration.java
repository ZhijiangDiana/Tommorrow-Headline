package com.heima.search.config;

import com.heima.search.filter.ApTokenInterceptor;
import com.heima.search.filter.RequestTimeInterceptor;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @Description
 * @Author 嘉然今天吃向晚
 * @Date 2024/12/26-20:39:43
 */
@Configuration
@ComponentScan("com.heima.apis")
public class WebMvcConfiguration implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new ApTokenInterceptor())
                .addPathPatterns("/**");
        registry.addInterceptor(new RequestTimeInterceptor())
                .addPathPatterns("/**");
    }
}
