package com.example.i18n.config;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.ResourcePatternResolver;

import com.example.JsonProcessingUtil;
import com.example.client.InternationalizationClient;
import com.example.i18n.LanguageProvider;
import com.example.i18n.impl.LocalLanguageProvider;
import com.example.i18n.impl.RemoteLanguageProvider;
import com.example.service.InternationalizationService;
import com.example.service.impl.InternationalizationServiceImpl;
import com.example.service.impl.stub.InternationalizationServiceStub;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
@EnableConfigurationProperties(InternationalizationProperties.class)
public class InternationalizationServiceConfig {

    @Value("classpath:messages_ru.json")
    private String resourcePath;

    // @Bean(value = "defaultMessages")
    public Map<String, String> readDefaultMessages() {
        try {
            return JsonProcessingUtil.getObjectMapper().readValue(new File(resourcePath),
                    new TypeReference<Map<String, String>>() {
                    });
        } catch (IOException e) {
            return new HashMap<>();
        }
    }

    @Bean(value = "localLanguageProvider")
    @ConditionalOnProperty(value = "internationalization.enabled", havingValue = "true")
    public InternationalizationService internationalizationService(
            InternationalizationClient internationalizationClient,
            InternationalizationProperties properties,
            ResourcePatternResolver resolver) {
        return new InternationalizationServiceImpl(readDefaultMessages(), properties.getNamespace(), resolver);
    }

    @Bean(value = "internationalizationServiceStub")
    // @ConditionalOnProperty(value = "internationalization.enabled", havingValue =
    // "true", matchIfMissing = true)
    @ConditionalOnMissingBean(InternationalizationService.class)
    public InternationalizationService InternationalizationServiceStub(
            InternationalizationProperties properties,
            InternationalizationClient client) {
        return new InternationalizationServiceStub(readDefaultMessages(), properties.getNamespace(), client);
    }
}
