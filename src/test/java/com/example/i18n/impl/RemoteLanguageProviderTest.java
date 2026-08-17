package com.example.i18n.impl;

import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.example.client.InternationalizationClient;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

class RemoteLanguageProviderTest {

    @Test
    @DisplayName("getMessages should fetch messages from remote client and strip namespace")
    void getMessages_success() {
        InternationalizationClient client = mock(InternationalizationClient.class);
        given(client.getMessages("ru", "BE")).willReturn(Map.of("BE.welcome", "Добро пожаловать"));

        RemoteLanguageProvider provider = new RemoteLanguageProvider("BE", client);
        Map<String, String> messages = provider.getMessages("ru");

        assertThat(messages).containsEntry("welcome", "Добро пожаловать");
    }

    @Test
    @DisplayName("getMessages should return empty map when client throws Exception")
    void getMessages_handlesException() {
        InternationalizationClient client = mock(InternationalizationClient.class);
        given(client.getMessages("ru", "BE")).willThrow(new RuntimeException("Remote service timeout"));

        RemoteLanguageProvider provider = new RemoteLanguageProvider("BE", client);
        Map<String, String> messages = provider.getMessages("ru");

        assertThat(messages).isEmpty();
    }

    @Test
    @DisplayName("getMessages should return empty map when client returns null")
    void getMessages_handlesNullReturn() {
        InternationalizationClient client = mock(InternationalizationClient.class);
        given(client.getMessages("ru", "BE")).willReturn(null);

        RemoteLanguageProvider provider = new RemoteLanguageProvider("BE", client);
        Map<String, String> messages = provider.getMessages("ru");

        assertThat(messages).isEmpty();
    }
}
