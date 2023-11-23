package org.example;

import java.util.Properties;

public class KafkaConsumer {
    public static void main(String[] args) {
        // Create a Kafka Streams producer
        Properties props = new Properties();
        props.put("bootstrap.servers", "localhost:9092");
        props.put("acks", "all"); // This is dangerous in production
        props.put("retries", 0);
        props.put("batch.size", 16384); // 16 KB
        props.put("linger.ms", 1);
        props.put("buffer.memory", 33554432); // 32 MB
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringSerializer");

    }
}
