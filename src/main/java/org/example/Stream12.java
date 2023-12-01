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

                String buyTopic = "testBuy1";
                String sellTopic = "Sell";
                String outTopic = "profitResult";

                Properties props = new Properties();
                props.put(StreamsConfig.APPLICATION_ID_CONFIG, "exercises-application10004");
                props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "broker1:9092");
                props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
                props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, CustomSaleSerializer.class);

                // SELL
                StreamsBuilder builder = new StreamsBuilder();

                KStream<String, Sale> lines = builder.stream(sellTopic, Consumed.with(
                                Serdes.String(),
                                new CustomSaleSerializer()));

                lines.foreach((key, value) -> System.out.println("key1: " + key + " Value: " + value));

                KStream<String, Sale> lines2 = builder.stream(buyTopic, Consumed.with(
                                Serdes.String(),
                                new CustomSaleSerializer()));

                lines2.foreach((key, value) -> System.out.println("key2: " + key + " Value: " + value));

                // Get the total revenue in the last hour using a tumbling time window.

                KTable<Windowed<String>, Double> Sell = lines
                                .groupBy((key, value) -> value.getType(),
                                                Grouped.with(Serdes.String(), new CustomSaleSerializer()))
                                .windowedBy(TimeWindows.of(Duration.ofSeconds(30)))
                                .aggregate(
                                                () -> 0.0,
                                                (aggKey, newValue, aggValue) -> aggValue
                                                                + (newValue.getPricePerPair() * newValue.getQuantity()),
                                                Materialized.with(Serdes.String(), Serdes.Double()))
                                .toStream()
                                .groupByKey()
                                .reduce((aggValue, newValue) -> aggValue + newValue);

                Sell.toStream().foreach(
                                (key, value) -> System.out.println("Sock: " + key.key() + " Total Expenses: " + value));

                Sell.toStream().to(outTopic,
                                Produced.with(WindowedSerdes.timeWindowedSerdeFrom(String.class), Serdes.Double()));


                //BUY
                KTable<Windowed<String>, Double> BuyStream = lines2
                                .groupBy((key, value) -> value.getType(),
                                                Grouped.with(Serdes.String(), new CustomSaleSerializer()))
                                .windowedBy(TimeWindows.of(Duration.ofSeconds(30)))
                                .aggregate(
                                                () -> 0.0,
                                                (aggKey, newValue, aggValue) -> aggValue
                                                                + (newValue.getPricePerPair() * newValue.getQuantity()),
                                                Materialized.with(Serdes.String(), Serdes.Double()))
                                .toStream()
                                .groupByKey()
                                .reduce((aggValue, newValue) -> aggValue + newValue);
                BuyStream.toStream().foreach(
                                (key, value) -> System.out.println("Sock: " + key.key() + " Total Revenue: " + value));

                BuyStream.toStream().to(buyTopic,
                                Produced.with(WindowedSerdes.timeWindowedSerdeFrom(String.class),
                                                Serdes.Double()));
                //JUNTAR
                KTable<Windowed<String>, Double> profitTable = Sell
                                .leftJoin(
                                                BuyStream,
                                                (sellValue, buyValue) -> {
                                                        double sellAmount = (sellValue != null) ? sellValue : 0.0;
                                                        double buyAmount = (buyValue != null) ? buyValue : 0.0;
                                                        return buyAmount - sellAmount;
                                                });

                profitTable
                                .toStream()
                                .foreach((key, value) -> System.out
                                                .println("Stock: " + key.key() + " Profit: " + value));

                // Envie o resultado para o tópico de saída
                profitTable.toStream().to(outTopic,
                                Produced.with(WindowedSerdes.timeWindowedSerdeFrom(String.class),
                                                Serdes.Double()));
                KafkaStreams streams = new KafkaStreams(builder.build(), props);
                streams.start();

                System.out.println("Reading streams from topics " + buyTopic + " and " + sellTopic);

        }
}
