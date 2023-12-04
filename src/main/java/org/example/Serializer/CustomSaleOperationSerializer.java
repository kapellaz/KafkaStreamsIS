package org.example.Serializer;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serializer;

import java.util.Map;

public class CustomSaleOperationSerializer implements Serde<Sale_Operation>, Serializer<Sale_Operation>, Deserializer<Sale_Operation>{
    //json serializer for the class Sale
    @Override
    public byte[] serialize(String topic, Sale_Operation data) {
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
    public Sale_Operation deserialize(String topic, byte[] data) {
        try {
            if (data == null)
                return null;
            else
                return new ObjectMapper().readValue(data, Sale_Operation.class);
        } catch (Exception e) {
            throw new SerializationException(e);
        }
    }

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
        // nothing to configure
    }

    @Override
    public void close() {
        // nothing to close
    }

    @Override
    public Serializer<Sale_Operation> serializer() {
        return this;
    }

    @Override
    public Deserializer<Sale_Operation> deserializer() {
        return this;
    }

}
