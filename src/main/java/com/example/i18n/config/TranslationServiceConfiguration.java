package com.example.i18n.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import com.example.i18n.LanguageProvider;
import com.example.i18n.TranslationService;
import com.example.i18n.impl.LocalTranslationService;
import com.example.i18n.impl.RemoteTranslationService;

@Configuration
@EnableConfigurationProperties(InternationalizationProperties.class)
public class TranslationServiceConfiguration {

    @Bean(value = "localTranslationService")
    public TranslationService localTranslationService(
            @Qualifier("localLanguageProvider") LanguageProvider provider,
            InternationalizationProperties properties) {
        return new LocalTranslationService(properties.getLanguage(), provider);
    }

    @Primary
    @Bean(value = "remoteTranslationService")
    @ConditionalOnProperty(value = "internationalization.enabled", havingValue = "true", matchIfMissing = true)
    public TranslationService remoteTranslationService(
            // @Qualifier("remoteLanguageProvider") LanguageProvider provider,
            @Qualifier("localTranslationService") TranslationService localTranslationService,
            InternationalizationProperties properties) {
        return new RemoteTranslationService(provider, properties, localTranslationService);
    }
}
