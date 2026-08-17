package com.example.i18n;

import java.util.Locale;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.core.MethodParameter;
import org.springframework.context.i18n.LocaleContextHolder;

import com.example.model.LocalizedRequest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

class LanguageRequestAdviceTest {

    @AfterEach
    void tearDown() {
        LocaleContextHolder.resetLocaleContext();
    }

    @Test
    @DisplayName("supports should return true when parameter is LocalizedRequest")
    void supports_returnsTrue_forLocalizedRequest() {
        LanguageRequestAdvice advice = new LanguageRequestAdvice();
        MethodParameter parameter = mock(MethodParameter.class);

        given((Class) parameter.getParameterType()).willReturn(MockLocalizedRequest.class);

        boolean supports = advice.supports(parameter, MockLocalizedRequest.class, null);
        assertThat(supports).isTrue();
    }

    @Test
    @DisplayName("afterBodyRead should set Locale in LocaleContextHolder from requestLang")
    void afterBodyRead_setsLocale() {
        LanguageRequestAdvice advice = new LanguageRequestAdvice();
        LocalizedRequest request = () -> "en";

        advice.afterBodyRead(request, null, null, null, null);

        assertThat(LocaleContextHolder.getLocale()).isEqualTo(Locale.ENGLISH);
    }

    interface MockLocalizedRequest extends LocalizedRequest {}
}
