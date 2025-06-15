package com.LetMeDoWith.LetMeDoWith.common.converter;

import com.LetMeDoWith.LetMeDoWith.common.holders.TimeZoneContextHolder;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Converter(autoApply = true)
public class DateTimeConverter implements AttributeConverter<LocalDateTime, Timestamp> {

    @Override
    public Timestamp convertToDatabaseColumn(LocalDateTime dateTime) {
        if (dateTime == null) return null;

        ZoneId timeZone = TimeZoneContextHolder.getTimeZone();
        return Timestamp.from(dateTime.atZone(timeZone).toInstant());
    }

    @Override
    public LocalDateTime convertToEntityAttribute(Timestamp timestamp) {
        if (timestamp == null) return null;

        ZoneId timeZone = TimeZoneContextHolder.getTimeZone();
        return timestamp.toInstant()
                .atZone(timeZone)
                .toLocalDateTime();
    }
}
