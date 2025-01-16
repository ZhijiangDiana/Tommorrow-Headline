package com.heima.article.interceptor;

import com.heima.utils.thread.ThreadLocalUtil;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @Description
 * @Author 嘉然今天吃向晚
 * @Date 2024/12/26-20:11:34
 */
public class TokenInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String userId = request.getHeader("userId");
        if (userId != null) {
            ThreadLocalUtil.setUserId(Integer.parseInt(userId));
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {
        ThreadLocalUtil.rmUserId();
    }
}
