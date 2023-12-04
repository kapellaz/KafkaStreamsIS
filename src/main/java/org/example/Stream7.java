package org.example;
import org.apache.kafka.streams.kstream.*;
import org.apache.log4j.BasicConfigurator;
import java.util.Properties;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.example.Serializer.AggregateSale;
import org.example.Serializer.AggregateSaleSerde;
import org.example.Serializer.CustomSaleSerializer;
import org.example.Serializer.Sale;


public class Stream7 {
    public static void main(String[] args) {
        BasicConfigurator.configure();
        String topicName = "Buy";
        String outtopicname = "req11";

        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "exercises-application1000001");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "broker1:9092");
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, CustomSaleSerializer.class);

        StreamsBuilder builder = new StreamsBuilder();
        KStream<String, Sale> lines = builder.stream(topicName, Consumed.with(
                Serdes.String(),
                new CustomSaleSerializer()
        ));
        //Get the average amount spent in each purchase (separated by sock type)
        KTable<String, AggregateSale> out = lines
                .groupBy((key, value) -> value.getType())
                .aggregate(
                        AggregateSale::new,
                        (aggKey, newValue, aggregate) ->{
                            aggregate.addAmount(newValue.getPricePerPair() * newValue.getQuantity());
                            aggregate.incrementCount();
                            return aggregate;
                        },

                        Materialized.with(Serdes.String(), new AggregateSaleSerde())
                );

        KTable<String, Double> out2 = out.mapValues(AggregateSale::Average);

        //out2.toStream().to(outtopicname, Produced.with(Serdes.String(), Serdes.Double()));
        out2.mapValues((k,v)->{String a = "{\"schema\":{\"type\":\"struct\",\"fields\":" +
        "[{\"type\":\"string\",\"optional\":false,\"field\":\"id\"},"+
        "{\"type\":\"double\",\"optional\":false,\"field\":\"AverageBySock\"}" +
        "]}," +
        "\"payload\":{\"id\":\""+k+"\",\"AverageBySock\":"+v+"}}";System.out.println(a); return a;}).
        toStream().to("REQ11", Produced.with(Serdes.String(), Serdes.String()));

        //print the result
        out2.toStream().foreach((key, value) -> System.out.println("Buy: " + key + " Average: " + value));

        KafkaStreams streams = new KafkaStreams(builder.build(), props);
        streams.start();


        System.out.println("Reading stream from topic " + topicName);

    }
}

