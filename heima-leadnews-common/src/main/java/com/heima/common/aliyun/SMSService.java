package com.heima.common.aliyun;

import com.aliyun.teaopenapi.Client;
import com.aliyun.teaopenapi.models.Config;
import com.aliyun.teaopenapi.models.OpenApiRequest;
import com.aliyun.teaopenapi.models.Params;
import com.aliyun.teautil.models.RuntimeOptions;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


/**
 * @Description
 * @Author 嘉然今天吃向晚
 * @Date 2025/3/18-14:34:08
 */
@Slf4j
@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "aliyun")
public class SMSService {

    private String accessKeyId;
    private String secret;

    private Client createClient() throws Exception {
        // 工程代码泄露可能会导致 AccessKey 泄露，并威胁账号下所有资源的安全性。以下代码示例仅供参考。
        // 建议使用更安全的 STS 方式，更多鉴权访问方式请参见：https://help.aliyun.com/document_detail/378657.html。
        Config config = new Config()
                // 必填，请确保代码运行环境设置了环境变量 ALIBABA_CLOUD_ACCESS_KEY_ID。
                .setAccessKeyId(accessKeyId)
                // 必填，请确保代码运行环境设置了环境变量 ALIBABA_CLOUD_ACCESS_KEY_SECRET。
                .setAccessKeySecret(secret);
        // Endpoint 请参考 https://api.aliyun.com/product/Dysmsapi
        config.endpoint = "dysmsapi.aliyuncs.com";
        return new Client(config);
    }

    private Params createApiInfo() {
        Params params = new Params()
                // 接口名称
                .setAction("SendSms")
                // 接口版本
                .setVersion("2017-05-25")
                // 接口协议
                .setProtocol("HTTPS")
                // 接口 HTTP 方法
                .setMethod("POST")
                .setAuthType("AK")
                .setStyle("RPC")
                // 接口 PATH
                .setPathname("/")
                // 接口请求体内容格式
                .setReqBodyType("json")
                // 接口响应体内容格式
                .setBodyType("json");
        return params;
    }

    public void sendVerifyCode(String phone, String code) throws Exception {
        Client client = createClient();
        Params params = createApiInfo();
        // query params
        Map<String, Object> queries = new HashMap<>();
        queries.put("PhoneNumbers", phone);
        queries.put("SignName", "阿里云短信测试");
        queries.put("TemplateCode", "SMS_154950909");
        queries.put("TemplateParam", "{\"code\":\"" + code + "\"}");
        // runtime options
        RuntimeOptions runtime = new RuntimeOptions();
        OpenApiRequest request = new OpenApiRequest()
                .setQuery(com.aliyun.openapiutil.Client.query(queries));
        // 复制代码运行请自行打印 API 的返回值
        // 返回值实际为 Map 类型，可从 Map 中获得三类数据：响应体 body、响应头 headers、HTTP 返回的状态码 statusCode。
        Map<String, ?> resp = client.callApi(params, request, runtime);
        Map<String, String> body = (Map<String, String>) resp.get("body");
        if (!resp.get("statusCode").equals(200)) {
            throw new IOException(resp.toString());
        }
        if (!body.get("Code").equals("OK")) {
            throw new IOException(body.get("Message"));
        }
    }
}
