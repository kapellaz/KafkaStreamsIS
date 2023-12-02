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
import org.apache.kafka.streams.kstream.Grouped;

public class Stream9 {
    public static void main(String[] args) {
        BasicConfigurator.configure();
        String topicName = "Buy";
        String outtopicname = "req13";

        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "exercises-application9");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "broker1:9092");
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, CustomSaleSerializer.class);

        StreamsBuilder builder = new StreamsBuilder();
        KStream<String, Sale> lines = builder.stream(topicName, Consumed.with(
                Serdes.String(),
                new CustomSaleSerializer()
        ));

        lines.foreach((key,value)-> System.out.println("key: "+key+" Value: "+value));

        //Get the sock type with the highest profit of all.
        KTable<String, Double> out = lines
                .groupBy((key, value) -> value.getType())
                .aggregate(
                        () -> 0.0,
                        (aggKey, newValue, aggValue) -> aggValue + (newValue.getPricePerPair() * (0.5) * newValue.getQuantity()),
                        Materialized.with(Serdes.String(), Serdes.Double())
                )
                .toStream()
                .groupByKey(Grouped.with(Serdes.String(), Serdes.Double()))
                .reduce((aggValue, newValue) -> (Double.compare(aggValue, newValue) > 0) ? aggValue : newValue)
                ;

 
/*
        KTable<String, Double> out = lines.map((key,value)->{
                Double profit = value.getPricePerPair() * (0.5) * value.getQuantity();
                String tipo = value.getType();
                return new KeyValue<>(tipo, profit);
        }
        )
        .groupByKey(Grouped.with(Serdes.String(), Serdes.Double()))
        .reduce((a, b) -> Double.compare(a, b) > 0 ? a : b);

        out.toStream().peek((key,value)->{
                String output = "4. Max ==== profit: " + key + " maximum: " + value;
                System.out.println(output);
        })
        .to(outtopicname);
 */
        out.toStream().to(outtopicname, Produced.with(Serdes.String(), Serdes.Double()));
        //print the result
        out.toStream().foreach((key, value) -> System.out.println("Sock: "+ key + " Total Profit: " +value));
        KafkaStreams streams = new KafkaStreams(builder.build(), props);
        streams.start();


        System.out.println("Reading stream from topic " + topicName);

    }
<<<<<<< HEAD
}

=======
}
>>>>>>> 42f283b7516901d8a00372bebcabae50eb6baa75
