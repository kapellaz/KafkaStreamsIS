package org.example;

import org.apache.kafka.streams.kstream.*;

import org.apache.log4j.BasicConfigurator;
import java.util.Properties;
import java.time.Duration;

import org.apache.kafka.common.serialization.Serdes;

import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;

import org.example.Serializer.CustomSaleSerializer;
import org.example.Serializer.Sale;

public class Stream10 {
        public static void main(String[] args) {
                BasicConfigurator.configure();
                String topicName = "Buy";

                Properties props = new Properties();
                props.put(StreamsConfig.APPLICATION_ID_CONFIG, "exercises-application10");
                props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "broker1:9092,broker2:9092,broker3:9092");
                props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
                props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, CustomSaleSerializer.class);

                StreamsBuilder builder = new StreamsBuilder();
                KStream<String, Sale> lines = builder.stream(topicName, Consumed.with(
                                Serdes.String(),
                                new CustomSaleSerializer()));

                lines.foreach((key, value) -> System.out.println("key: " + key + " Value: " + value));

                // Get the total revenue in the last hour using a tumbling time window.

                KTable<Windowed<String>, Double> out = lines
                                .groupByKey()
                                .windowedBy(TimeWindows.of(Duration.ofSeconds(30)))
                                .aggregate(
                                                () -> 0.0,
                                                (aggKey, newValue, aggValue) -> aggValue
                                                                + (newValue.getPricePerPair() * newValue.getQuantity()),
                                                Materialized.with(Serdes.String(), Serdes.Double()));

                out.mapValues((k, v) -> {
                        String a = "{\"schema\":{\"type\":\"struct\",\"fields\":" +
                                        "[{\"type\":\"string\",\"optional\":false,\"field\":\"id\"}," +
                                        "{\"type\":\"double\",\"optional\":false,\"field\":\"LastRevenues\"}"
                                        +
                                        "]},"
                                        +
                                        "\"payload\":{\"id\":" + "\"" + "Rev" + "\""
                                        + ",\"LastRevenues\":" + v + "}}";
                        System.out.println(a);
                        return a;
                }).toStream().to("REQ14",
                                Produced.with(WindowedSerdes.timeWindowedSerdeFrom(String.class), Serdes.String()));

                KafkaStreams streams = new KafkaStreams(builder.build(), props);
                streams.start();

                System.out.println("Reading stream from topic " + topicName);
        }
}
