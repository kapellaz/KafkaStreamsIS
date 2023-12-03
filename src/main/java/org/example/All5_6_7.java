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
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "exercises-application0");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "broker1:9092");
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.Integer().getClass());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, CustomSaleSerializer.class.getName());

        StreamsBuilder builder = new StreamsBuilder();
        KStream<String, Sale> lines = builder.stream(topicName1, Consumed.with(
                Serdes.String(), new CustomSaleSerializer()
        ));

        KStream<String, Sale> lines1 = builder.stream(topicName2, Consumed.with(
                Serdes.String(), new CustomSaleSerializer()
        ));

        //req5 get renenue per sock
        KTable<Integer, Double> out = lines
                .groupBy((key, value) -> value.getId())
                .aggregate(
                        () -> 0.0,
                        (aggKey, newValue, aggValue) -> aggValue + (newValue.getPricePerPair() * newValue.getQuantity()),
                        Materialized.with(Serdes.Integer(), Serdes.Double())
                );

        out.toStream().to("req5", Produced.with(Serdes.Integer(), Serdes.Double()));
        //print the result and the latest results
        out.toStream().foreach((key, value) -> System.out.println("Buy: " + key + " Revenue: " + value));

        
        //req6
        //get the expenses of one pair of socks
        KTable<Integer, Double> out1 = lines1
                .groupBy((key, value) -> value.getId())
                .aggregate(
                        () -> 0.0,
                        (aggKey, newValue, aggValue) -> aggValue + (newValue.getPricePerPair() * newValue.getQuantity()),
                        Materialized.with(Serdes.Integer(), Serdes.Double())
                );

        //out1.toStream().to("req6", Produced.with(Serdes.Integer(), Serdes.Double()));
        out1.mapValues((k,v)->{String a = "{\"schema\":{\"type\":\"struct\",\"fields\":" +
        "[{\"type\":\"int32\",\"optional\":false,\"field\":\"id\"}," +
        "{\"type\":\"int32\",\"optional\":false,\"field\":\"revenue\"}" +
        "],\"optional\":false}," +
        "\"payload\":{\"id\":"+k+",\"Expenses\":"+v+"}}";System.out.println(a); return a;}).
        toStream().to("testout", Produced.with(Serdes.Integer(), Serdes.String()));

        out1.toStream().foreach((key, value) -> System.out.println("Sell: " + key + " expenses: " + value));

        //req7 get profit per sock
        // Calculate profit per sock by joining revenue and expenses
        KTable<Integer, Double> profitPerSock = out
                .join(out1, (revenueValue, expenseValue) -> revenueValue - expenseValue);


        profitPerSock.toStream().to("req7", Produced.with(Serdes.Integer(), Serdes.Double()));
        profitPerSock.toStream().foreach((key, value) -> System.out.println("Buy: " + key + " Profit: " + value));


        KafkaStreams streams = new KafkaStreams(builder.build(), props);
        streams.start();

    }
}
