package com.example.component;

import org.springframework.beans.factory.annotation.Autowired;

import com.example.service.InternationalizationService;
import com.example.service.Localizer;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class AbstractComponent implements Localizer {

    @Autowired
    protected InternationalizationService internationalizationService;

    public abstract void initialize();

    @Override
    public String localize(String mnemonic) {
        return internationalizationService.localize(mnemonic);
    }

    @Override
    public String localizeDefault(String mnemonic) {
        return internationalizationService.localizeDefault(mnemonic);
    }

    protected Localizer getLocalizer() {
        return internationalizationService.withPrefix(getClass().getSimpleName());
    }

}
