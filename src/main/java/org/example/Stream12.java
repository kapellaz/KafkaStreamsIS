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

public class Stream12 {
        public static void main(String[] args) {
                BasicConfigurator.configure();
                String topicName1 = "Buy";
                String topicName2 = "Sell";

                Properties props = new Properties();
                props.put(StreamsConfig.APPLICATION_ID_CONFIG, "exercises-application12");
                props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "broker1:9092,broker2:9092,broker3:9092");
                props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
                props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, CustomSaleSerializer.class);

                StreamsBuilder builder = new StreamsBuilder();
                KStream<String, Sale> lines = builder.stream(topicName1, Consumed.with(
                                Serdes.String(),
                                new CustomSaleSerializer()));

                KStream<String, Sale> lines2 = builder.stream(topicName2, Consumed.with(
                                Serdes.String(),
                                new CustomSaleSerializer()));

                lines.foreach((key, value) -> System.out.println("key: " + key + " Value: " + value));

                lines2.foreach((key, value) -> System.out.println("key: " + key + " Value: " + value));

                // Get the total revenue in the last hour using a tumbling time window.

                KTable<Windowed<String>, Double> out = lines
                                .groupBy((key, value) -> value.getType())
                                .windowedBy(TimeWindows.of(Duration.ofSeconds(30)))
                                .aggregate(
                                                () -> 0.0,
                                                (aggKey, newValue, aggValue) -> aggValue
                                                                + (newValue.getPricePerPair() * newValue.getQuantity()),
                                                Materialized.with(Serdes.String(), Serdes.Double()))
                                .toStream()
                                .groupByKey()
                                .reduce((aggValue, newValue) -> aggValue);

                out.toStream().foreach(
                                (key, value) -> System.out.println("Sock: " + key.key() + " Total Revenue: " + value));

                // Get the total expenses in the last 30 seconds using a tumbling time window.
                KTable<Windowed<String>, Double> out1 = lines2
                                .groupBy((key, value) -> value.getType())
                                .windowedBy(TimeWindows.of(Duration.ofSeconds(30)))
                                .aggregate(
                                                () -> 0.0,
                                                (aggKey, newValue, aggValue) -> aggValue
                                                                + (newValue.getPricePerPair() * newValue.getQuantity()),
                                                Materialized.with(Serdes.String(), Serdes.Double()))
                                .toStream()
                                .groupByKey()
                                .reduce((aggValue, newValue) -> aggValue + newValue);

                out1.toStream().foreach(
                                (key, value) -> System.out.println("Sock: " + key.key() + " Total Expenses: " + value));

                // Join entre as duas KTables para calcular o lucro
                KTable<Windowed<String>, Double> profit = out.join(
                                out1,
                                (totalRevenue, totalExpenses) -> totalRevenue - totalExpenses);

                profit.mapValues((k, v) -> {
                        String a = "{\"schema\":{\"type\":\"struct\",\"fields\":" +
                                        "[{\"type\":\"string\",\"optional\":false,\"field\":\"id\"}," +
                                        "{\"type\":\"double\",\"optional\":false,\"field\":\"LastProfit\"}"
                                        +
                                        "]},"
                                        +
                                        "\"payload\":{\"id\":" + "\"" + "Profit" + "\""
                                        + ",\"LastProfit\":" + v + "}}";
                        System.out.println(a);
                        return a;
                }).toStream().to("REQ16",
                                Produced.with(WindowedSerdes.timeWindowedSerdeFrom(String.class), Serdes.String()));

                KafkaStreams streams = new KafkaStreams(builder.build(), props);
                streams.start();

                System.out.println("Reading stream from topic " + topicName1 + " and " + topicName2);
        }
}
