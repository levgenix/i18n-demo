package com.example;

import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonProcessingUtil {

    public static ObjectMapper getObjectMapper() {
        return new ObjectMapper();
    }

}
