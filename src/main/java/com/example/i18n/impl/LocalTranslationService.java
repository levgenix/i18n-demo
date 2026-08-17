package com.example.i18n.impl;

import java.util.HashMap;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;
import com.example.i18n.LanguageProvider;

/**
 * Управляет кешем переводов для языка по-умолчанию из локальных ресурсов.
 * <p>
 * Для поиска набора переводов для языка по-умолчанию использует загрузчик
 * локальных ресурсов
 * </p>
 */
@Slf4j
public class LocalTranslationService extends AbstractTranslationService {

    private final LanguageProvider provider;
    private volatile Map<String, String> messages = new HashMap<>();

    public LocalTranslationService(String defaultLanguage, LanguageProvider provider) {
        super(defaultLanguage);
        this.provider = provider;
    }

    @Override
    protected void preloadLanguage() {
        Map<String, String> readedMessages = provider.getMessages(defaultLanguage);
        if (readedMessages != null) {
            this.messages = new HashMap<>(readedMessages);
            log.info("Preloaded {} local messages for default language '{}'", messages.size(), defaultLanguage);
        }
    }

    /**
     * @return значение перевода по мнемонике для языка по-умолчанию из локальных
     *         ресурсов
     */
    @Override
    public String localize(String mnemonic) {
        return messages.getOrDefault(mnemonic, mnemonic);
    }
}
