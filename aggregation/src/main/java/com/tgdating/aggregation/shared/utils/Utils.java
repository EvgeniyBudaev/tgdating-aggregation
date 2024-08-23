package com.tgdating.aggregation.shared.utils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

public class Utils {
    public static LocalDateTime getNowUtc() {
//        ZonedDateTime nowUTC = ZonedDateTime.now(ZoneOffset.UTC); // Получаем текущее время в формате UTC
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").withZone(ZoneOffset.UTC);
        // Получаем текущее время в формате UTC как Instant
        Instant instant = Instant.now();
        // Преобразуем Instant в ZonedDateTime, указывая, что время в UTC
        ZonedDateTime zonedDateTime = instant.atZone(ZoneOffset.UTC);
        // Из ZonedDateTime извлекаем LocalDateTime
        return zonedDateTime.toLocalDateTime();
    }

    public static boolean calculateIsOnline(LocalDateTime lastOnline) {
        Instant now = Instant.now(); // Текущее время в формате Instant
        Instant lastOnlineInstant = lastOnline.toInstant(ZoneOffset.UTC); // Преобразование LocalDateTime в Instant
        long diffInMinutes = Math.abs(now.toEpochMilli() / 60000L - lastOnlineInstant.toEpochMilli() / 60000L); // Расчет разницы в минутах
        return diffInMinutes < 5; // Если разница меньше 5 минут, считается пользователь онлайн
    }
}
