package org.example;
import java.util.Properties;

//import simple producer packages
import org.apache.kafka.clients.producer.Producer;

//import KafkaProducer packages
import org.apache.kafka.clients.producer.KafkaProducer;

//import ProducerRecord packages
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.log4j.BasicConfigurator;

//Create java class named “SimpleProducer”
public class SimpleProducer {

    public static void main(String[] args) throws Exception{
        //Configure the logger
        BasicConfigurator.configure();
        //Assign topicName to string variable
        String topicName = "Buy";

        // create instance for properties to access producer configs
        Properties props = new Properties();

        //Assign localhost id
        props.put("bootstrap.servers", "broker1:9092");

        //Set acknowledgements for producer requests.
        props.put("acks", "all");

        //If the request fails, the producer can automatically retry,
        props.put("retries", 0);

        //Specify buffer size in config
        props.put("batch.size", 16384);

        //Reduce the no of requests less than 0
        props.put("linger.ms", 1);

        //The buffer.memory controls the total amount of memory available to the producer for buffering.
        props.put("buffer.memory", 33554432);

        props.put("key.serializer",
                "org.apache.kafka.common.serialization.StringSerializer");

        props.put("value.serializer",
                "org.apache.kafka.common.serialization.LongSerializer");

        Producer<String, Long> producer = new KafkaProducer<>(props);

        producer.send(new ProducerRecord<String, Long>(topicName, "buy", (long) 3));

        System.out.println("Message sent successfully to topic " + topicName);
        producer.close();
    }
}