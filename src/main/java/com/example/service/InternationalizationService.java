package com.example.service;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * Главный контракт сервиса интернационализации приложения.
 * <p>
 * Расширяет {@link Localizer}, добавляя возможности управления жизненным циклом (инициализация),
 * проверки состояния локализации и иерархического контекстного связывания префиксов.
 * </p>
 */
public interface InternationalizationService extends Localizer {

    /**
     * Инициализирует контекст локализации для указанной услуги или шага сценария.
     * <p>
     * <b>Версия v1 (Legacy):</b> выполняет подгрузку словаря переводов через внешний REST-клиент под текущий язык.
     * <br>
     * <b>Версия v2 (Proxy):</b> является no-op операцией, так как язык определяется автоматически из {@code LocaleContextHolder}.
     * </p>
     * 
     * @param serviceId идентификатор услуги или шага бизнес-процесса
     */
    void init(String serviceId);

    /**
     * Проверяет, доступны ли языковые переводы для текущей сессии/запроса.
     * 
     * @return {@code true}, если загружен языковой словарь (или используется v2); 
     *         {@code false}, если используются базовые значения по умолчанию.
     */
    boolean isLocalized();

    /**
     * Создает дочерний {@link Localizer} с дополнительным контекстным префиксом.
     * <p>
     * Предназначен для изоляции мнемоник внутри конкретных экранных компонентов или вложенных модулей.
     * Все последующие вызовы {@code localize("key")} у полученного объекта будут автоматически 
     * производить поиск по полному ключу вида {@code "<текущий_префикс>.<subPrefix>.<key>"}.
     * </p>
     * <p><b>Пример использования:</b></p>
     * <pre>{@code
     * // Исходный сервис настроен на префикс "BE"
     * Localizer componentLocalizer = i18nService.withPrefix("someComponent");
     * 
     * // Ищет перевод по полному ключу "BE.someComponent.goodbye"
     * String text = componentLocalizer.localize("goodbye");
     * }</pre>
     * 
     * @param subPrefix добавочный сегмент префикса (например, имя компонента или блока)
     * @return сфокусированный экземпляр {@link Localizer} с усеченным контекстом
     */
    Localizer withPrefix(String subPrefix);

    default Map<String, String> filterByPrefix(String prefix, String key, Map<String, String> messages) {
        return messages.entrySet().stream()
                .filter(entry -> entry.getKey().startsWith(prefix + "." + key + "."))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
}
