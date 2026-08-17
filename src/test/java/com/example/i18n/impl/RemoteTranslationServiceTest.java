package com.example.i18n.impl;

import java.time.Duration;
import java.time.Instant;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.test.util.ReflectionTestUtils;

import com.example.i18n.LanguageProvider;
import com.example.i18n.TranslationService;
import com.example.i18n.config.InternationalizationProperties;
import com.example.i18n.model.MessagesCache;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.clearInvocations;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

class RemoteTranslationServiceTest {

    private LanguageProvider remoteProvider;
    private TranslationService localTranslationService;
    private InternationalizationProperties properties;
    private RemoteTranslationService service;

    @BeforeEach
    void setUp() {
        remoteProvider = mock(LanguageProvider.class);
        localTranslationService = mock(TranslationService.class);
        properties = new InternationalizationProperties("BE", "ru", 10L, 2L);
        service = new RemoteTranslationService(remoteProvider, properties, localTranslationService);
    }

    @AfterEach
    void tearDown() {
        LocaleContextHolder.resetLocaleContext();
    }

    @Test
    @DisplayName("preloadLanguage: should preload default language on init")
    void preloadLanguage_preloadsDefaultLanguage() {
        given(remoteProvider.getMessages("ru")).willReturn(Map.of("hello", "Привет"));

        service.init();

        verify(remoteProvider).getMessages("ru");

        @SuppressWarnings("unchecked")
        ConcurrentMap<String, MessagesCache> cache = (ConcurrentMap<String, MessagesCache>) ReflectionTestUtils.getField(service, "cache");
        assertThat(cache).containsKey("ru");

        MessagesCache ruCache = cache.get("ru");
        assertThat(ruCache.failed()).isFalse();
        assertThat(ruCache.messages()).containsEntry("hello", "Привет");
    }

    @Test
    @DisplayName("preloadLanguage: should create negative cache when remoteProvider returns empty or null on init")
    void preloadLanguage_createsNegativeCache_whenDefaultLanguageEmpty() {
        given(remoteProvider.getMessages("ru")).willReturn(Map.of());

        service.init();

        @SuppressWarnings("unchecked")
        ConcurrentMap<String, MessagesCache> cache = (ConcurrentMap<String, MessagesCache>) ReflectionTestUtils.getField(service, "cache");
        MessagesCache ruCache = cache.get("ru");
        assertThat(ruCache.failed()).isTrue();
        assertThat(ruCache.messages()).isEmpty();
    }

    @Test
    @DisplayName("getCache: should reuse valid cache when within successTtl")
    void getCache_usesCache_whenWithinSuccessTtl() {
        given(remoteProvider.getMessages("ru")).willReturn(Map.of("hello", "Привет"));
        service.init();

        clearInvocations(remoteProvider);

        // Submitting multiple requests for "ru" within TTL
        assertThat(service.localize("hello")).isEqualTo("Привет");
        assertThat(service.localize("hello")).isEqualTo("Привет");

        // Should not call remoteProvider again
        verifyNoInteractions(remoteProvider);
    }

    @Test
    @DisplayName("getCache: should reuse negative cache when within failureTtl")
    void getCache_usesNegativeCache_whenWithinFailureTtl() {
        given(remoteProvider.getMessages("en")).willReturn(Map.of());

        LocaleContextHolder.setLocale(Locale.ENGLISH);
        service.localize("welcome"); // First fetch fails -> negative cache (failureTtl = 2s)

        clearInvocations(remoteProvider);

        // Second call within failureTtl should reuse negative cache and not call remoteProvider
        service.localize("welcome");
        verifyNoInteractions(remoteProvider);
    }

    @Test
    @DisplayName("getCache: should refresh cache from remote when TTL has expired")
    void getCache_reloadsFromRemote_whenTtlExpired() {
        given(remoteProvider.getMessages("ru")).willReturn(Map.of("hello", "Привет"));
        service.init();

        // Inject an expired MessagesCache into cache to simulate TTL expiration
        @SuppressWarnings("unchecked")
        ConcurrentMap<String, MessagesCache> cache = (ConcurrentMap<String, MessagesCache>) ReflectionTestUtils.getField(service, "cache");
        MessagesCache expiredCache = new MessagesCache(Map.of("hello", "Привет"), Instant.now().minusSeconds(15), false);
        cache.put("ru", expiredCache);

        // Next fetch for expired cache
        given(remoteProvider.getMessages("ru")).willReturn(Map.of("hello", "Привет обновленный"));

        assertThat(service.localize("hello")).isEqualTo("Привет обновленный");
        verify(remoteProvider, times(2)).getMessages("ru"); // 1st during init, 2nd during reload
    }

    @Test
    @DisplayName("getCache: should fallback to old messages and mark failed when remote fetch fails on expired cache")
    void getCache_retainsOldMessages_whenRemoteFetchFailsOnExpiredCache() {
        given(remoteProvider.getMessages("ru")).willReturn(Map.of("hello", "Привет старый"));
        service.init();

        // Expire the cache
        @SuppressWarnings("unchecked")
        ConcurrentMap<String, MessagesCache> cache = (ConcurrentMap<String, MessagesCache>) ReflectionTestUtils.getField(service, "cache");
        MessagesCache expiredCache = new MessagesCache(Map.of("hello", "Привет старый"), Instant.now().minusSeconds(15), false);
        cache.put("ru", expiredCache);

        // Remote fetch fails (returns empty map)
        given(remoteProvider.getMessages("ru")).willReturn(Map.of());

        // Should return old messages "Привет старый" and update cache to failed=true
        assertThat(service.localize("hello")).isEqualTo("Привет старый");

        MessagesCache updatedCache = cache.get("ru");
        assertThat(updatedCache.failed()).isTrue();
        assertThat(updatedCache.messages()).containsEntry("hello", "Привет старый");
    }

    @Test
    @DisplayName("getCache: should return empty map when first fetch fails on uncached language")
    void getCache_returnsEmptyMap_whenFirstFetchFails() {
        given(remoteProvider.getMessages("fr")).willReturn(Map.of());
        given(localTranslationService.localize("salut")).willReturn("Salut local");

        LocaleContextHolder.setLocale(Locale.FRENCH);

        assertThat(service.localize("salut")).isEqualTo("Salut local");

        @SuppressWarnings("unchecked")
        ConcurrentMap<String, MessagesCache> cache = (ConcurrentMap<String, MessagesCache>) ReflectionTestUtils.getField(service, "cache");
        MessagesCache frCache = cache.get("fr");
        assertThat(frCache.failed()).isTrue();
        assertThat(frCache.messages()).isEmpty();
    }

    @Test
    @DisplayName("localize: should resolve language from LocaleContextHolder")
    void localize_usesLocaleContextHolder() {
        given(remoteProvider.getMessages("ru")).willReturn(Map.of("welcome", "Привет"));
        given(remoteProvider.getMessages("en")).willReturn(Map.of("welcome", "Welcome"));

        service.init();

        LocaleContextHolder.setLocale(Locale.ENGLISH);
        assertThat(service.localize("welcome")).isEqualTo("Welcome");
    }

    @Test
    @DisplayName("localize: should fallback to localTranslationService when mnemonic missing in remote cache")
    void localize_fallbacksToLocalTranslationService() {
        given(remoteProvider.getMessages("ru")).willReturn(Map.of("hello", "Привет"));
        given(localTranslationService.localize("missing_key")).willReturn("Локальный текст");

        service.init();

        assertThat(service.localize("missing_key")).isEqualTo("Локальный текст");
        verify(localTranslationService).localize("missing_key");
    }
}
