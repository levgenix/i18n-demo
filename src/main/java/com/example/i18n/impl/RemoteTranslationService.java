package com.example.i18n.impl;

import java.time.Duration;
import java.time.Instant;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.i18n.LocaleContext;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.util.StringUtils;
import com.example.i18n.LanguageProvider;
import com.example.i18n.TranslationService;
import com.example.i18n.config.InternationalizationProperties;
import com.example.i18n.model.MessagesCache;

/**
 * Управляет кешем переводов для языков.
 * <p>
 * Для поиска наборов переводов использует загрузчик внешних переводов,
 * или загрузчик переводов локальных ресурсов, если язык не будет найден.
 * </p>
 */
@Slf4j
public class RemoteTranslationService extends AbstractTranslationService {

    /**
     * Кеш переводов по языкам
     * key -> language; value -> cache messages
     */
    private final ConcurrentMap<String, MessagesCache> cache = new ConcurrentHashMap<>();

    private final LanguageProvider remoteProvider;

    // TTL успешного кеша перевода. После истечения времени выполняется повторная
    // загрузка.
    private final Duration successTtl;
    // TTL негативного кеша переводов. Используется после ошибки загрузки из
    // внешнего сервиса.
    private final Duration failureTtl;
    private final TranslationService localTranslationService;

    public RemoteTranslationService(LanguageProvider remoteProvider,
            InternationalizationProperties properties, TranslationService localTranslationService) {
        super(properties.getLanguage());
        this.remoteProvider = remoteProvider;
        this.successTtl = Duration.ofSeconds(properties.getSuccessTtl());
        this.failureTtl = Duration.ofSeconds(properties.getFailureTtl());
        this.localTranslationService = localTranslationService;
    }

    @Override
    protected void preloadLanguage() {
        Map<String, String> messages = remoteProvider.getMessages(defaultLanguage);
        if (messages.isEmpty()) {
            cache.put(defaultLanguage, new MessagesCache(Map.of(), Instant.now(), true));
        } else {
            cache.put(defaultLanguage, new MessagesCache(Map.copyOf(messages), Instant.now(), false));
        }
    }

    /**
     * @return значение перевода по мнемонике для пользовательского языка
     */
    @Override
    public String localize(String mnemonic) {
        String language = resolveLang();
        Map<String, String> messages = getCache(language);
        if (messages.containsKey(mnemonic)) {
            return messages.get(mnemonic);
        }

        log.warn("I18n remote mnemonic not found {}: {}", language, mnemonic);
        return localTranslationService.localize(mnemonic);
    }

    /**
     * Проверит наличие и актуальность кэша языка.
     * При необходимости подгрузит в кэш.
     * При ошибке создает негативный кэш.
     * 
     * @return переводы для языка
     */
    private Map<String, String> getCache(String language) {
        MessagesCache existing = cache.get(language);
        if (existing != null) {
            Duration ttl = existing.failed() ? failureTtl : successTtl;
            if (existing.actual(ttl)) {
                return existing.messages();
            }
        }

        MessagesCache updated = cache.compute(language, (lang, currentCache) -> {
            if (currentCache != null) {
                Duration ttl = currentCache.failed() ? failureTtl : successTtl;
                if (currentCache.actual(ttl)) {
                    return currentCache;
                }
            }

            Map<String, String> fetched = remoteProvider.getMessages(lang);
            if (fetched.isEmpty()) {
                if (currentCache != null && !currentCache.messages().isEmpty()) {
                    log.warn("Use old loaded i18n remote messages for lang:{}", lang);
                    return new MessagesCache(currentCache.messages(), Instant.now(), true);
                }
                return new MessagesCache(Map.of(), Instant.now(), true);
            }
            return new MessagesCache(Map.copyOf(fetched), Instant.now(), false);
        });

        return updated.messages();
    }

    /**
     * Определит текущий язык пользователя из LocaleContextHolder.
     * Если пользовательский язык не задан, то использовать язык по-умолчанию.
     */
    private String resolveLang() {
        return Optional.ofNullable(LocaleContextHolder.getLocaleContext())
                .map(LocaleContext::getLocale)
                .map(Locale::getLanguage)
                .filter(StringUtils::hasText)
                .orElse(defaultLanguage);
    }
}
