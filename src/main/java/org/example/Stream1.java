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


public class Stream1 {
    public static void main(String[] args) {
        BasicConfigurator.configure();
        String topicName = "testBuy1";
        String outtopicname = "req5";

        java.util.Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "exercises-application1");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "broker1:9092");
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.Integer().getClass());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, CustomSaleSerializer.class);

        StreamsBuilder builder = new StreamsBuilder();
        KStream<String, Sale> lines = builder.stream(topicName, Consumed.with(
                Serdes.String(),
                new CustomSaleSerializer()
        ));
        //get renenue per sock pair group the buy ID
        KTable<Integer, Double> out = lines
                .groupBy((key, value) -> value.getId())
                .aggregate(
                        () -> 0.0,
                        (aggKey, newValue, aggValue) -> aggValue + (newValue.getPricePerPair() * newValue.getQuantity()),
                        Materialized.with(Serdes.Integer(), Serdes.Double())
                );

        out.toStream().to(outtopicname, Produced.with(Serdes.Integer(), Serdes.Double()));
        //print the result and the latest results
        out.toStream().foreach((key, value) -> System.out.println("Buy: " + key + " Revenue: " + value));

        KafkaStreams streams = new KafkaStreams(builder.build(), props);
        streams.start();


        System.out.println("Reading stream from topic " + topicName);

    }
}
