package com.example.i18n;

import java.util.Map;

/**
 * Провайдер загрузки сырых словарей переводов для указанного языка из источника (файл, REST API, база данных).
 */
public interface LanguageProvider {

    /**
     * Загружает карту всех доступных пар "ключ-значение" для заданного языка.
     * 
     * @param language код языка (например, "ru", "en", "fr")
     * @return карта переводов или {@code null}/пустая карта при отсутствии ресурса
     */
    Map<String, String> getMessages(String language);

}
