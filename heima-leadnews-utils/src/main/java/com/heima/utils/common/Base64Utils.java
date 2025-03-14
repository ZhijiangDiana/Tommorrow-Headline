package com.heima.utils.common;

//import sun.misc.BASE64Decoder;
//import sun.misc.BASE64Encoder;
//
//public class Base64Utils {
//
//    /**
//     * 解码
//     * @param base64
//     * @return
//     */
//    public static byte[] decode(String base64){
//        BASE64Decoder decoder = new BASE64Decoder();
//        try {
//            // Base64解码
//            byte[] b = decoder.decodeBuffer(base64);
//            for (int i = 0; i < b.length; ++i) {
//                if (b[i] < 0) {// 调整异常数据
//                    b[i] += 256;
//                }
//            }
//            return b;
//        } catch (Exception e) {
//            return null;
//        }
//    }
//
//
//    /**
//     * 编码
//     * @param data
//     * @return
//     * @throws Exception
//     */
//    public static String encode(byte[] data) {
//        BASE64Encoder encoder = new BASE64Encoder();
//        return encoder.encode(data);
//    }
//}

import java.util.Base64;

public class Base64Utils {

    /**
     * 解码
     * @param base64
     * @return 解码后的字节数组
     */
    public static byte[] decode(String base64) {
        try {
            // 使用标准库的 Base64 解码
            return Base64.getDecoder().decode(base64);
        } catch (IllegalArgumentException e) {
            // 解码异常处理
            return null;
        }
    }

    /**
     * 编码
     * @param data 要编码的字节数组
     * @return Base64 编码后的字符串
     */
    public static String encode(byte[] data) {
        // 使用标准库的 Base64 编码
        return Base64.getEncoder().encodeToString(data);
    }
}
