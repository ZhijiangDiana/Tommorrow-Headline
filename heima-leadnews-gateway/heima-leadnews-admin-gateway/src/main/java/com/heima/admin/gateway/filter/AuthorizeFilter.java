package com.heima.admin.gateway.filter;

import com.heima.admin.gateway.util.AppJwtUtil;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class AuthorizeFilter implements Ordered, GlobalFilter {


    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 1. Request和Response对象
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();

        // 2. 判断是否是登录
        if (request.getURI().getPath().contains("/login"))
            return chain.filter(exchange);

        // 3. 获取token
        String token = request.getHeaders().getFirst("token");

        // 4. 判断token是否存在
        if (StringUtils.isBlank(token)) {
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return response.setComplete();
        }
        // 5. 判断token是否有效
        try {
            Claims claimsBody = AppJwtUtil.getClaimsBody(token);
            int res = AppJwtUtil.verifyToken(claimsBody);
            if (res == 1 || res == 2) {
                response.setStatusCode(HttpStatus.UNAUTHORIZED);
                return response.setComplete();
            }
            // 获取用户信息
            Integer id = (Integer) claimsBody.get("id");
            // 存入header中
            ServerHttpRequest newReq = request.mutate().headers(httpHeaders -> {
                httpHeaders.add("userId", id.toString());
            }).build();
            // 重置请求
            exchange.mutate().request(newReq);
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return response.setComplete();
        }

        // 6.放行
        return chain.filter(exchange);
    }

    /**
     * 优先级设置 值越小 优先级越高
     * @return
     */
    @Override
    public int getOrder() {
        return 0;
    }
}
