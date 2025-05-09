package com.heima.user.config;

import com.heima.user.interceptor.ApTokenInterceptor;
import com.heima.user.interceptor.RequestTimeInterceptor;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

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
