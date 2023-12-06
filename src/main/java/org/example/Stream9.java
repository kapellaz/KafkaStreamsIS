package org.example;

import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.kstream.*;
import org.apache.kafka.streams.state.KeyValueStore;
import org.apache.log4j.BasicConfigurator;
import java.util.Properties;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;

import org.example.Serializer.CustomSaleSerializer;
import org.example.Serializer.ProfitTracker;
import org.example.Serializer.Sale;

public class Stream9 {
        public static void main(String[] args) {
                BasicConfigurator.configure();
                String topicName1 = "Buy";
                String topicName2 = "Sell";
                ProfitTracker prof = new ProfitTracker("Stream9Profit.txt");
                Properties props = new Properties();
                props.put(StreamsConfig.APPLICATION_ID_CONFIG, "exercises-application9");
                props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "broker1:9092");
                props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
                props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, CustomSaleSerializer.class);

                StreamsBuilder builder = new StreamsBuilder();
                KStream<String, Sale> lines = builder.stream(topicName1, Consumed.with(
                                Serdes.String(),
                                new CustomSaleSerializer()));

                KStream<String, Sale> lines1 = builder.stream(topicName2, Consumed.with(
                                Serdes.String(), new CustomSaleSerializer()));
                lines.foreach((key, value) -> System.out.println("key: " + key + " Value: " + value));

                // Get the sock type with the highest profit of all.
                // Get revenue per sock type
                KTable<String, Double> out = lines
                                .groupBy((key, value) -> value.getType())
                                .aggregate(
                                                () -> 0.0,
                                                (aggKey, newValue, aggValue) -> aggValue
                                                                + (newValue.getPricePerPair() * newValue.getQuantity()),
                                                Materialized.with(Serdes.String(), Serdes.Double()));

                // Get the expenses per socktype
                KTable<String, Double> out1 = lines1
                                .groupBy((key, value) -> value.getType())
                                .aggregate(
                                                () -> 0.0,
                                                (aggKey, newValue, aggValue) -> aggValue
                                                                + (newValue.getPricePerPair() * newValue.getQuantity()),
                                                Materialized.with(Serdes.String(), Serdes.Double()));

                // profit per sock type
                KTable<String, Double> profitPerSock = out
                                .join(out1, (revenueValue, expenseValue) -> revenueValue - expenseValue);

                profitPerSock.toStream().foreach((key, value) -> System.out.println(key + " Profit: " + value));

                // Get the sock type with the highest profit of all.
                KTable<String, Double> maxProfitTable = profitPerSock.toStream()
                                .groupByKey(Grouped.with(Serdes.String(), Serdes.Double()))
                                .reduce((aggValue, newValue) -> newValue);

                // out.toStream().to(outtopicname, Produced.with(Serdes.String(),
                // Serdes.Double()));

                maxProfitTable.mapValues((k, v) -> {
                        prof.processProfit(k, v);
                        String a = "{\"schema\":{\"type\":\"struct\",\"fields\":" +
                                        "[{\"type\":\"string\",\"optional\":false,\"field\":\"id\"}," +
                                        "{\"type\":\"double\",\"optional\":false,\"field\":\"SockTypeHighestProfit\"}" +
                                        "]}," +
                                        "\"payload\":{\"type\":\"" + prof.getSupplierWithHighestProfit()
                                        + "}}";
                        System.out.println(a);
                        return a;
                }).toStream().to("REQ13", Produced.with(Serdes.String(), Serdes.String()));
                // print the result

                KafkaStreams streams = new KafkaStreams(builder.build(), props);
                streams.start();

                System.out.println("Reading stream from topic ");

        }
}
