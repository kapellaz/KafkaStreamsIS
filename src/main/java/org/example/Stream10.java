package org.example;

import org.apache.kafka.streams.kstream.*;
import org.apache.kafka.streams.state.WindowStore;
import org.apache.log4j.BasicConfigurator;
import java.util.Properties;
import java.time.Duration;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;

import org.example.Serializer.CustomSaleSerializer;
import org.example.Serializer.Sale;

public class Stream10 {
        public static void main(String[] args) {
                BasicConfigurator.configure();
                String topicName = "Buy";
                String outtopicname = "req14";

                Properties props = new Properties();
                props.put(StreamsConfig.APPLICATION_ID_CONFIG, "exercises-application10");
                props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "broker1:9092");
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

                out.toStream().foreach(
                                (key, value) -> System.out.println("Sock: " + key.key() + " Total Expenses: " + value));

                out.toStream().to(outtopicname,
                                Produced.with(WindowedSerdes.timeWindowedSerdeFrom(String.class), Serdes.Double()));

                KafkaStreams streams = new KafkaStreams(builder.build(), props);
                streams.start();

                System.out.println("Reading stream from topic " + topicName);
        }
}
