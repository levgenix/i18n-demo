package com.example.i18n.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class InternationalizationPropertiesTest {

    @Test
    @DisplayName("Should apply default values when constructor inputs are empty or null")
    void constructor_defaultsApplied() {
        InternationalizationProperties props = new InternationalizationProperties(null, null, null, null);

        assertThat(props.getNamespace()).isEqualTo("BE");
        assertThat(props.getPrefix()).isEqualTo("BE.");
        assertThat(props.getLanguage()).isEqualTo("ru");
        assertThat(props.getSuccessTtl()).isEqualTo(21600L);
        assertThat(props.getFailureTtl()).isEqualTo(60L);
    }

    @Test
    @DisplayName("Should apply custom values when constructor inputs are provided")
    void constructor_customValuesApplied() {
        InternationalizationProperties props = new InternationalizationProperties("FE", "en", 3600L, 30L);

        assertThat(props.getNamespace()).isEqualTo("FE");
        assertThat(props.getPrefix()).isEqualTo("FE.");
        assertThat(props.getLanguage()).isEqualTo("en");
        assertThat(props.getSuccessTtl()).isEqualTo(3600L);
        assertThat(props.getFailureTtl()).isEqualTo(30L);
    }
}
