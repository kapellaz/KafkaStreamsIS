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

public class Stream3 {
        public static void main(String[] args) {
                BasicConfigurator.configure();
                String topicName = "Buy";

                Properties props = new Properties();
                props.put(StreamsConfig.APPLICATION_ID_CONFIG, "exercises-application3");
                props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "broker1:9092,broker2:9092,broker3:9092");
                props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.Integer().getClass());
                props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, CustomSaleSerializer.class);

                StreamsBuilder builder = new StreamsBuilder();
                KStream<String, Sale> lines = builder.stream(topicName, Consumed.with(
                                Serdes.String(),
                                new CustomSaleSerializer()));
                // get the profit of one pair of socks
                KTable<Integer, Double> out = lines
                                .groupBy((key, value) -> value.getId())
                                .aggregate(
                                                () -> 0.0,
                                                (aggKey, newValue,
                                                                aggValue) -> aggValue + (newValue.getPricePerPair()
                                                                                * (0.5) * newValue.getQuantity()),
                                                Materialized.with(Serdes.Integer(), Serdes.Double()));

                // print the result
                out.toStream().foreach((key, value) -> System.out.println("Buy: " + key + "Profit: " + value));
                out.mapValues((k, v) -> {

                        String a = "{\"schema\":{\"type\":\"struct\",\"fields\":" +
                                        "[{\"type\":\"string\",\"optional\":false,\"field\":\"id\"}," +
                                        "{\"type\":\"double\",\"optional\":false,\"field\":\"Pair\"},"
                                        +
                                        "{\"type\":\"double\",\"optional\":false,\"field\":\"Profit\"}"
                                        +
                                        "]}," +
                                        "\"payload\":{\"Pair\":\"" + k
                                        + "\",\"Profit\":" + v + "}}";
                        System.out.println(a);
                        return a;
                }).toStream().to("REQ7", Produced.with(Serdes.Integer(), Serdes.String()));

                KafkaStreams streams = new KafkaStreams(builder.build(), props);
                streams.start();

                System.out.println("Reading stream from topic " + topicName);

        }
}
