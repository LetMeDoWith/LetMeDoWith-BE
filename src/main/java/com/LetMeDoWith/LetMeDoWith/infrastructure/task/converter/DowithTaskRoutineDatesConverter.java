package com.LetMeDoWith.LetMeDoWith.infrastructure.task.converter;

import com.LetMeDoWith.LetMeDoWith.domain.task.model.DowithTaskRoutineDates;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@Converter(autoApply = true)
public class DowithTaskRoutineDatesConverter
        implements AttributeConverter<DowithTaskRoutineDates, String> {

    @Override
    public String convertToDatabaseColumn(DowithTaskRoutineDates dowithTaskRoutineDates) {
        StringBuilder sb = new StringBuilder();
        dowithTaskRoutineDates
                .getDates()
                .forEach(
                        date -> {
                            sb.append(date.toString());
                            sb.append("/");
                        });
        sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }

    @Override
    public DowithTaskRoutineDates convertToEntityAttribute(String s) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        Set<LocalDate> dates =
                Arrays.stream(s.split("/"))
                        .map(date -> LocalDate.parse(date, formatter))
                        .collect(Collectors.toSet());
        return DowithTaskRoutineDates.from(dates);
    }
}
