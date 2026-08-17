package com.example.service.impl.stub;

import java.util.Map;

import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import com.example.i18n.TranslationService;
import com.example.service.InternationalizationService;
import com.example.service.Localizer;
import com.example.service.impl.MapLocalizer;

/**
 * Реализация сервиса интернационализации, использующая внутренний ресурс
 * src/main/resources/messages_ru.json
 */
@Slf4j
@Service
public class InternationalizationServiceStub implements InternationalizationService {

    protected final Map<String, String> defaultMessages;
    protected final String prefix;
    protected final String version;
    protected final TranslationService translationService;

    public InternationalizationServiceStub(
            Map<String, String> defaultMessages,
            String prefix,
            String version,
            TranslationService translationService) {
        this.defaultMessages = defaultMessages;
        this.prefix = prefix;
        this.version = version;
        this.translationService = translationService;
    }

    /**
     * "Пустая" инициализация сервиса при вызовах getService, getNextStep,
     * getPrevStep
     * 
     * @param serviceId не используется
     */
    @Override
    public void init(String serviceId) {
        log.info("Multilingual Stub initialized");
    }

    @Override
    public String localizeDefault(String mnemonic) {
        String key = prefix + "." + mnemonic;
        return defaultMessages.getOrDefault(key, key);
    }

    @Override
    public String localize(String mnemonic) {
        return localizeDefault(mnemonic);
    }

    @Override
    public boolean isLocalized() {
        return false;
    }

    @Override
    public Localizer withPrefix(String key) {
        return new MapLocalizer(prefix, filterByPrefix(prefix, key, defaultMessages));
    }
}
