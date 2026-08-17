package com.example.i18n.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.support.ResourcePatternResolver;
import com.example.i18n.LanguageProvider;
import com.example.i18n.impl.LocalLanguageProvider;
import com.example.i18n.impl.RemoteLanguageProvider;
import com.example.client.InternationalizationClient;

@Configuration
@EnableConfigurationProperties(InternationalizationProperties.class)
public class LanguageProviderConfiguration {

    @Primary
    @Bean(value = "localLanguageProvider")
    public LanguageProvider localLanguageProvider(
            InternationalizationProperties properties,
            ResourcePatternResolver resolver) {
        return new LocalLanguageProvider(properties.getNamespace(), resolver);
    }

    @Bean(value = "remoteLanguageProvider")
    @ConditionalOnProperty(value = "internationalization.enabled", havingValue = "true", matchIfMissing = true)
    public LanguageProvider remoteLanguageProvider(
            InternationalizationProperties properties,
            InternationalizationClient client) {
        return new RemoteLanguageProvider(properties.getNamespace(), client);
    }
}
