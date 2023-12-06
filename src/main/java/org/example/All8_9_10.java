package org.example;

import org.apache.kafka.streams.kstream.*;
import org.apache.log4j.BasicConfigurator;
import java.util.Properties;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.example.Serializer.CustomSaleSerializer;
import org.example.Serializer.Sale;

public class All8_9_10 {
        public static void main(String[] args) {
                BasicConfigurator.configure();
                String topicName1 = "Buy";
                String topicName2 = "Sell";

                java.util.Properties props = new Properties();
                props.put(StreamsConfig.APPLICATION_ID_CONFIG, "exercises-application4");
                props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "broker1:9092,broker2:9092,broker3:9092");
                props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
                props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, CustomSaleSerializer.class);

                StreamsBuilder builder = new StreamsBuilder();
                KStream<String, Sale> lines = builder.stream(topicName1, Consumed.with(
                                Serdes.String(), new CustomSaleSerializer()));

                KStream<String, Sale> lines1 = builder.stream(topicName2, Consumed.with(
                                Serdes.String(), new CustomSaleSerializer()));
                // get total revenue
                KTable<String, Double> out = lines
                                .map((key, value) -> KeyValue.pair("key", value))
                                .groupByKey()
                                .aggregate(
                                                () -> 0.0,
                                                (aggKey, newValue, aggValue) -> aggValue
                                                                + (newValue.getPricePerPair() * newValue.getQuantity()),
                                                Materialized.with(Serdes.String(), Serdes.Double()));

                out.mapValues((k, v) -> {
                        String a = "{\"schema\":{\"type\":\"struct\",\"fields\":" +
                                        "[{\"type\":\"string\",\"optional\":false,\"field\":\"id\"}," +
                                        "{\"type\":\"double\",\"optional\":false,\"field\":\"TotalRevenue\"}" +
                                        "]}," +
                                        "\"payload\":{\"id\":\"" + k + "\",\"TotalRevenue\":" + v + "}}";
                        System.out.println(a);
                        return a;
                }).toStream().to("REQ8", Produced.with(Serdes.String(), Serdes.String()));

                // print the result and the latest results
                out.toStream().foreach((key, value) -> System.out.println(key + " Total Revenue: " + value));

                // get the revenue of one pair of socks
                KTable<String, Double> out1 = lines1
                                .map((key, value) -> KeyValue.pair("key", value))
                                .groupByKey()
                                .aggregate(
                                                () -> 0.0,
                                                (aggKey, newValue, aggValue) -> aggValue
                                                                + (newValue.getPricePerPair() * newValue.getQuantity()),
                                                Materialized.with(Serdes.String(), Serdes.Double()));

                out1.mapValues((k, v) -> {
                        String a = "{\"schema\":{\"type\":\"struct\",\"fields\":" +
                                        "[{\"type\":\"string\",\"optional\":false,\"field\":\"id\"}," +
                                        "{\"type\":\"double\",\"optional\":false,\"field\":\"TotalExpenses\"}" +
                                        "]}," +
                                        "\"payload\":{\"id\":\"" + k + "\",\"TotalExpenses\":" + v + "}}";
                        System.out.println(a);
                        return a;
                }).toStream().to("REQ9", Produced.with(Serdes.String(), Serdes.String()));
                // print the result
                out1.toStream().foreach((key, value) -> System.out.println(key + " Total Expenses: " + value));

                KTable<String, Double> profitPerSock = out
                                .join(out1, (revenueValue, expenseValue) -> revenueValue - expenseValue);

                profitPerSock.toStream().to("req10", Produced.with(Serdes.String(), Serdes.Double()));

                profitPerSock.mapValues((k, v) -> {
                        String a = "{\"schema\":{\"type\":\"struct\",\"fields\":" +
                                        "[{\"type\":\"string\",\"optional\":false,\"field\":\"id\"}," +
                                        "{\"type\":\"double\",\"optional\":false,\"field\":\"TotalProfit\"}" +
                                        "]}," +
                                        "\"payload\":{\"id\":\"" + k + "\",\"TotalProfit\":" + v + "}}";
                        System.out.println(a);
                        return a;
                }).toStream().to("REQ10", Produced.with(Serdes.String(), Serdes.String()));

                profitPerSock.toStream().foreach((key, value) -> System.out.println(key + " Profit: " + value));

                KafkaStreams streams = new KafkaStreams(builder.build(), props);
                streams.start();

                System.out.println("Reading stream from topic " + topicName1 + " and " + topicName2);

        }
}
