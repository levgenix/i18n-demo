package com.example.service.impl;

import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.client.InternationalizationClient;
import com.example.i18n.TranslationService;
import com.example.service.Localizer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InternationalizationServiceImplTest {

    @Mock
    private TranslationService translationService;

    @Mock
    private InternationalizationClient internationalizationClient;

    @Test
    void testV2Behavior() {
        when(translationService.localize("BE.hello")).thenReturn("Hello v2");
        when(translationService.localizeDefault("BE.hello")).thenReturn("Hello v2");
        when(translationService.localize("BE.sub.world")).thenReturn("World v2");

        InternationalizationServiceImpl service = new InternationalizationServiceImpl(
                "BE", translationService, internationalizationClient);

        // init is no-op for v2
        service.init("serviceId");
        verifyNoInteractions(internationalizationClient);

        assertThat(service.isLocalized()).isTrue();
        assertThat(service.localize("hello")).isEqualTo("Hello v2");
        assertThat(service.localizeDefault("hello")).isEqualTo("Hello v2");

        Localizer subLocalizer = service.withPrefix("sub");
        assertThat(subLocalizer.localize("world")).isEqualTo("World v2");
        assertThat(subLocalizer.localizeDefault("world")).isEqualTo("World v2");
    }

    @Test
    void testV1Behavior() {
        when(internationalizationClient.getMessages("fr", "BE"))
                .thenReturn(Map.of("BE.hello", "Hello v1"));

        // Create v1 instance by passing version="v1" via custom constructor / subclass
        InternationalizationServiceImpl serviceV1 = new InternationalizationServiceImpl(
                "BE", "v1", translationService, internationalizationClient);

        assertThat(serviceV1.isLocalized()).isFalse();

        serviceV1.init("service1");

        assertThat(serviceV1.isLocalized()).isTrue();
        assertThat(serviceV1.localize("hello")).isEqualTo("Hello v1");
    }
}
