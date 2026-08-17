package com.example.client;

import java.util.Map;

import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class InternationalizationClientStub implements InternationalizationClient {

    @Override
    public Map<String, String> getMessages(String language, String namespace) {
        log.info("Loading messages from remote service for language: {}", language);
        return Map.of(
                "BE.hello", "Hello",
                "BE.goodbye", "Goodbye");
    }
}
