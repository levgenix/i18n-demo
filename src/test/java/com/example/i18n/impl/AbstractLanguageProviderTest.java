package com.example.i18n.impl;

import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AbstractLanguageProviderTest {

    static class TestableLanguageProvider extends AbstractLanguageProvider {
        TestableLanguageProvider(String namespace) {
            super(namespace);
        }

        @Override
        public Map<String, String> getMessages(String language) {
            return Map.of();
        }
    }

    @Test
    @DisplayName("resolveMnemonic should strip namespace prefix when present")
    void resolveMnemonic_stripsPrefix() {
        TestableLanguageProvider provider = new TestableLanguageProvider("BE");

        String result = provider.resolveMnemonic("BE.hello");
        assertThat(result).isEqualTo("hello");
    }

    @Test
    @DisplayName("resolveMnemonic should return original string if prefix does not match")
    void resolveMnemonic_returnsOriginal_whenPrefixNotMatched() {
        TestableLanguageProvider provider = new TestableLanguageProvider("BE");

        String result = provider.resolveMnemonic("FE.hello");
        assertThat(result).isEqualTo("FE.hello");
    }

    @Test
    @DisplayName("resolveMnemonic should handle null gracefully")
    void resolveMnemonic_handlesNull() {
        TestableLanguageProvider provider = new TestableLanguageProvider("BE");

        String result = provider.resolveMnemonic(null);
        assertThat(result).isNull();
    }
}
