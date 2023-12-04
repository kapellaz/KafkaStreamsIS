package org.example;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.log4j.BasicConfigurator;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;


import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.KStream;
import org.example.Serializer.Sale_Operation;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;


public class KafkaConsumerDB {
    static Map<String, Sale_Operation> topicHash = new HashMap<>();
    static Object control = new Object();
    static boolean wait = false;
    static KafkaStreams streams;
    public static void main(String[] args) {
        SendToNewTopic();
    }

    public static void ReadDB(){
        BasicConfigurator.configure();
        String topicName = "dbToTopic";
        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "testapp61");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "broker1:9092");
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());

        StreamsBuilder builder = new StreamsBuilder();
        KStream<String, String> lines = builder.stream(topicName, Consumed.with(
                Serdes.String(),
                Serdes.String()
        ));

 
        ObjectMapper objectMapper = new ObjectMapper();

        lines.foreach((key, value) -> {
            try {
                // Parse the JSON value
                JsonNode jsonNode = objectMapper.readTree(value);
                JsonNode payloadNode = jsonNode.get("payload");
                int sockId = payloadNode.get("sockid").asInt();
                String sockType = payloadNode.get("socktype").asText();
                String operation = payloadNode.get("operation").asText();
                int socktypeid = payloadNode.get("socktypeid").asInt();
                int sockPrice = payloadNode.get("sockprice").asInt();
                String sockSupplierId = Integer.toString(payloadNode.get("socksupplierid").asInt());
                int quantity = payloadNode.get("quantity").asInt();

                System.out.println("====================================");
                System.out.println("sockId: " + sockId);
                System.out.println("sockType: " + sockType);
                System.out.println("operation: " + operation);
                System.out.println("socktypeid: " + socktypeid);
                System.out.println("sockPrice: " + sockPrice);
                System.out.println("sockSupplierId: " + sockSupplierId);
                System.out.println("quantity: " + quantity);
                topicHash.put(Integer.toString(sockId),new Sale_Operation(sockId,sockType,sockPrice,quantity,sockSupplierId,operation,socktypeid));
                wait=false;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }); 


            streams = new KafkaStreams(builder.build(), props);
           
            streams.start();
            System.out.println("Reading stream from topic " + topicName);
    }


    public static void SendToNewTopic(){
        BasicConfigurator.configure();
        System.out.println("entrou aqui");
        Properties props = new Properties();
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
                "org.apache.kafka.common.serialization.StringSerializer");
        while(true){
            if(topicHash.size()==0){
                wait=true;
                ReadDB();
                while(wait==true){
                    try {
                        Thread.sleep(10000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
            else{
                streams.close();
                Producer<String,String> producer = new KafkaProducer<>(props);
                for (Map.Entry<String, Sale_Operation> entry : topicHash.entrySet()) {
                    Sale_Operation v = entry.getValue();
                    String a = "{\"type\""+":"+"\""+v.getType()+"\""+","+"\"pricePerPair\""+":"+v.getPricePerPair()+","+"\"quantity\""+":"+v.getQuantity()+",\"supplierIdentifier\""+":"+"\""+v.getSupplierIdentifier()+"\"}";
                    System.out.println(a);
                    producer.send(new ProducerRecord<String,String>("buyteste1",v.getOperation(),a));
                }
                topicHash.clear();
                producer.close();
                
            }
        }
    }
}
