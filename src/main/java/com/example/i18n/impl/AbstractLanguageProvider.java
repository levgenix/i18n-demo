package com.example.i18n.impl;

import lombok.extern.slf4j.Slf4j;
import com.example.i18n.LanguageProvider;

@Slf4j
public abstract class AbstractLanguageProvider implements LanguageProvider {

    protected final String namespace;

    protected AbstractLanguageProvider(String namespace) {
        this.namespace = namespace;
    }

    protected String resolveMnemonic(String mnemonic) {
        log.trace("{} Mnemonic: {}", this.getClass().getSimpleName(), mnemonic);
        String prefix = namespace + ".";
        if (mnemonic != null && mnemonic.startsWith(prefix)) {
            return mnemonic.substring(prefix.length());
        }
        return mnemonic;
    }
}
