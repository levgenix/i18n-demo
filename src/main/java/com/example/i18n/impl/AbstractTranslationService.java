package com.example.i18n.impl;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;
import com.example.i18n.TranslationService;

@Slf4j
public abstract class AbstractTranslationService implements TranslationService {

    protected final String defaultLanguage;

    protected AbstractTranslationService(String defaultLanguage) {
        this.defaultLanguage = defaultLanguage;
    }

    @PostConstruct
    public void init() {
        Assert.hasText(defaultLanguage, "internationalization.language must be configured");
        preloadLanguage();
    }

    /**
     * Прогрев дефолтного языка
     */
    protected abstract void preloadLanguage();
}
