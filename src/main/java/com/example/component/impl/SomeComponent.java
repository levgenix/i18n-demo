package com.example.component.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.example.component.AbstractComponent;
import com.example.component.ComponentUtils;
import com.example.service.Localizer;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class SomeComponent extends AbstractComponent {

    private static final String PREFIX = "someComponent";

    private static final Map<String, String> MESSAGES = Map.of(
            PREFIX + "ERROR_NAMES.key.0", PREFIX + "ERROR_NAMES.value.0",
            PREFIX + "ERROR_NAMES.key.1", PREFIX + "ERROR_NAMES.value.1");

    @Override
    public void initialize() {
        log.info("Initializing SomeComponent");

        getSomething();

        new FieldBuilder(getLocalizer());

        ComponentUtils.printHello(this::localize);

        Map<String, String> localizedMap = new HashMap<>();
        MESSAGES.forEach((key, value) -> {
            localizedMap.put(localizeDefault(key), localize(value));
        });
        log.info("localizedMap: {}", localizedMap);
    }

    private void getSomething() {
        log.info("getSomething private method: {}", localize("goodbye"));
    }

    private static class FieldBuilder {
        private final Localizer localizer;

        public FieldBuilder(Localizer localizer) {
            this.localizer = localizer;
            getSomething();
        }

        private void getSomething() {
            log.info("getSomething from FieldBuilder: {}", localizer.localize("goodbye"));
        }
    }

}
