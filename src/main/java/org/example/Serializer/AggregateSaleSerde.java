package org.example.Serializer;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serializer;

import java.util.Map;
public class AggregateSaleSerde  implements Serde<AggregateSale>, Serializer<AggregateSale>, Deserializer<AggregateSale>{

    @Override
    public AggregateSale deserialize(String topic, byte[] data) {
        try {
            if (data == null)
                return null;
            else
                return new ObjectMapper().readValue(data, AggregateSale.class);
        } catch (Exception e) {
            throw new SerializationException(e);
        }
    }

    @Override
    public void configure(Map<String, ?> map, boolean isKey) {

    }

    @Override
    public byte[] serialize(String topic, AggregateSale data) {
        try {
            if (data == null)
                return null;
            else
                return new ObjectMapper().writeValueAsBytes(data);
        } catch (Exception e) {
            throw new SerializationException("Error serializing JSON message", e);
        }
    }

    @Override
    public void close() {

    }

    @Override
    public Serializer<AggregateSale> serializer() {
        return this;
    }

    @Override
    public Deserializer<AggregateSale> deserializer() {
        return this;
    }
}
