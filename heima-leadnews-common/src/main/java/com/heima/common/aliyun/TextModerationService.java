package com.heima.common.aliyun;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.green20220302.Client;
import com.aliyun.green20220302.models.TextModerationPlusRequest;
import com.aliyun.green20220302.models.TextModerationPlusResponse;
import com.aliyun.green20220302.models.TextModerationPlusResponseBody;
import com.aliyun.teaopenapi.models.Config;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @Description
 * @Author 嘉然今天吃向晚
 * @Date 2024/12/28-20:13:30
 */
@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "aliyun")
@Slf4j
public class TextModerationService {

    private String accessKeyId;
    private String secret;

    private static Config config;
    private void initialize() {
        if (config == null) {
            config = new Config();
            config.setAccessKeyId(accessKeyId);
            config.setAccessKeySecret(secret);
            //接入区域和地址请根据实际情况修改
            config.setRegionId("cn-shanghai");
            config.setEndpoint("green-cip.cn-shanghai.aliyuncs.com");
            //读取时超时时间，单位毫秒（ms）。
            config.setReadTimeout(6000);
            //连接时超时时间，单位毫秒（ms）。
            config.setConnectTimeout(3000);
            //设置http代理。
            //config.setHttpProxy("http://xx.xx.xx.xx:xxxx");
            //设置https代理。
            //config.setHttpsProxy("https://xx.xx.xx.xx:xxxx");
        }
    }
    public ModerationResult textJudge(String content) throws Exception {
        initialize();
        /**
         * 阿里云账号AccessKey拥有所有API的访问权限，建议您使用RAM用户进行API访问或日常运维。
         * 常见获取环境变量方式：
         * 方式一：
         *     获取RAM用户AccessKey ID：System.getenv("ALIBABA_CLOUD_ACCESS_KEY_ID");
         *     获取RAM用户AccessKey Secret：System.getenv("ALIBABA_CLOUD_ACCESS_KEY_SECRET");
         * 方式二：
         *     获取RAM用户AccessKey ID：System.getProperty("ALIBABA_CLOUD_ACCESS_KEY_ID");
         *     获取RAM用户AccessKey Secret：System.getProperty("ALIBABA_CLOUD_ACCESS_KEY_SECRET");
         */
        Client client = new Client(config);

        JSONObject serviceParameters = new JSONObject();
        serviceParameters.put("content", content);
        TextModerationPlusRequest textModerationPlusRequest = new TextModerationPlusRequest();
        // 检测类型
        textModerationPlusRequest.setService("llm_query_moderation");
        textModerationPlusRequest.setServiceParameters(serviceParameters.toJSONString());

        ModerationResult res = null;  // 审核结果
        try {
            TextModerationPlusResponse response = client.textModerationPlus(textModerationPlusRequest);
            if (response.getStatusCode().equals(200)) {
                TextModerationPlusResponseBody result = response.getBody();

                Integer code = result.getCode();
                if (code.equals(200)) {
                    TextModerationPlusResponseBody.TextModerationPlusResponseBodyData data = result.getData();
                    res = new ModerationResult();
                    if (data.getRiskLevel() != null) {
                        res.setRisk(data.getRiskLevel());
                    } else {
                        // 处理阿里云riskLevel字段缺失的bug
                        for (TextModerationPlusResponseBody.TextModerationPlusResponseBodyDataResult
                                x : data.getResult()) {
                            if (!x.label.equals("nonLabel") && x.confidence >= 80) {
                                res.setRisk(ModerationResult.HIGH_RISK);
                                break;
                            }
                        }
                    }
                    if (data.result != null)
                        res.setReason(data.result.stream()
                                .filter(x -> x.confidence != null && x.confidence >= 80)
                                .map(x -> x.label).collect(Collectors.toList()));
                } else {
                    log.error("text moderation not success. code: {}", code);
                }
            } else {
                log.error("response not success. status: {}", response.getStatusCode());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return res;
    }
}
