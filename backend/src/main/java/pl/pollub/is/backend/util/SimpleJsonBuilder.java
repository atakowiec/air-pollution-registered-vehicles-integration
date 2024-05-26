package pl.pollub.is.backend.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.Map;

public class SimpleJsonBuilder {
    private final Map<String, Object> json = new HashMap<>();

    private SimpleJsonBuilder() {
        // empty
    }

    public static SimpleJsonBuilder of(String key, Object value) {
        return new SimpleJsonBuilder().add(key, value);
    }

    public static SimpleJsonBuilder empty() {
        return new SimpleJsonBuilder();
    }

    public SimpleJsonBuilder add(String key, Object value) {
        json.put(key, value);
        return this;
    }

    public Map<String, Object> build() {
        return json;
    }

    public String toJson() {
        try {
            return new ObjectMapper().writeValueAsString(json);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
