package com.example.i18n.impl;

import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;
import com.example.client.InternationalizationClient;

/**
 * Загрузчик переводов из внешнего сервиса для пользовательского языка.
 * <p>
 * Для поиска наборов переводов использует внешний сервис по неймспейсу, в нашем
 * случае "BE"
 * </p>
 */
@Slf4j
public class RemoteLanguageProvider extends AbstractLanguageProvider {

    // Клиент внешнего сервиса переводов
    private final InternationalizationClient client;

    public RemoteLanguageProvider(String namespace, InternationalizationClient client) {
        super(namespace);
        this.client = client;
    }

    @Override
    public Map<String, String> getMessages(String language) {
        try {
            Map<String, String> fetched = client.getMessages(language, namespace);
            if (fetched == null) {
                return Collections.emptyMap();
            }
            Map<String, String> messages = fetched.entrySet().stream()
                    .collect(Collectors.toMap(
                            e -> resolveMnemonic(e.getKey()),
                            Map.Entry::getValue));
            log.info("Loaded {} remote i18n messages", messages.size());
            return messages;
        } catch (Exception e) {
            log.error("Unable to load remote i18n messages: {}", e.getMessage());
        }
        return Collections.emptyMap();
    }
}
