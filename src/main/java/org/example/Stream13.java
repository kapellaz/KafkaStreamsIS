package org.example;

import org.apache.kafka.streams.kstream.*;
import org.apache.log4j.BasicConfigurator;
import java.util.Properties;

import org.apache.kafka.streams.state.KeyValueStore;

import org.apache.kafka.common.serialization.Serdes;

import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;

import org.example.Serializer.CustomSaleSerializer;
import org.example.Serializer.ProfitTracker;
import org.example.Serializer.Sale;

public class Stream13 {

        public static String name = "max-Store-Name";

        public static void main(String[] args) {
                BasicConfigurator.configure();
                String topicName1 = "Buy";
                String topicName2 = "Sell";

                ProfitTracker prof = new ProfitTracker("profit_tracker_state.txt");

                Properties props = new Properties();
                props.put(StreamsConfig.APPLICATION_ID_CONFIG, "exercises-application13");
                props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "broker1:9092,broker2:9092,broker3:9092");
                props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
                props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, CustomSaleSerializer.class);

                StreamsBuilder builder = new StreamsBuilder();

                KStream<String, Sale> lines = builder.stream(topicName1, Consumed.with(
                                Serdes.String(),
                                new CustomSaleSerializer()));
                lines.foreach((key, value) -> System.out.println("key: " + key + " Value: " + value));

                KStream<String, Sale> lines2 = builder.stream(topicName2, Consumed.with(
                                Serdes.String(),
                                new CustomSaleSerializer()));

                lines2.foreach((key, value) -> System.out.println("key: " + key + " Value: " + value));

                // Junta Buy com o seu supplier
                KTable<String, Double> BuyTable = lines
                                .groupBy((key, value) -> value.getSupplierIdentifier())
                                .aggregate(
                                                () -> 0.0,
                                                (aggKey, newValue, aggValue) -> aggValue
                                                                + (newValue.getPricePerPair() * newValue.getQuantity()),
                                                Materialized.with(Serdes.String(), Serdes.Double()));

                // Junta Sell com o seu supplier
                KTable<String, Double> SellTable = lines2
                                .groupBy((key, value) -> value.getSupplierIdentifier())
                                .aggregate(
                                                () -> 0.0,
                                                (aggKey, newValue, aggValue) -> aggValue
                                                                + (newValue.getPricePerPair() * newValue.getQuantity()),
                                                Materialized.with(Serdes.String(), Serdes.Double()));
                // Calcula Profit para cada Supplier
                KTable<String, Double> JoinBothTable = BuyTable.join(SellTable,
                                (revenueValue, expenseValue) -> revenueValue - expenseValue);

                JoinBothTable.toStream()
                                .foreach((key, value) -> System.out.println("O " + key + " com profit " + value));

                // Verifica o maior profit de cada
                KTable<String, Double> maxProfitTableEachOne = JoinBothTable.toStream()
                                .groupByKey()
                                .reduce(

                                                (value1, value2) -> value2,
                                                Materialized.<String, Double, KeyValueStore<org.apache.kafka.common.utils.Bytes, byte[]>>as(
                                                                name)
                                                                .withKeySerde(Serdes.String())
                                                                .withValueSerde(Serdes.Double()));

                maxProfitTableEachOne.mapValues((k, v) -> {
                        prof.processProfit(k, v);
                        String a = "{\"schema\":{\"type\":\"struct\",\"fields\":" +
                                        "[{\"type\":\"string\",\"optional\":false,\"field\":\"id\"}," +
                                        "{\"type\":\"string\",\"optional\":false,\"field\":\"NameSupplierWithHighestProfit\"},"
                                        +
                                        "{\"type\":\"double\",\"optional\":false,\"field\":\"HighestProfit\"}"
                                        +
                                        "]},"
                                        +
                                        "\"payload\":{\"id\":" + "\"" + "Sup" + "\""
                                        + ",\"NameSupplierWithHighestProfit\":" + "\""
                                        + prof.getSupplierWithHighestProfit()
                                        + "\",\"HighestProfit\":" + prof.getHighestProfit() + "}}";
                        System.out.println(a);
                        return a;
                }).toStream().to("REQ17", Produced.with(Serdes.String(), Serdes.String()));

                KafkaStreams streams = new KafkaStreams(builder.build(), props);
                streams.start();

                System.out.println("Reading stream from topic " + topicName1 + " and " + topicName2);
        }
}
