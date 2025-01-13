package com.heima.utils.net;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class IPUtils {

    // 判断 IP 地址是否属于局域网
    public static boolean isPrivateIP(String ip) {
        try {
            // 将 IP 地址解析为 InetAddress 对象
            InetAddress inetAddress = InetAddress.getByName(ip);

            // 获取 IP 地址的字节数组
            byte[] addressBytes = inetAddress.getAddress();

            // 判断是否属于局域网的三个范围

            // 10.0.0.0 - 10.255.255.255
            if (addressBytes[0] == (byte) 10) {
                return true;
            }
            // 172.16.0.0 - 172.31.255.255
            if (addressBytes[0] == (byte) 172 && addressBytes[1] >= (byte) 16 && addressBytes[1] <= (byte) 31) {
                return true;
            }
            // 192.168.0.0 - 192.168.255.255
            if (addressBytes[0] == (byte) 192 && addressBytes[1] == (byte) 168) {
                return true;
            }

            // 本机ip地址
            if (addressBytes[0] == (byte) 127) {
                return true;
            }

            // 如果都不匹配，则不是局域网地址
            return false;
        } catch (UnknownHostException e) {
            // 如果无法解析 IP 地址，返回 false
            return false;
        }
    }

    public static void main(String[] args) {
        // 测试 IP 地址
        String ip = "0.0.0.0";

        if (isPrivateIP(ip)) {
            System.out.println(ip + " 是局域网地址");
        } else {
            System.out.println(ip + " 不是局域网地址");
        }
    }
}
