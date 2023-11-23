package org.example;

import java.util.Properties;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.log4j.Logger;
import org.apache.log4j.BasicConfigurator;
public class KafkaStream {
    public static void main(String[] args) {
        // Create a Kafka Streams producer
        Properties config = new Properties();
        config.put(StreamsConfig.APPLICATION_ID_CONFIG, "my-first-streams-application");
        config.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        config.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
    }
}
