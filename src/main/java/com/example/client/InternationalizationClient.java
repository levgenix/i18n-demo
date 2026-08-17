package com.example.client;

import java.util.Map;

public interface InternationalizationClient {

    Map<String, String> getMessages(String language, String namespace);
}
