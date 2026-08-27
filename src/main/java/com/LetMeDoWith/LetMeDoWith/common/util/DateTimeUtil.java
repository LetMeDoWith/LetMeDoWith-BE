package com.LetMeDoWith.LetMeDoWith.common.util;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
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

    public static DateTimeFormatter getLocalTimeFormatter() {
        return DateTimeFormatter.ofPattern(TIME_FORMAT);
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

    public static boolean isLastDayOfWeekAt(LocalDateTime targetDateTime, DayOfWeek dayOfWeek, int hour) {
        if (targetDateTime.getDayOfWeek() != dayOfWeek) {
            return false;
        }
        if (targetDateTime.toLocalTime().getHour() != hour) {
            return false;
        }
        return targetDateTime.toLocalDate().plusWeeks(1).getMonth() != targetDateTime.getMonth();
    }

    public static LocalDate earlier(LocalDate date1, LocalDate date2) {
        return date1.isBefore(date2) ? date1 : date2;
    }

    public static LocalDate later(LocalDate date1, LocalDate date2) {
        return date1.isAfter(date2) ? date1 : date2;
    }
}
