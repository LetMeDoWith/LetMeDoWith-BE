package com.LetMeDoWith.LetMeDoWith.infrastructure.task.converter;

import com.LetMeDoWith.LetMeDoWith.domain.task.model.TodoTaskRoutineDates;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@Converter(autoApply = true)
public class TodoTaskRoutineDatesConverter
    implements AttributeConverter<TodoTaskRoutineDates, String> {
    
    @Override
    public String convertToDatabaseColumn(TodoTaskRoutineDates todoTaskRoutineDates) {
        StringBuilder sb = new StringBuilder();
        todoTaskRoutineDates.getDates().forEach(date -> {
            sb.append(date.toString());
            sb.append("/");
        });
        sb.deleteCharAt(sb.length() - 1);
        
        return sb.toString();
    }
    
    @Override
    public TodoTaskRoutineDates convertToEntityAttribute(String s) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        Set<LocalDate> dates = Arrays.stream(s.split("/"))
                                     .map(date -> LocalDate.parse(date, formatter))
                                     .collect(Collectors.toSet());
        
        return TodoTaskRoutineDates.from(dates);
    }
}