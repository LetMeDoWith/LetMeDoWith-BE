package com.LetMeDoWith.LetMeDoWith.common.converter;

import com.LetMeDoWith.LetMeDoWith.common.holders.TimeZoneContextHolder;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Converter(autoApply = true)
public class DateTimeConverter implements AttributeConverter<LocalDateTime, Instant> {

    @Override
    public Instant convertToDatabaseColumn(LocalDateTime dateTime) {
        if (dateTime == null) return null;

        ZoneId timeZone = TimeZoneContextHolder.getTimeZone();
        return dateTime.atZone(timeZone).toInstant();
    }

    @Override
    public LocalDateTime convertToEntityAttribute(Instant instant) {
        if (instant == null) return null;

        ZoneId timeZone = TimeZoneContextHolder.getTimeZone();
        return LocalDateTime.ofInstant(instant, timeZone);
    }
}
