package com.example.i18n.model;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;

/**
 * Запись (Record), представляющая закэшированный набор переводов для конкретного языка.
 * <p>
 * Хранит карту сообщений, метку времени загрузки кэша для отслеживания TTL 
 * и признак ошибки при загрузке (негативный кэш).
 * </p>
 * 
 * @param messages карта переводов вида "мнемоника -> локализованный текст"
 * @param loadedAt отметка времени момента подгрузки кэша
 * @param failed   признак ошибки загрузки ({@code true} — если внешняя загрузка завершилась ошибкой и кэш негативный)
 */
public record MessagesCache(
        Map<String, String> messages,
        Instant loadedAt,
        boolean failed) {

    /**
     * Проверяет актуальность текущего кэша на основе переданного времени жизни (TTL).
     * 
     * @param ttl допустимый период жизни кэша (Time-To-Live)
     * @return {@code true}, если период жизни кэша еще не истек относительно текущего времени;
     *         {@code false}, если кэш устарел и требует повторного обновления
     */
    public boolean actual(Duration ttl) {
        return loadedAt.plus(ttl)
                .isAfter(Instant.now());
    }
}
