package com.example.i18n.impl;

import static org.springframework.core.io.ResourceLoader.CLASSPATH_URL_PREFIX;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.type.TypeReference;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Component;
import com.example.i18n.LanguageProvider;
import com.example.JsonProcessingUtil;

/**
 * Загрузчик переводов из локальных ресурсов для языка по-умолчанию.
 */
@Slf4j
public class LocalLanguageProvider extends AbstractLanguageProvider {

    private static final String LOCATION_PATTERN = CLASSPATH_URL_PREFIX + "i18n/*_%s.json";

    private final ResourcePatternResolver resolver;

    public LocalLanguageProvider(String namespace, ResourcePatternResolver resolver) {
        super(namespace);
        this.resolver = resolver;
    }

    @Override
    public Map<String, String> getMessages(String language) {
        try {
            Resource[] resources = resolver.getResources(resolveLocationPattern(language));
            Map<String, String> messages = new HashMap<>();
            for (Resource resource : resources) {
                try (InputStream inputStream = resource.getInputStream()) {
                    Map<String, String> parsed = JsonProcessingUtil.getObjectMapper().readValue(
                            inputStream,
                            new TypeReference<Map<String, String>>() {
                            });
                    if (parsed != null) {
                        messages.putAll(parsed.entrySet().stream()
                                .collect(Collectors.toMap(e -> resolveMnemonic(e.getKey()), Map.Entry::getValue)));
                    }
                }
            }
            log.info("Loaded {} local i18n messages", messages.size());
            return messages;
        } catch (IOException e) {
            log.error("Unable to load local i18n messages: {}", e.getMessage());
            return Collections.emptyMap();
        }
    }

    private String resolveLocationPattern(String language) {
        return String.format(LOCATION_PATTERN, language);
    }
}
