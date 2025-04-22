package com.LetMeDoWith.LetMeDoWith.common.util;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import lombok.experimental.UtilityClass;

@UtilityClass
public class SystemTimeUtil {
    
    private static Clock clock = Clock.systemDefaultZone();
    
    public static void setClock(Clock clock) {
        if (!ProfileHolder.activeProfile.equals("test")) {
            throw new IllegalStateException("테스트 환경에서만 사용 가능합니다.");
        }
        SystemTimeUtil.clock = clock;
    }
    
    public static void resetClock() {
        clock = Clock.systemDefaultZone();
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
