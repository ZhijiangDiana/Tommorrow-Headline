package com.heima.common.aliyun;

import com.alibaba.fastjson.JSON;
import com.aliyun.green20220302.Client;
import com.aliyun.green20220302.models.*;
import com.aliyun.green20220302.models.ImageModerationResponseBody.ImageModerationResponseBodyData;
import com.aliyun.green20220302.models.ImageModerationResponseBody.ImageModerationResponseBodyDataResult;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.teaopenapi.models.Config;
import com.aliyun.teautil.models.RuntimeOptions;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "aliyun")
public class ImageModerationService {

    private String accessKeyId;
    private String secret;
    private final String endpoint = "green-cip.cn-shanghai.aliyuncs.com";

    //文件上传token endpoint->token
    private static Map<String, DescribeUploadTokenResponseBody.DescribeUploadTokenResponseBodyData> tokenMap = new HashMap<>();

    private static Config config;

    public ModerationResult imageModeration(byte[] byteFile) throws Exception {
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
        //注意，此处实例化的client请尽可能重复使用，避免重复建立连接，提升检测性能。
        if (config == null) {
            config = new Config();
            config.setAccessKeyId(accessKeyId);
            config.setAccessKeySecret(secret);
            // 接入区域和地址请根据实际情况修改
            config.setEndpoint(endpoint);
        }

        Client client = new Client(config);
        RuntimeOptions runtime = new RuntimeOptions();

        //获取文件上传token
        if (tokenMap.get(endpoint) == null || tokenMap.get(endpoint).expiration <= System.currentTimeMillis() / 1000) {
            DescribeUploadTokenResponse tokenResponse = client.describeUploadToken();
            tokenMap.put(endpoint,tokenResponse.getBody().getData());
        }
        //上传文件请求客户端
        DescribeUploadTokenResponseBody.DescribeUploadTokenResponseBodyData tokenData = tokenMap.get(endpoint);
        OSS ossClient = new OSSClientBuilder().build(tokenData.ossInternetEndPoint, tokenData.getAccessKeyId(), tokenData.getAccessKeySecret(), tokenData.getSecurityToken());

        //上传文件
        String objectName = tokenData.getFileNamePrefix() + UUID.randomUUID();
        ossClient.putObject(tokenData.getBucketName(), objectName, new ByteArrayInputStream(byteFile));

        // 检测参数构造。
        Map<String, String> serviceParameters = new HashMap<>();
        //文件上传信息
        serviceParameters.put("ossBucketName", tokenMap.get(endpoint).getBucketName());
        serviceParameters.put("ossObjectName", objectName);
        serviceParameters.put("dataId", UUID.randomUUID().toString());

        ImageModerationRequest request = new ImageModerationRequest();
        // 图片检测service：内容安全控制台图片增强版规则配置的serviceCode，示例：baselineCheck
        request.setService("baselineCheck");
        request.setServiceParameters(JSON.toJSONString(serviceParameters));

        ImageModerationResponse response = null;
        try {
            response = client.imageModerationWithOptions(request, runtime);
        } catch (Exception e) {
            e.printStackTrace();
        }

        ModerationResult res = null;
        try {
            // 打印检测结果。
            if (response != null) {
                if (response.getStatusCode() == 200) {
                    ImageModerationResponseBody body = response.getBody();
                    if (body.getCode() == 200) {
                        res = new ModerationResult();
                        ImageModerationResponseBodyData data = body.getData();
                        List<ImageModerationResponseBodyDataResult> results = data.getResult();
                        res.setRisk(data.getRiskLevel());
                        res.setReason(results.stream().map(x -> x.label).collect(Collectors.toList()));
                    } else {
                        System.out.println("image moderation not success. code:" + body.getCode());
                    }
                } else {
                    System.out.println("response not success. status:" + response.getStatusCode());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return res;
    }
}