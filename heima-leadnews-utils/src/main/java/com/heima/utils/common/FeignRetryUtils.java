package com.heima.utils.common;

import com.heima.model.common.dtos.ResponseResult;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Callable;

@Slf4j
public class FeignRetryUtils {

    private FeignRetryUtils() {}

    private static final Integer MAX_RETRIES = 3;

    private static final Integer RETRY_INTERVAL_MILLISECOND = 1000;

    public static ResponseResult executeWithRetry(Callable<ResponseResult> method) {
        return executeWithRetry(method, MAX_RETRIES);
    }

    public static ResponseResult executeWithRetry(Callable<ResponseResult> method, int maxRetries) {
        ResponseResult result = null;
        for (int i = 0; i < maxRetries; i++) {
            try {
                result = method.call();  // 执行RPC调用
                if (result != null && result.getCode().equals(200)) {
                    return result;  // 如果成功，返回结果
                } else {
                    throw new RuntimeException();
                }
            } catch (Exception e) {
                if (result != null)
                    log.error("RPC调用失败，重试中... 错误码{}, 错误信息: {}", result.getCode(), result.getErrorMessage());
                e.printStackTrace();
                // 可以在这里设置重试间隔，例如：
                try {
                    Thread.sleep(RETRY_INTERVAL_MILLISECOND); // 睡眠1秒后重试
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
        }
        throw new RuntimeException("RPC调用失败");
    }

    public static void main(String[] args) {
        try {
            ResponseResult result = FeignRetryUtils.executeWithRetry(new Callable<ResponseResult>() {
                @Override
                public ResponseResult call() throws Exception {
                    // 模拟调用一个RPC接口
                    return mockRpcCall();
                }
            });

            System.out.println("RPC调用成功: " + result.getCode());
        } catch (Exception e) {
            System.out.println("RPC调用失败: " + e.getMessage());
        }
    }

    // 模拟的RPC调用方法
    private static ResponseResult mockRpcCall() {
        // 假设在这里判断是否成功调用RPC
        boolean success = Math.random() > 0.5; // 随机成功与失败
        if (success) {
            return ResponseResult.okResult(114514);
        } else {
            return ResponseResult.errorResult(500, "失败，尝试再次调用");
        }
    }
}
