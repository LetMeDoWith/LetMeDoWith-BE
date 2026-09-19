package com.LetMeDoWith.LetMeDoWith.application.notification.deeplink;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import lombok.experimental.UtilityClass;

@UtilityClass
public class DeepLinkFactory {

    private static final String SCHEME = "letmedowith://";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;

    public static String home(LocalDate date) {
        return SCHEME + "home?date=" + date.format(DATE_FORMATTER);
    }
}
