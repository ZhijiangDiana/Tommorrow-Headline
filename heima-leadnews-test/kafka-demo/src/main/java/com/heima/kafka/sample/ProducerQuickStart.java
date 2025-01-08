package com.heima.kafka.sample;

import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;

public class ProducerQuickStart {

    public static void main(String[] args) {

        // 1.kafka连接配置
        Properties prop = new Properties();
        prop.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "192.168.1.210:9092");
        prop.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        prop.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        // ack机制，默认是仅需leader节点收到消息即返回
        prop.put(ProducerConfig.ACKS_CONFIG, "1");
        // 重试次数
        prop.put(ProducerConfig.RETRIES_CONFIG, 10);
        // 压缩类型
        prop.put(ProducerConfig.COMPRESSION_TYPE_CONFIG, "lz4");

        // 2.创建kafka生产者对象
        KafkaProducer<String, String> producer = new KafkaProducer<>(prop);

        // 3.异步发送消息
        ProducerRecord<String, String> record = new ProducerRecord<>("topic-first", "key-001", "114514");
        producer.send(record, new Callback() {
            @Override
            public void onCompletion(RecordMetadata recordMetadata, Exception e) {
                if (e != null) {
                    e.printStackTrace();
                }
            }
        });

        // 4.关闭消息通道，必须关闭，否则消息发送不成功
        producer.close();
    }
}
