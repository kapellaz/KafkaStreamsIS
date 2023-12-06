package org.example;

import org.apache.kafka.streams.kstream.*;
import org.apache.log4j.BasicConfigurator;
import java.util.Properties;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.example.Serializer.CustomSaleSerializer;
import org.example.Serializer.Sale;

public class Stream6 {
        public static void main(String[] args) {
                BasicConfigurator.configure();
                String topicName = "Buy";

                Properties props = new Properties();
                props.put(StreamsConfig.APPLICATION_ID_CONFIG, "exercises-application6");
                props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "broker1:9092,broker2:9092,broker3:9092");
                props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
                props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, CustomSaleSerializer.class);

                StreamsBuilder builder = new StreamsBuilder();
                KStream<String, Sale> lines = builder.stream(topicName, Consumed.with(
                                Serdes.String(),
                                new CustomSaleSerializer()));
                // get the profit of one pair of socks
                KTable<String, Double> out = lines
                                .groupByKey()
                                .aggregate(
                                                () -> 0.0,
                                                (aggKey, newValue,
                                                                aggValue) -> aggValue + (newValue.getPricePerPair()
                                                                                * (0.5) * newValue.getQuantity()),
                                                Materialized.with(Serdes.String(), Serdes.Double()));
                out.mapValues((k, v) -> {

                        String a = "{\"schema\":{\"type\":\"struct\",\"fields\":" +
                                        "[{\"type\":\"string\",\"optional\":false,\"field\":\"id\"}," +
                                        "{\"type\":\"double\",\"optional\":false,\"field\":\"name\"},"
                                        +
                                        "{\"type\":\"double\",\"optional\":false,\"field\":\"Profit\"}"
                                        +
                                        "]}," +
                                        "\"payload\":{\"name\":\"" + k
                                        + "\",\"Profit\":" + v + "}}";
                        System.out.println(a);
                        return a;
                }).toStream().to("REQ10", Produced.with(Serdes.String(), Serdes.String()));

               

                // print the result
                out.toStream().foreach((key, value) -> System.out.println("Buy: " + key + "Profit: " + value));

                KafkaStreams streams = new KafkaStreams(builder.build(), props);
                streams.start();

                System.out.println("Reading stream from topic " + topicName);

        }
}
