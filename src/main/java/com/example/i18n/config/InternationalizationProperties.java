package com.example.i18n.config;

import java.util.Objects;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.ConstructorBinding;
import org.springframework.util.StringUtils;

@ConfigurationProperties(prefix = "internationalization")
@Getter
public class InternationalizationProperties {

    private static final String DEFAULT_NAMESPACE = "BE";
    private static final String DEFAULT_LANGUAGE = "ru";
    private static final Long DEFAULT_SUCCESS_TTL = 21600L;
    private static final Long DEFAULT_FAILURE_TTL = 60L;
    private static final String VERSION_V1 = "v1";

    private final String namespace;
    private final String prefix;
    private final String language;
    private final Long successTtl;
    private final Long failureTtl;
    private final String version;

    @ConstructorBinding
    public InternationalizationProperties(String prefix, String language, Long successTtl, Long failureTtl,
            String version) {
        if (StringUtils.hasText(prefix)) {
            this.namespace = prefix;
            this.prefix = prefix + ".";
        } else {
            this.namespace = DEFAULT_NAMESPACE;
            this.prefix = DEFAULT_NAMESPACE + ".";
        }
        this.language = StringUtils.hasText(language) ? language : DEFAULT_LANGUAGE;
        this.successTtl = Objects.requireNonNullElse(successTtl, DEFAULT_SUCCESS_TTL);
        this.failureTtl = Objects.requireNonNullElse(failureTtl, DEFAULT_FAILURE_TTL);
        this.version = StringUtils.hasText(version) ? version : VERSION_V1;
    }
}
