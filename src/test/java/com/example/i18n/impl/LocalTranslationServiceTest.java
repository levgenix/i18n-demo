package com.example.i18n.impl;

import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.test.util.ReflectionTestUtils;

import com.example.i18n.LanguageProvider;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class LocalTranslationServiceTest {

    @Test
    @DisplayName("localize should return translation for existing key and fallback to mnemonic when key is missing")
    void localize_returnsTranslationOrFallbackKey() {
        LanguageProvider provider = mock(LanguageProvider.class);
        given(provider.getMessages("ru")).willReturn(Map.of("hello", "Привет"));

        LocalTranslationService service = new LocalTranslationService("ru", provider);
        service.init();

        // 1. Проверяем, что был вызов provider.getMessages("ru")
        verify(provider).getMessages("ru");

        // 2. Проверяем, что поле messages не пустое (через ReflectionTestUtils)
        @SuppressWarnings("unchecked")
        Map<String, String> messages = (Map<String, String>) ReflectionTestUtils.getField(service, "messages");
        assertThat(messages).isNotEmpty().containsEntry("hello", "Привет");

        // 3. Проверяем локализацию существующего ключа и фолбэк для отсутствующего
        assertThat(service.localize("hello")).isEqualTo("Привет");
        assertThat(service.localize("unknown_key")).isEqualTo("unknown_key");
    }
}
