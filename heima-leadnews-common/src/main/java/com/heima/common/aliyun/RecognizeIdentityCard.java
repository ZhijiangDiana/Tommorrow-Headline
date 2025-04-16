package com.heima.common.aliyun;

import com.alibaba.fastjson.JSON;
import com.aliyun.ocr20191230.Client;
import com.aliyun.ocr20191230.models.RecognizeIdentityCardAdvanceRequest;
import com.aliyun.ocr20191230.models.RecognizeIdentityCardResponse;
import com.aliyun.ocr20191230.models.RecognizeIdentityCardResponseBody;
import com.aliyun.tea.TeaException;
import com.aliyun.tea.TeaModel;
import com.aliyun.teaopenapi.models.Config;
import com.aliyun.teautil.Common;
import com.aliyun.teautil.models.RuntimeOptions;
import com.heima.model.user.vos.IdCardTextVO;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.net.URL;

@Slf4j
@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "aliyun")
public class RecognizeIdentityCard {

    private String accessKeyId;
    private String secret;
    private final String endpoint = "ocr.cn-shanghai.aliyuncs.com";

    private Client createClient(String accessKeyId, String accessKeySecret) throws Exception {
        /*
          初始化配置对象com.aliyun.teaopenapi.models.Config
          Config对象存放 AccessKeyId、AccessKeySecret、endpoint等配置
         */
        Config config = new Config()
                .setAccessKeyId(accessKeyId)
                .setAccessKeySecret(accessKeySecret);
        // 访问的域名
        config.endpoint = endpoint;
        return new Client(config);
    }

    public IdCardTextVO recoIdCard(String cardUrl) throws Exception {
        Client client = createClient(accessKeyId, secret);

        URL url = new URL(cardUrl);
        InputStream inputStream = url.openConnection().getInputStream();
        RecognizeIdentityCardAdvanceRequest recognizeIdentityCardAdvanceRequest = new RecognizeIdentityCardAdvanceRequest()
                .setImageURLObject(inputStream)
                .setSide("back");
        RuntimeOptions runtime = new RuntimeOptions();
        IdCardTextVO idCardTextVO = new IdCardTextVO();
        try {
            RecognizeIdentityCardResponse recognizeIdentityCardResponse = client.recognizeIdentityCardAdvance(recognizeIdentityCardAdvanceRequest, runtime);
            RecognizeIdentityCardResponseBody
                    .RecognizeIdentityCardResponseBodyDataFrontResult frontResult =
                    recognizeIdentityCardResponse.getBody().getData().getFrontResult();
            if (frontResult == null)
                return idCardTextVO;
            idCardTextVO.setName(frontResult.getName());
            idCardTextVO.setIdCardNumber(frontResult.getIDNumber());
        } catch (TeaException teaException) {
            // 获取整体报错信息。
            teaException.printStackTrace();
            log.error(Common.toJSONString(teaException));
            // 获取单个字段。
            log.error(teaException.getCode());
        }

//        System.out.println(JSON.toJSONString(idCardTextVO));
        return idCardTextVO;
    }
}