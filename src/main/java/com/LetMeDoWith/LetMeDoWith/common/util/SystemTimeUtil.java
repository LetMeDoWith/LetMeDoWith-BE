package com.LetMeDoWith.LetMeDoWith.common.util;

import com.LetMeDoWith.LetMeDoWith.common.holders.TimeZoneContextHolder;
import lombok.experimental.UtilityClass;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@UtilityClass
public class SystemTimeUtil {

    private static Clock clock = Clock.system(TimeZoneContextHolder.getTimeZone());

    public static void setClock(Clock clock) {
        if (!ProfileHolder.activeProfile.equals("test")) {
            throw new IllegalStateException("테스트 환경에서만 사용 가능합니다.");
        }
        SystemTimeUtil.clock = clock;
    }

    public static void resetClock() {
        clock = Clock.system(TimeZoneContextHolder.getTimeZone());
    }

    public static LocalDateTime now() {
        return LocalDateTime.now(clock);
    }

    public static LocalDate nowDate() {
        return LocalDate.now(clock);
    }

    public static LocalTime nowTime() {
        return LocalTime.now(clock);
    }
}
