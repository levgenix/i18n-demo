package com.example.service.impl;

import java.util.Map;
import java.util.Objects;
import com.example.service.Localizer;

/**
 * In-memory реализация {@link Localizer}, работающая на основе переданных карт сообщений и префикса.
 * <p>
 * Обычно создается автоматически при вызове {@code withPrefix(...)} в режиме v1.
 * </p>
 */
public class MapLocalizer implements Localizer {

    private final String prefix;
    private final Map<String, String> defaultMessages;
    private Map<String, String> langMessages;

    public MapLocalizer(String prefix, Map<String, String> defaultMessages) {
        this.prefix = prefix;
        this.defaultMessages = defaultMessages;
    }

    public MapLocalizer(String prefix, Map<String, String> defaultMessages, Map<String, String> langMessages) {
        this(prefix, defaultMessages);
        this.langMessages = langMessages;
    }

    @Override
    public String localize(String mnemonic) {
        String key = prefix + "." + mnemonic;
        if (Objects.isNull(langMessages) || langMessages.isEmpty()) {
            return defaultMessages.getOrDefault(key, key);
        } else {
            return langMessages.getOrDefault(key, key);
        }
    }

    @Override
    public String localizeDefault(String mnemonic) {
        String key = prefix + "." + mnemonic;
        return defaultMessages.getOrDefault(key, key);
    }

}
