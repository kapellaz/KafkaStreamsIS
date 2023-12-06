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

public class All5_6_7 {
        public static void main(String[] args) {
                BasicConfigurator.configure();
                String topicName1 = "Buy";
                String topicName2 = "Sell";

                java.util.Properties props = new Properties();
                props.put(StreamsConfig.APPLICATION_ID_CONFIG, "exercises");
                props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "broker1:9092,broker2:9092,broker3:9092");
                props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
                props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, CustomSaleSerializer.class.getName());

                StreamsBuilder builder = new StreamsBuilder();
                KStream<String, Sale> lines = builder.stream(topicName1, Consumed.with(
                                Serdes.String(), new CustomSaleSerializer()));

                KStream<String, Sale> lines1 = builder.stream(topicName2, Consumed.with(
                                Serdes.String(), new CustomSaleSerializer()));

                // req5 get renenue per sock
                KTable<String, Double> out = lines
                                .groupBy((key, value) -> value.getType())
                                .aggregate(
                                                () -> 0.0,
                                                (aggKey, newValue, aggValue) -> aggValue
                                                                + (newValue.getPricePerPair() * newValue.getQuantity()),
                                                Materialized.with(Serdes.String(), Serdes.Double()));

                out.mapValues((k, v) -> {
                        String a = "{\"schema\":{\"type\":\"struct\",\"fields\":" +
                                        "[{\"type\":\"string\",\"optional\":false,\"field\":\"id\"}," +
                                        "{\"type\":\"double\",\"optional\":false,\"field\":\"revenue\"}" +
                                        "]}," +
                                        "\"payload\":{\"id\":" + "\"" + k + "\"" + ",\"revenue\":" + v + "}}";
                        System.out.println(a);
                        return a;
                }).toStream().to("REQ5", Produced.with(Serdes.String(), Serdes.String()));

                out.toStream().foreach((key, value) -> System.out.println("Buy: " + key + " Revenue: " + value));

                // req6
                // get the expenses of one pair of socks
                KTable<String, Double> out1 = lines1
                                .groupBy((key, value) -> value.getType())
                                .aggregate(
                                                () -> 0.0,
                                                (aggKey, newValue, aggValue) -> aggValue
                                                                + (newValue.getPricePerPair() * newValue.getQuantity()),
                                                Materialized.with(Serdes.String(), Serdes.Double()));

                // out1.toStream().to("req6", Produced.with(Serdes.Integer(), Serdes.Double()));
                out1.mapValues((k, v) -> {
                        String a = "{\"schema\":{\"type\":\"struct\",\"fields\":" +
                                        "[{\"type\":\"string\",\"optional\":false,\"field\":\"id\"}," +
                                        "{\"type\":\"double\",\"optional\":false,\"field\":\"expenses\"}" +
                                        "]}," +
                                        "\"payload\":{\"id\":" + "\"" + k + "\"" + ",\"expenses\":" + v + "}}";
                        System.out.println(a);
                        return a;
                }).toStream().to("REQ6", Produced.with(Serdes.String(), Serdes.String()));

                out1.toStream().foreach((key, value) -> System.out.println("Sell: " + key + " expenses: " + value));
                // req7 get profit per sock
                // Calculate profit per sock by joining revenue and expenses
                KTable<String, Double> profitPerSock = out
                                .join(out1, (revenueValue, expenseValue) -> revenueValue - expenseValue);

                profitPerSock.mapValues((k, v) -> {
                        String a = "{\"schema\":{\"type\":\"struct\",\"fields\":" +
                                        "[{\"type\":\"string\",\"optional\":false,\"field\":\"id\"}," +
                                        "{\"type\":\"double\",\"optional\":false,\"field\":\"profit\"}" +
                                        "]}," +
                                        "\"payload\":{\"id\":" + "\"" + k + "\"" + ",\"profit\":" + v + "}}";
                        System.out.println(a);
                        return a;
                }).toStream().to("REQ7", Produced.with(Serdes.String(), Serdes.String()));
                profitPerSock.toStream()
                                .foreach((key, value) -> System.out.println("Buy: " + key + " Profit: " + value));

                KafkaStreams streams = new KafkaStreams(builder.build(), props);
                System.out.println("a ler");
                streams.start();

        }
}
