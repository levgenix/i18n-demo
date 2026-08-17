package com.example.i18n.model;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MessagesCacheTest {

    @Test
    @DisplayName("actual() should return true when loaded within TTL duration")
    void actual_returnsTrue_whenWithinTtl() {
        MessagesCache cache = new MessagesCache(Map.of("key", "val"), Instant.now(), false);
        assertThat(cache.actual(Duration.ofMinutes(10))).isTrue();
    }

    @Test
    @DisplayName("actual() should return false when TTL duration has expired")
    void actual_returnsFalse_whenExpired() {
        MessagesCache cache = new MessagesCache(Map.of("key", "val"), Instant.now().minusSeconds(120), false);
        assertThat(cache.actual(Duration.ofSeconds(60))).isFalse();
    }
}
