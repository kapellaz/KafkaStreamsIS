package org.example;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.Properties;

public class ProducerSell {
    public static void main(String[] args) {
        // Create a Kafka Streams producer
        Properties props = new Properties();
        props.put("bootstrap.servers", "broker1:9092");
        props.put("retries", 0);
        props.put("batch.size", 16384); // 16 KB
        props.put("linger.ms", 1);
        props.put("buffer.memory", 33554432); // 32 MB
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");


        //send a string to the topic Buy
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("topic", "Buy");
        Producer<String, String> producer = new KafkaProducer<>(props);
        producer.send(new ProducerRecord<>(props.getProperty("topic"), "Hello World"));

    }
}