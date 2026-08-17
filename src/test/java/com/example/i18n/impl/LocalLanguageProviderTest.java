package com.example.i18n.impl;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

class LocalLanguageProviderTest {

    @Test
    @DisplayName("getMessages should load and parse local JSON resources")
    void getMessages_loadsAndParsesJsonResource() throws IOException {
        ResourcePatternResolver resolver = mock(ResourcePatternResolver.class);
        Resource resource = mock(Resource.class);

        String json = "{\"BE.hello\": \"Привет\", \"BE.goodbye\": \"Пока\"}";
        given(resource.getInputStream()).willReturn(new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8)));
        given(resolver.getResources(anyString())).willReturn(new Resource[]{resource});

        LocalLanguageProvider provider = new LocalLanguageProvider("BE", resolver);
        Map<String, String> messages = provider.getMessages("ru");

        assertThat(messages)
                .containsEntry("hello", "Привет")
                .containsEntry("goodbye", "Пока");
    }

    @Test
    @DisplayName("getMessages should return empty map when IOException occurs")
    void getMessages_returnsEmptyMap_onIOException() throws IOException {
        ResourcePatternResolver resolver = mock(ResourcePatternResolver.class);
        given(resolver.getResources(anyString())).willThrow(new IOException("File not found"));

        LocalLanguageProvider provider = new LocalLanguageProvider("BE", resolver);
        Map<String, String> messages = provider.getMessages("ru");

        assertThat(messages).isEmpty();
    }
}
