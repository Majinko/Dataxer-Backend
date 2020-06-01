package com.data.dataxer.mappers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.persistence.AttributeConverter;
import java.io.IOException;
import java.util.Map;

public class HashMapConverter implements AttributeConverter<Map<String, Object>, String> {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(Map<String, Object> objectInfo) {
        String objectInfoJson = null;

        try {
            objectInfoJson = objectMapper.writeValueAsString(objectInfo);
        } catch (final JsonProcessingException e) {
            return null;
        }

        return objectInfoJson;
    }

    @Override
    public Map<String, Object> convertToEntityAttribute(String objectInfoJSON) {
        if (objectInfoJSON == null) {
            return null;
        }

        Map<String, Object> objectInfo = null;
        try {
            objectInfo = objectMapper.readValue(objectInfoJSON, Map.class);
        } catch (final IOException e) {
            return null;
        }

        return objectInfo;
    }
}
