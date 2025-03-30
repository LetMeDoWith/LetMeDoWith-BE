package com.LetMeDoWith.LetMeDoWith.common.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.UtilityClass;

@UtilityClass
public class DateTimeUtil {
    
    private static final String DATE_TIME_FORMAT = "yyyy-MM-d'T'HH:mm:ss";
    private static final String DATE_FORMAT = "yyyy-MM-dd";
    private static final String TIME_FORMAT = "kk:mm:ss";
    
    public static String toFormatString(LocalDateTime dateTime) {
        return DateTimeFormatter.ofPattern(DATE_TIME_FORMAT).format(dateTime);
    }
    
    public static String toFormatString(LocalDate date) {
        return DateTimeFormatter.ofPattern(DATE_FORMAT).format(date);
    }
    
    public static String toFormatString(LocalTime time) {
        return DateTimeFormatter.ofPattern(TIME_FORMAT).format(time);
    }
    
    public static boolean isAfterOrEqual(LocalDate targetDate, LocalDate standardDate) {
        return targetDate.isAfter(standardDate) || targetDate.isEqual(standardDate);
    }
    
    public static boolean isBefore(LocalDate targetDate, LocalDate standardDate) {
        return targetDate.isBefore(standardDate);
    }
    
    public static boolean isBeforeOrEqual(LocalDate targetDate, LocalDate standardDate) {
        return targetDate.isBefore(standardDate) || targetDate.isEqual(standardDate);
    }
    
    public static boolean isBefore(LocalTime targetTime, LocalTime standardTime) {
        return targetTime.isBefore(standardTime);
    }
    
    public static DateDifferences getDifferences(Set<LocalDate> leftDates,
                                                 Set<LocalDate> rightDates) {
        Set<LocalDate> commonDates = leftDates.stream()
                                              .filter(rightDates::contains)
                                              .collect(Collectors.toSet());
        
        Set<LocalDate> leftOnlyDates = leftDates.stream()
                                                .filter(date -> !rightDates.contains(date))
                                                .collect(Collectors.toSet());
        
        Set<LocalDate> rightOnlyDates = rightDates.stream()
                                                  .filter(date -> !leftDates.contains(date))
                                                  .collect(Collectors.toSet());
        
        return new DateDifferences(commonDates, leftOnlyDates, rightOnlyDates);
    }
    
    @AllArgsConstructor
    @Getter
    public static class DateDifferences {
        
        final Set<LocalDate> commonDates;
        final Set<LocalDate> leftOnlyDates;
        final Set<LocalDate> rightOnlyDates;
    }
    
}
