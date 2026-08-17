package com.example.service.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.ResourceAccessException;
import com.example.i18n.TranslationService;
import com.example.service.InternationalizationService;
import com.example.service.Localizer;
import com.example.service.impl.stub.InternationalizationServiceStub;
import com.example.client.InternationalizationClient;

/**
 * Двухрежимная реализация сервиса интернационализации.
 * <p>
 * Поддерживает режим v1 (динамическая загрузка словаря под услугу через
 * {@link InternationalizationClient})
 * и режим v2 (проксирование в реактивный {@link TranslationService} с
 * автоопределением языка).
 * Выбор режима определяется параметром {@code version}.
 * </p>
 */
@Slf4j
@Service
public class InternationalizationServiceImpl extends InternationalizationServiceStub
        implements InternationalizationService {

    private static final String INITIALIZED_BY_LANG_MSG = "Initialized by lang={}, serviceId={}";
    private static final String CANT_BE_INITIALIZED_BY_LANG_MSG = "Can't be initialized by lang={}, serviceId={}";
    private static final String NOT_REQUIRED_MSG = "No language localization required for serviceId={}";
    private static final String NEW_VERSION_KEY = "v2";

    private static final Map<String, String> DEFAULT_MESSAGES = Map.of(
            "ru", "Русский",
            "en", "Английский");

    private final InternationalizationClient internationalizationClient;

    private Map<String, String> langMessages;

    public InternationalizationServiceImpl(
            String prefix,
            TranslationService translationService,
            InternationalizationClient internationalizationClient,
            String version) {
        super(DEFAULT_MESSAGES, prefix, version, translationService);
        this.internationalizationClient = internationalizationClient;
    }

    /*
     * public InternationalizationServiceImpl(
     * String prefix,
     * TranslationService translationService,
     * InternationalizationClient internationalizationClient) {
     * this(prefix, VERSION_KEY, translationService, internationalizationClient);
     * }
     */

    @Override
    public void init(String serviceId) {
        String lang = "fr";
        if (StringUtils.hasText(lang)) {
            initByLanguage(lang, serviceId);
            return;
        }
    }

    /**
     * Инициализация по языку с запросом к клиенту интернационализации, заполнение
     * langMessages
     * 
     * @param lang      язык локализации
     * @param serviceId идентификатор услуги, используется для логирования
     */
    private void initByLanguage(String lang, String serviceId) {
        if (Objects.isNull(langMessages)) {
            try {
                langMessages = internationalizationClient.getMessages(lang, prefix).entrySet().stream()
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
                log.info(INITIALIZED_BY_LANG_MSG, lang, serviceId);
            } catch (ResourceAccessException e) {
                langMessages = new HashMap<>();
                log.warn(CANT_BE_INITIALIZED_BY_LANG_MSG, lang, serviceId, e);
            }
        }
    }

    /**
     * Получение локализации по ключу с префиксом по настройке и добавлением ключа
     * локализации,
     * если не найдено значение в langMessages, то используется значение по
     * умолчанию из ресурса локализации
     * 
     * @param mnemonic ключ/мнемоника без префикса
     * @return локализованная строка
     */
    @Override
    public String localize(String mnemonic) {
        if (NEW_VERSION_KEY.equals(version)) {
            return translationService.localize(buildFullKey(mnemonic));
        }
        String key = prefix + "." + mnemonic;
        if (Objects.isNull(langMessages) || langMessages.isEmpty()) {
            return defaultMessages.getOrDefault(key, key);
        } else {
            return langMessages.get(key);
        }
    }

    @Override
    public String localizeDefault(String mnemonic) {
        if (NEW_VERSION_KEY.equals(version)) {
            return translationService.localizeDefault(buildFullKey(mnemonic));
        }
        return super.localizeDefault(mnemonic);
    }

    @Override
    public boolean isLocalized() {
        if (NEW_VERSION_KEY.equals(version)) {
            return true;
        }
        return Objects.nonNull(langMessages) && !langMessages.isEmpty();
    }

    @Override
    public Localizer withPrefix(String key) {
        if (NEW_VERSION_KEY.equals(version)) {
            String fullPrefix = buildFullKey(key);
            return mnemonic -> translationService.localize(
                    StringUtils.hasText(fullPrefix) ? fullPrefix + "." + mnemonic : mnemonic);
        }
        if (Objects.nonNull(langMessages) && !langMessages.isEmpty()) {
            return new MapLocalizer(prefix, filterByPrefix(prefix, key, defaultMessages),
                    filterByPrefix(prefix, key, langMessages));
        } else {
            return super.withPrefix(key);
        }
    }

    private String buildFullKey(String key) {
        if (!StringUtils.hasText(prefix)) {
            return key;
        }
        return prefix + "." + key;
    }
}
