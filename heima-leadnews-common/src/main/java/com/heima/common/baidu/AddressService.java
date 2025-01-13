package com.heima.common.baidu;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.heima.model.admin.dtos.AddressDto;
import com.heima.utils.string.JsonUtils;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.io.IOUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriUtils;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "baidu")
public class AddressService {

    private final String url = "https://api.map.baidu.com/location/ip?";

    private String ak;

//    public static void main(String[] args) throws Exception {
//
//        SearchHttpAK snCal = new SearchHttpAK();
//
//        snCal.getAdressByIP(url, "114.114.114.114");
//    }

    /**
     * 默认ak
     * 选择了ak，使用IP白名单校验：
     * 根据您选择的AK已为您生成调用代码
     * 检测到您当前的ak设置了IP白名单校验
     * 您的IP白名单中的IP非公网IP，请设置为公网IP，否则将请求失败
     * 请在IP地址为的计算发起请求，否则将请求失败
     */
    public AddressDto getAddressByIP(String ip) throws IOException {
        Map<String, String> params = new LinkedHashMap<>();
        params.put("ip", ip);
        params.put("coor", "bd09ll");
        params.put("ak", ak);

        StringBuffer queryString = new StringBuffer();
        queryString.append(url);
        for (Map.Entry<?, ?> pair : params.entrySet()) {
            queryString.append(pair.getKey() + "=");
            //    第一种方式使用的 jdk 自带的转码方式  第二种方式使用的 spring 的转码方法 两种均可
            //    queryString.append(URLEncoder.encode((String) pair.getValue(), "UTF-8").replace("+", "%20") + "&");
            queryString.append(UriUtils.encode((String) pair.getValue(), "UTF-8") + "&");
        }

        if (queryString.length() > 0) {
            queryString.deleteCharAt(queryString.length() - 1);
        }

        java.net.URL url = new URL(queryString.toString());
//        System.out.println(queryString);
        URLConnection httpConnection = url.openConnection();
        httpConnection.setRequestProperty("Content-type", "text/html");
        httpConnection.setRequestProperty("Accept-Charset", "UTF-8");
        httpConnection.setRequestProperty("contentType", "UTF-8");

        httpConnection.connect();
        String resp = IOUtils.toString(httpConnection.getInputStream(), StandardCharsets.UTF_8);
        resp = JsonUtils.decodeUnicode(resp);
        IOUtils.close(httpConnection);

//        System.out.println("AK: " + resp);
        // 组装dto
        AddressDto addressDto = new AddressDto();
        JSONObject jsonObject = JSON.parseObject(resp);
        JSONObject content = jsonObject.getJSONObject("content");
        addressDto.setAddress(content.getString("address"));
        JSONObject point = content.getJSONObject("point");
        addressDto.setX(point.getDouble("x"));
        addressDto.setY(point.getDouble("y"));

        return addressDto;
    }
}